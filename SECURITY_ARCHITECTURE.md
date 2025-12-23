# 📋 Arquitectura de Seguridad: UserDetails + JWT

## 1. Agregar dependencias (en el module que uses JWT)

Primero, necesitas agregar las librerías de seguridad y JWT:

```gradle
dependencies {
    // Spring Security
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'org.springframework.boot:spring-boot-starter-webflux'
    
    // JWT
    implementation 'io.jsonwebtoken:jjwt-api:0.12.3'
    implementation 'io.jsonwebtoken:jjwt-impl:0.12.3'
    implementation 'io.jsonwebtoken:jjwt-jackson:0.12.3'
}
```

## 2. Domain Layer - Puertos y Entidades

### UserDetails personalizado (Domain):

```java
// domain/model/src/main/java/co/example/security/User.java
public class User {
    private Long id;
    private String username;
    private String email;
    private String password;
    private List<Role> roles;
    private LocalDateTime createdAt;
    
    // Constructor, getters, setters
}

// domain/model/src/main/java/co/example/security/Role.java
public class Role {
    private Long id;
    private String name;
    private String description;
}
```

### Puertos (Interfaces):

```java
// domain/usecase/src/main/java/co/example/security/port/LoadUserPort.java
public interface LoadUserPort {
    Mono<User> findByUsername(String username);
    Mono<User> findById(Long id);
    Mono<User> findByEmail(String email);
}

// domain/usecase/src/main/java/co/example/security/port/JwtPort.java
public interface JwtPort {
    String generateToken(User user);
    Mono<User> validateAndExtractUser(String token);
}
```

## 3. Use Cases

```java
// domain/usecase/src/main/java/co/example/security/usecase/AuthenticateUserUseCase.java
@RequiredArgsConstructor
public class AuthenticateUserUseCase {
    private final LoadUserPort loadUserPort;
    private final JwtPort jwtPort;
    
    public Mono<String> execute(String username, String password) {
        return loadUserPort.findByUsername(username)
            .filter(user -> user.getPassword().equals(password)) // En realidad, usa BCrypt
            .map(jwtPort::generateToken)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Invalid credentials")));
    }
}
```

## 4. Infrastructure Layer - UserDetailsService

```java
// infrastructure/driven-adapters/r2dbc-postgresql/src/main/java/.../security/UserDetailsServiceImpl.java
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements ReactiveUserDetailsService {
    private final UserRepositoryR2dbc userRepository;
    private final RoleRepositoryR2dbc roleRepository;
    
    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByUsername(username)
            .flatMap(userEntity -> 
                roleRepository.findRolesByUserId(userEntity.getId())
                    .collectList()
                    .map(roles -> mapToUserDetails(userEntity, roles))
            );
    }
    
    private UserDetails mapToUserDetails(UserEntity userEntity, List<RoleEntity> roles) {
        var authorities = roles.stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
            .collect(Collectors.toList());
            
        return User.builder()
            .username(userEntity.getUsername())
            .password(userEntity.getPasswordHash())
            .authorities(authorities)
            .accountExpired(false)
            .accountLocked(false)
            .credentialsExpired(false)
            .disabled(false)
            .build();
    }
}
```

## 5. JWT Provider (Implementación del Puerto)

```java
// infrastructure/driven-adapters/.../security/JwtProviderImpl.java
@Component
@RequiredArgsConstructor
public class JwtProviderImpl implements JwtPort {
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.expiration}")
    private long jwtExpiration;
    
    private final LoadUserPort loadUserPort;
    
    @Override
    public String generateToken(User user) {
        var claims = new HashMap<String, Object>();
        claims.put("roles", user.getRoles().stream()
            .map(Role::getName)
            .collect(Collectors.toList()));
            
        return Jwts.builder()
            .claims(claims)
            .subject(user.getUsername())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }
    
    @Override
    public Mono<User> validateAndExtractUser(String token) {
        return Mono.fromCallable(() -> {
            var claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
                
            String username = claims.getSubject();
            return username;
        })
        .flatMap(loadUserPort::findByUsername)
        .onErrorResume(e -> Mono.error(new JwtException("Invalid token")));
    }
    
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
```

## 6. Security Configuration

```java
// infrastructure/entry-points/.../config/SecurityConfig.java
@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .csrf(csrf -> csrf.disable())
            .authorizeExchange(authorize -> authorize
                .pathMatchers("/auth/login", "/auth/register").permitAll()
                .pathMatchers("/admin/**").hasRole("ADMIN")
                .pathMatchers("/user/**").hasRole("USER")
                .anyExchange().authenticated()
            )
            .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

## 7. JWT Authentication Filter

```java
// infrastructure/entry-points/.../filter/JwtAuthenticationFilter.java
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {
    private final JwtPort jwtPort;
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String token = extractToken(exchange.getRequest());
        
        if (token == null) {
            return chain.filter(exchange);
        }
        
        return jwtPort.validateAndExtractUser(token)
            .flatMap(user -> {
                var authentication = new UsernamePasswordAuthenticationToken(
                    user.getUsername(),
                    null,
                    extractAuthorities(user)
                );
                return chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
            })
            .onErrorResume(e -> {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().writeWith(Mono.empty());
            });
    }
    
    private String extractToken(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
    
    private Collection<GrantedAuthority> extractAuthorities(User user) {
        return user.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
            .collect(Collectors.toList());
    }
}
```

## 8. Controller (Entry Point)

```java
// infrastructure/entry-points/.../controller/AuthController.java
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticateUserUseCase authenticateUserUseCase;
    
    @PostMapping("/login")
    public Mono<ResponseEntity<LoginResponse>> login(@RequestBody LoginRequest request) {
        return authenticateUserUseCase.execute(request.username(), request.password())
            .map(token -> ResponseEntity.ok(new LoginResponse(token)))
            .onErrorReturn(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
}
```

## Resumen de flujo:

```
1. Login → AuthController
   ↓
2. AuthenticateUserUseCase busca usuario
   ↓
3. LoadUserPort (R2DBC) obtiene User + Roles de BD
   ↓
4. JwtPort genera token con roles
   ↓
5. Siguiente request → JwtAuthenticationFilter
   ↓
6. Valida token y extrae User
   ↓
7. SecurityContext con authorities
```

## Configuración adicional en application.properties:

```properties
jwt.secret=your-secret-key-base64-encoded
jwt.expiration=86400000
```

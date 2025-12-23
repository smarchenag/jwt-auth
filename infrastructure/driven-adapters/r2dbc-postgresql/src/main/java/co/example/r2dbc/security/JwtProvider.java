package co.example.r2dbc.security;

import co.example.model.usuario.Rol;
import co.example.model.usuario.Usuario;
import co.example.model.usuario.gateways.JwtGateway;
import co.example.model.usuario.gateways.UsuarioRepository;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtProvider implements JwtGateway {

    private final UsuarioRepository usuarioRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Override
    public String generateToken(Usuario usuario) {
        var claims = new HashMap<String, Object>();
        if (usuario.getRoles() != null && !usuario.getRoles().isEmpty()) {
            claims.put("roles", usuario.getRoles().stream()
                    .map(Rol::getName)
                    .collect(Collectors.toList()));
        } else {
            claims.put("roles", java.util.Collections.emptyList());
        }
        return Jwts.builder()
                .claims(claims)
                .subject(usuario.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public Mono<Usuario> validarYExtraerUsuario(String token) {
        return Mono.fromCallable(() -> {
                    var claims = Jwts.parser()
                            .verifyWith(getSigningKey())
                            .build()
                            .parseSignedClaims(token)
                            .getPayload();

                    String username = claims.getSubject();
                    return username;
                })
                .flatMap(usuarioRepository::getUsuarioByEmail)
                .onErrorResume(e -> Mono.error(new JwtException("Invalid token")));
    }
}

package co.example.r2dbc.security;

import co.example.r2dbc.entity.RolEntity;
import co.example.r2dbc.entity.UsuarioEntity;
import co.example.r2dbc.repository.RolAdapterRepository;
import co.example.r2dbc.repository.UsuarioAdapterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements ReactiveUserDetailsService {

    private final UsuarioAdapterRepository usuarioAdapterRepository;
    private final RolAdapterRepository rolAdapterRepository;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return usuarioAdapterRepository.findUserByEmail(username)
                .flatMap(usuarioEntity -> rolAdapterRepository.findUserRoles(usuarioEntity.getIdUsuario())
                        .collectList()
                        .map(roles -> mapToUserDetails(usuarioEntity, roles)));
    }

    private UserDetails mapToUserDetails(UsuarioEntity userEntity, List<RolEntity> roles) {
        var authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toList());

        return User.builder()
                .username(userEntity.getEmail())
                .password(userEntity.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}

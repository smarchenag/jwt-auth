package co.example.r2dbc.adapter;

import co.example.model.usuario.Rol;
import co.example.model.usuario.Usuario;
import co.example.model.usuario.gateways.UsuarioRepository;
import co.example.r2dbc.entity.UsuarioEntity;
import co.example.r2dbc.repository.RolAdapterRepository;
import co.example.r2dbc.repository.UsuarioAdapterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.HashSet;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class UsuarioAdapter implements UsuarioRepository {

    private final UsuarioAdapterRepository usuarioAdapterRepository;
    private final RolAdapterRepository rolAdapterRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Mono<Usuario> getUsuarioByEmail(String email) {
        return usuarioAdapterRepository.findUserByEmail(email)
                .flatMap(this::toModelWithRoles);
    }

    @Override
    public Mono<Usuario> guardarUsuario(Usuario usuario) {
        UsuarioEntity entity = UsuarioEntity.builder()
                .name(usuario.getName())
                .email(usuario.getEmail())
                .password(usuario.getPassword())
                .build();
        return usuarioAdapterRepository.save(entity)
                .flatMap(this::toModelWithRoles);
    }

    private Mono<Usuario> toModelWithRoles(UsuarioEntity entity) {
        return rolAdapterRepository.findUserRoles(entity.getIdUsuario())
                .map(rolEntity -> new Rol(
                        rolEntity.getId(),
                        rolEntity.getName(),
                        rolEntity.getDescripcion()
                ))
                .collect(() -> new HashSet<Rol>(), Set::add)
                .map(roles -> Usuario.builder()
                        .name(entity.getName())
                        .email(entity.getEmail())
                        .password(entity.getPassword())
                        .roles(roles)
                        .build());
    }
}

package co.example.usecase.usuario;

import co.example.model.usuario.Usuario;
import co.example.model.usuario.gateways.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RegistroUseCase {
    private final UsuarioRepository usuarioRepository;

    public Mono<Usuario> registrarUsuario(String nombre, String email, String passwordEncriptada) {
        return usuarioRepository.getUsuarioByEmail(email)
                .hasElement()
                .flatMap(existe -> {
                    if (existe) {
                        return Mono.error(new IllegalArgumentException("El email ya está registrado"));
                    }
                    Usuario nuevoUsuario = Usuario.builder()
                            .name(nombre)
                            .email(email)
                            .password(passwordEncriptada)
                            .build();
                    return usuarioRepository.guardarUsuario(nuevoUsuario);
                });
    }
}

package co.example.r2dbc.adapter;

import co.example.model.usuario.gateways.PasswordGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordAdapter implements PasswordGateway {
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean validarPassword(String passwordPlain, String passwordEncriptada) {
        return passwordEncoder.matches(passwordPlain, passwordEncriptada);
    }
}

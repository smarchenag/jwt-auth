package co.example.model.usuario.gateways;

public interface PasswordGateway {
    boolean validarPassword(String passwordPlain, String passwordEncriptada);
}

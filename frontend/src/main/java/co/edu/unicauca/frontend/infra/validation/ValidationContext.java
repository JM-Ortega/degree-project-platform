package co.edu.unicauca.frontend.infra.validation;

/**
 * Contexto con los datos a validar en el login.
 */
public record ValidationContext(String email, String password, String rol) {
}

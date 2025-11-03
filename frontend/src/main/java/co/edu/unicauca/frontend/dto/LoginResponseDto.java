package co.edu.unicauca.frontend.dto;

/**
 * Respuesta del backend tras un inicio de sesión exitoso.
 *
 * Contiene la información de sesión (email, nombre visible, rol)
 * y el token simbólico devuelto por el servicio de autenticación.
 *
 * Esta clase está pensada para el FRONT (JavaFX), por eso no
 * incluye anotaciones de Swagger.
 */
public record LoginResponseDto(
        SessionInfo session,
        String token
) { }

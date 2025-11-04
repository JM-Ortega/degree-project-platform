package co.edu.unicauca.authservice.dto;

import co.edu.unicauca.shared.contracts.dto.SessionInfo;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Representa la respuesta de inicio de sesión.
 *
 * <p>
 * Incluye la información básica de la sesión activa
 * (correo, nombre visible y rol actual) junto con
 * el token o identificador de sesión.
 * </p>
 *
 * <p>
 * Este diseño permite mantener un contrato uniforme entre
 * el microservicio Auth, el front-end y los demás microservicios
 * que consuman información de sesión.
 * </p>
 */
@Schema(description = "Respuesta devuelta tras un inicio de sesión exitoso.")
public record LoginResponse(

        @Schema(description = "Información de sesión del usuario autenticado.")
        SessionInfo session,

        @Schema(description = "Token o cadena de sesión. En esta entrega es simbólico.")
        String token

) { }

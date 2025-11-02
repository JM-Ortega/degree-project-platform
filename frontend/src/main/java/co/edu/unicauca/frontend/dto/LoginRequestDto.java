package co.edu.unicauca.frontend.dto;

import co.edu.unicauca.frontend.entities.enums.Rol;

/**
 * DTO utilizado por el frontend para enviar las credenciales
 * de inicio de sesión al microservicio Auth.
 */
public record LoginRequestDto(
        String email,
        String password,
        Rol rol
) { }

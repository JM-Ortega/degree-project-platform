package co.edu.unicauca.frontend.dto;


import co.edu.unicauca.frontend.entities.enums.Rol;

/**
 * Representa la información mínima del usuario autenticado
 * que puede compartirse entre microservicios o usarse en
 * el front-end para mantener la sesión activa.
 *
 * Este DTO no expone datos sensibles (como contraseña).
 */
public record SessionInfo(
        String email,       // correo institucional
        String nombres,     // nombre completo o visible
        Rol rolActivo       // rol con el que inició sesión
) { }

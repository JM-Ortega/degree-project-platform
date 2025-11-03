package co.edu.unicauca.shared.contracts.dto;

import co.edu.unicauca.shared.contracts.model.Rol;

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

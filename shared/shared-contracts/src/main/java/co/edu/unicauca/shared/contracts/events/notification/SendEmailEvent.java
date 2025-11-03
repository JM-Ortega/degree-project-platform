package co.edu.unicauca.shared.contracts.events.notification;

import java.util.List;
import java.util.Map;

/**
 * Evento estándar para solicitar el envío de un correo electrónico
 * al microservicio de notificaciones.
 *
 * <p>
 * Puede ser publicado por cualquier microservicio cuando se requiera
 * notificar a uno o varios destinatarios.
 * </p>
 */
public record SendEmailEvent(
        String from,                  // Remitente (opcional)
        List<String> to,              // Uno o varios destinatarios
        String subject,               // Asunto del correo
        String template,              // Nombre del template (ej: "user.created")
        String body,                  // Texto plano (si no hay template)
        Map<String, Object> variables // Variables para reemplazar en la plantilla
) { }

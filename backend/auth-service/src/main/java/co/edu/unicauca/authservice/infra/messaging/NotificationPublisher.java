package co.edu.unicauca.authservice.infra.messaging;

import co.edu.unicauca.shared.contracts.events.notification.NotificationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Component
public class NotificationPublisher {

    private static final Logger log = LoggerFactory.getLogger(NotificationPublisher.class);

    private final RabbitTemplate rabbitTemplate;
    private final String exchangeName;

    public NotificationPublisher(
            RabbitTemplate rabbitTemplate,
            @Value("${messaging.exchange.main}") String exchangeName
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchangeName = exchangeName;
    }

    /**
     * Publica una notificación genérica hacia notification-service.
     * @param type    Ej: "auth.user.created" (se enviará a la ruta "notification.send.auth.user.created")
     * @param toEmails lista de destinatarios por email
     * @param toPhones lista de celulares (puede ir vacía)
     * @param subject asunto del correo
     * @param message cuerpo del mensaje
     */
    public void publishNotification(String type,
                                    List<String> toEmails,
                                    List<String> toPhones,
                                    String subject,
                                    String message) {
        NotificationEvent event = new NotificationEvent(
                type,
                toEmails,
                subject,
                message,
                toPhones,
                OffsetDateTime.now(ZoneOffset.UTC)
        );

        String routingKey = "notification.send." + type; // p.ej. notification.send.auth.user.created

        try {
            rabbitTemplate.convertAndSend(exchangeName, routingKey, event);
            log.info("Evento de notificación publicado: {} -> {}", routingKey, event);
        } catch (Exception ex) {
            log.error("Error al publicar evento {}: {}", routingKey, ex.getMessage(), ex);
        }
    }
}

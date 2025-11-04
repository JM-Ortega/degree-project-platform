package co.edu.unicauca.departmentheadservice.infra.messaging;

import co.edu.unicauca.shared.contracts.events.notification.NotificationEvent;
import co.edu.unicauca.shared.contracts.messaging.RoutingKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Publica mensajes hacia el microservicio de notificaciones.
 *
 * <p>Este componente permite enviar eventos que indican al micro de
 * notificaciones que debe procesar un correo electrónico, por ejemplo,
 * para dar la bienvenida a un nuevo usuario o informar sobre una acción
 * del sistema.</p>
 *
 * <p>Utiliza la clave de enrutamiento {@link RoutingKeys#NOTIFICATION_SEND}
 * y el contrato de evento {@link NotificationEvent} definido en el módulo
 * <code>shared-contracts</code>.</p>
 */
@Component
public class NotificationPublisher {

    private static final Logger log = LoggerFactory.getLogger(NotificationPublisher.class);

    private final RabbitTemplate rabbitTemplate;
    private final String exchangeName;

    /**
     * Inyección por constructor: garantiza inmutabilidad y facilita las pruebas.
     *
     * @param rabbitTemplate plantilla de RabbitMQ utilizada para enviar los mensajes
     * @param exchangeName nombre del exchange principal configurado en el YAML
     */
    public NotificationPublisher(
            RabbitTemplate rabbitTemplate,
            @Value("${messaging.exchange.main}") String exchangeName
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchangeName = exchangeName;
    }

    /**
     * Envía un mensaje de correo electrónico al microservicio de notificaciones.
     *
     * @param event evento {@link NotificationEvent} con la información del mensaje a enviar
     */
    public void publishEmail(NotificationEvent event) {
        try {
            rabbitTemplate.convertAndSend(exchangeName, RoutingKeys.NOTIFICATION_SEND, event);
            log.info("Evento de notificación publicado: {} -> {}", RoutingKeys.NOTIFICATION_SEND, event);
        } catch (Exception ex) {
            log.error("Error al publicar evento {}: {}", RoutingKeys.NOTIFICATION_SEND, ex.getMessage(), ex);
        }
    }
}

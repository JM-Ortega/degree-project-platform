package co.edu.unicauca.authservice.infra.messaging;

import co.edu.unicauca.shared.contracts.events.auth.UserCreatedEvent;
import co.edu.unicauca.shared.contracts.messaging.RoutingKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Publica eventos de dominio relacionados con usuarios en RabbitMQ.
 *
 * <p>Este componente envía mensajes al exchange principal cuando ocurre un
 * evento relevante dentro del microservicio Auth, como la creación de un usuario.</p>
 *
 * <p>Utiliza las claves de enrutamiento definidas en {@link RoutingKeys} y
 * los contratos compartidos del módulo <code>shared-contracts</code>.</p>
 *
 * <p>Ejemplo de uso:</p>
 * <pre>
 *   userEventsPublisher.publishUserCreatedEvent(new UserCreatedEvent(...));
 * </pre>
 *
 */
@Component
public class UserEventsPublisher {

    private static final Logger log = LoggerFactory.getLogger(UserEventsPublisher.class);

    private final RabbitTemplate rabbitTemplate;
    private final String exchangeName;

    /**
     * Inyección por constructor: garantiza inmutabilidad y facilita pruebas unitarias.
     *
     * @param rabbitTemplate plantilla de RabbitMQ para publicar mensajes.
     * @param exchangeName nombre del exchange principal, obtenido del archivo YAML.
     */
    public UserEventsPublisher(
            RabbitTemplate rabbitTemplate,
            @Value("${messaging.exchange.main}") String exchangeName
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchangeName = exchangeName;
    }

    /**
     * Publica un evento de creación de usuario en el exchange principal.
     *
     * @param event evento {@link UserCreatedEvent} con los datos del usuario creado.
     */
    public void publishUserCreatedEvent(UserCreatedEvent event) {
        try {
            rabbitTemplate.convertAndSend(exchangeName, RoutingKeys.AUTH_USER_CREATED, event);
            log.info("Evento publicado: {} -> {}", RoutingKeys.AUTH_USER_CREATED, event);
        } catch (Exception ex) {
            log.error("Error al publicar evento {}: {}", RoutingKeys.AUTH_USER_CREATED, ex.getMessage(), ex);
        }
    }
}

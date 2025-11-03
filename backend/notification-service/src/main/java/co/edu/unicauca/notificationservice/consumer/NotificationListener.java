package co.edu.unicauca.notificationservice.consumer;


import co.edu.unicauca.notificationservice.sender.NotificationSender;
import co.edu.unicauca.shared.contracts.events.notification.NotificationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Consumer de eventos de notificación.
 * Decide el canal de envío (solo correo o correo+SMS) según la presencia de teléfonos.
 */
@Slf4j
@Component
public class NotificationListener {

    private final NotificationSender emailNotificationSender; // solo correo
    private final NotificationSender smsNotificationSender;   // correo + SMS

    /**
     * Inyección explícita de beans calificados.
     *
     * @param emailNotificationSender bean base (solo correo)
     * @param smsNotificationSender   bean decorado (correo + SMS)
     */
    public NotificationListener(
            @Qualifier("emailNotificationSender") NotificationSender emailNotificationSender,
            @Qualifier("smsNotificationSender") NotificationSender smsNotificationSender) {
        this.emailNotificationSender = emailNotificationSender;
        this.smsNotificationSender = smsNotificationSender;
    }

    /**
     * Procesa eventos de notificación recibidos desde la cola AMQP.
     * Si existen teléfonos, utiliza el sender decorado (correo + SMS); de lo contrario, solo correo.
     *
     * @param event evento de notificación deserializado desde el mensaje AMQP
     */
    @RabbitListener(queues = "${messaging.queues.notification}")
    public void handleNotification(NotificationEvent event) {
        if (event == null) {
            log.warn("Evento de notificación nulo recibido; se descarta.");
            return;
        }

        log.info("""
                        📬 Nueva notificación:
                        ├─ Tipo: {}
                        ├─ Destinatarios: {}
                        └─ Mensaje: {}
                        """, event.getType(),
                String.join(", ", event.getRecipientEmails()),
                event.getMessage());

        try {
            boolean hasPhones = event.getRecipientPhones() != null && !event.getRecipientPhones().isEmpty();
            NotificationSender sender = hasPhones ? smsNotificationSender : emailNotificationSender;
            sender.send(event);
        } catch (Exception e) {
            log.error("❌ Error al procesar notificación: {}", e.getMessage(), e);
            throw new AmqpRejectAndDontRequeueException(e);
        }
    }
}

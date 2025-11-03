package co.edu.unicauca.notificationservice.consumer;


import co.edu.unicauca.notificationservice.sender.NotificationSender;
import co.edu.unicauca.notificationservice.sender.SmsNotificationDecorator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import co.edu.unicauca.notificationservice.model.NotificationEvent;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationListener {

    // Inyectamos el bean base (correo)
    private final NotificationSender emailNotificationSender;

    @RabbitListener(queues = "${messaging.queues.notification}")
    public void handleNotification(NotificationEvent event) {
        log.info("""
        
        📬 Nueva notificación recibida:
        ├── Tipo: {}
        ├── Destinatarios: {}
        └── Mensaje: {}
        """, event.getType(), String.join(", ", event.getRecipientEmails()), event.getMessage());

        try {
            // 🔹 Si hay teléfonos, agregamos el decorador SMS
            NotificationSender senderToUse = event.getRecipientPhones() != null && !event.getRecipientPhones().isEmpty()
                    ? new SmsNotificationDecorator(emailNotificationSender)
                    : emailNotificationSender;

            senderToUse.send(event);

        } catch (Exception e) {
            log.error("❌ Error al procesar notificación: {}", e.getMessage());
            throw new AmqpRejectAndDontRequeueException(e);
        }
    }
}


package co.edu.unicauca.notificationservice.consumer;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import co.edu.unicauca.notificationservice.model.NotificationEvent;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import co.edu.unicauca.notificationservice.service.NotificationService;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationListener {

    private final NotificationService notificationService;

    @RabbitListener(queues = "${messaging.queues.notification}")
    public void handleNotification(NotificationEvent event) {
        log.info("""
        
        📬 Nuevo correo:
        ├── Para: {}
        ├── Asunto: {}
        └── Mensaje: {}
        """, String.join(", ", event.getRecipientEmails()), event.getSubject(), event.getMessage());
        try {
            notificationService.processNotification(event);
        } catch (Exception e) {
            log.error("Error al procesar notificación: {}", e.getMessage());
            throw new AmqpRejectAndDontRequeueException(e);
        }
    }
}


package co.edu.unicauca.notificationservice.sender;

import co.edu.unicauca.notificationservice.model.NotificationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmailNotificationSender implements NotificationSender {

    @Override
    public void send(NotificationEvent event) {
        log.info("""
        ✉️  Enviando correo electrónico
        ├── Para: {}
        ├── Asunto: {}
        └── Mensaje: {}
        """, String.join(", ", event.getRecipientEmails()), event.getSubject(), event.getMessage());
    }
}

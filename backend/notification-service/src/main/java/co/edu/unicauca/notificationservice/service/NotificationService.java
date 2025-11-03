package co.edu.unicauca.notificationservice.service;

import co.edu.unicauca.notificationservice.sender.EmailNotificationSender;
import co.edu.unicauca.notificationservice.sender.NotificationSender;
import co.edu.unicauca.notificationservice.sender.SmsNotificationDecorator;
import lombok.extern.slf4j.Slf4j;
import co.edu.unicauca.notificationservice.model.NotificationEvent;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class NotificationService {

    public void processNotification(NotificationEvent event) {
        log.info("📨 Recibida notificación tipo: {}", event.getType());

        // Siempre enviar por email
        NotificationSender sender = new EmailNotificationSender();

        // Si el evento trae números de teléfono, agregar SMS
        if (event.getRecipientPhones() != null && !event.getRecipientPhones().isEmpty()) {
            sender = new SmsNotificationDecorator(sender);
        }

        sender.send(event);
    }
}


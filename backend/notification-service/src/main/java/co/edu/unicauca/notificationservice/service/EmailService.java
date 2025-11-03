package co.edu.unicauca.notificationservice.service;

import co.edu.unicauca.notificationservice.model.NotificationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    public void sendEmail(NotificationEvent event) {
        for (String email : event.getRecipientEmails()) {
            log.info("📩 Enviando correo a: {} | Asunto: {} | Mensaje: {}",
                    email, event.getSubject(), event.getMessage());
        }
        // Aquí más adelante podrías integrar un proveedor real (JavaMail, SendGrid, etc.)
    }
}
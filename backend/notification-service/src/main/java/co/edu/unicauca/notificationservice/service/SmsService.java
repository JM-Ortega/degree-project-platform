package co.edu.unicauca.notificationservice.service;

import co.edu.unicauca.notificationservice.model.NotificationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SmsService {

    public void sendSms(NotificationEvent event) {
        log.info("📱 Enviando SMS:");
        log.info("├── Para: {}", String.join(", ", event.getRecipientPhones()));
        log.info("└── Mensaje: {}", event.getMessage());
        // Aquí iría la integración real con Twilio, Nexmo, etc.
    }
}

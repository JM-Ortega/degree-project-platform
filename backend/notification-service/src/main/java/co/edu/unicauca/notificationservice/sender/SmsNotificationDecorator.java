package co.edu.unicauca.notificationservice.sender;

import co.edu.unicauca.notificationservice.model.NotificationEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SmsNotificationDecorator implements NotificationSender {

    private final NotificationSender wrapped;

    public SmsNotificationDecorator(NotificationSender wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void send(NotificationEvent event) {
        // Primero, envío base (correo)
        wrapped.send(event);

        // Luego, SMS (decorador)
        if (event.getRecipientPhones() != null && !event.getRecipientPhones().isEmpty()) {
            for (String phone : event.getRecipientPhones()) {
                log.info("""
                
                📱 Enviando SMS
                ├── A: {}
                └── Mensaje: {}
                """, phone, event.getMessage());
            }
        } else {
            log.warn("⚠️ No se encontraron números de teléfono para enviar SMS.");
        }
    }
}

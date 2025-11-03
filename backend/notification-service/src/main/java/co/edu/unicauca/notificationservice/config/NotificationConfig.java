package co.edu.unicauca.notificationservice.config;

import co.edu.unicauca.notificationservice.sender.EmailNotificationSender;
import co.edu.unicauca.notificationservice.sender.NotificationSender;
import co.edu.unicauca.notificationservice.sender.SmsNotificationDecorator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationConfig {

    // Bean base: envío por correo
    @Bean
    public NotificationSender emailNotificationSender() {
        return new EmailNotificationSender();
    }

    // Bean decorado: correo + SMS
    @Bean
    public NotificationSender smsNotificationSender(NotificationSender emailNotificationSender) {
        return new SmsNotificationDecorator(emailNotificationSender);
    }
}

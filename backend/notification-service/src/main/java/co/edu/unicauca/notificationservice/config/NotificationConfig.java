package co.edu.unicauca.notificationservice.config;

import co.edu.unicauca.notificationservice.sender.EmailNotificationSender;
import co.edu.unicauca.notificationservice.sender.NotificationSender;
import co.edu.unicauca.notificationservice.sender.SmsNotificationDecorator;
import co.edu.unicauca.notificationservice.service.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class NotificationConfig {

    private final EmailNotificationSender emailSender;
    private final SmsService smsService;

    @Bean
    public NotificationSender notificationSender() {
        // Decorador: primero envía email, luego SMS
        return new SmsNotificationDecorator(emailSender, smsService);
    }
}

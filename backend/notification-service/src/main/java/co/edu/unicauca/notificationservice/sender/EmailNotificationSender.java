package co.edu.unicauca.notificationservice.sender;

import co.edu.unicauca.notificationservice.model.NotificationEvent;
import co.edu.unicauca.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class EmailNotificationSender implements NotificationSender {

    private final NotificationService notificationService;

    @Override
    public void send(NotificationEvent event) {
        notificationService.processNotification(event);
    }
}

package co.edu.unicauca.notificationservice.sender;

import co.edu.unicauca.shared.contracts.events.notification.NotificationEvent;

public interface NotificationSender {
    void send(NotificationEvent event);
}

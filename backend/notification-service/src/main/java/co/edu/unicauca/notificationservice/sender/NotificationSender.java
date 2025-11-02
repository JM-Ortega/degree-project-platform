package co.edu.unicauca.notificationservice.sender;

import co.edu.unicauca.notificationservice.model.NotificationEvent;

public interface NotificationSender {
    void send(NotificationEvent event);
}

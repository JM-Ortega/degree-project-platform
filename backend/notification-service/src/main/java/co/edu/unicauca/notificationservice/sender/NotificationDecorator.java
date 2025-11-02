package co.edu.unicauca.notificationservice.sender;

import co.edu.unicauca.notificationservice.model.NotificationEvent;

public abstract class NotificationDecorator implements NotificationSender {

    protected final NotificationSender wrappedSender;

    protected NotificationDecorator(NotificationSender wrappedSender) {
        this.wrappedSender = wrappedSender;
    }

    @Override
    public void send(NotificationEvent event) {
        wrappedSender.send(event); // Delegar al sender original
    }
}

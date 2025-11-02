package co.edu.unicauca.notificationservice.sender;

import co.edu.unicauca.notificationservice.model.NotificationEvent;
import co.edu.unicauca.notificationservice.service.SmsService;

public class SmsNotificationDecorator extends NotificationDecorator {

    private final SmsService smsService;

    public SmsNotificationDecorator(NotificationSender wrappedSender, SmsService smsService) {
        super(wrappedSender); // llama al constructor de la clase base
        this.smsService = smsService;
    }

    @Override
    public void send(NotificationEvent event) {
        super.send(event); // primero envía email
        smsService.sendSms(event); // luego envía SMS
    }
}

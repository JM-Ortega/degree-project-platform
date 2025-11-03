package co.edu.unicauca.notificationservice;

import co.edu.unicauca.notificationservice.consumer.NotificationListener;
import co.edu.unicauca.notificationservice.model.NotificationEvent;
import co.edu.unicauca.notificationservice.sender.EmailNotificationSender;
import co.edu.unicauca.notificationservice.sender.NotificationSender;
import co.edu.unicauca.notificationservice.sender.SmsNotificationDecorator;
import co.edu.unicauca.notificationservice.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;

public class NotificationListenerTest {

    private NotificationSender notificationSender;
    private NotificationListener listener;

    @BeforeEach
    void setup() {
        notificationSender = mock(NotificationSender.class);
        listener = new NotificationListener(notificationSender);
    }

    @Test
    void testHandleNotification_emailOnly() {
        NotificationEvent event = new NotificationEvent();
        event.setType("coordinator.formata.approved");
        event.setRecipientEmails(List.of("test@correo.com"));
        event.setRecipientPhones(List.of());
        event.setSubject("Formato A Aprobado");
        event.setMessage("El formato A fue aprobado correctamente.");
        event.setTimestamp(LocalDateTime.now());

        listener.handleNotification(event);

        verify(notificationSender, times(1)).send(event);
    }

    @Test
    void testHandleNotification_emailAndSms() {
        NotificationEvent event = new NotificationEvent();
        event.setType("coordinator.formata.rejected");
        event.setRecipientEmails(List.of("test@correo.com"));
        event.setRecipientPhones(List.of("3001234567"));
        event.setSubject("Formato A Rechazado");
        event.setMessage("El formato A fue rechazado.");
        event.setTimestamp(LocalDateTime.now());

        listener.handleNotification(event);

        verify(notificationSender, times(1)).send(event);
    }

    @Test
    void testNotificationDecoratorIntegration() {
        NotificationSender emailSender = mock(EmailNotificationSender.class);
        NotificationSender smsSender = new SmsNotificationDecorator(emailSender);

        NotificationEvent event = new NotificationEvent();
        event.setType("test.sms");
        event.setRecipientEmails(List.of("test@correo.com"));
        event.setRecipientPhones(List.of("3001234567"));
        event.setSubject("Prueba SMS");
        event.setMessage("Mensaje de prueba de SMS");
        event.setTimestamp(LocalDateTime.now());

        smsSender.send(event);

        ArgumentCaptor<NotificationEvent> captor = ArgumentCaptor.forClass(NotificationEvent.class);
        verify(emailSender, times(1)).send(captor.capture());
    }
}
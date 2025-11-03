package co.edu.unicauca.notificationservice.sender;

import co.edu.unicauca.shared.contracts.events.notification.NotificationEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * Decorador de {@link NotificationSender} que agrega funcionalidad
 * para el envío de mensajes SMS, además del envío base (correo electrónico).
 * <p>
 * Implementa el patrón de diseño <b>Decorator</b>, permitiendo extender el
 * comportamiento del componente sin modificar su estructura interna.
 */
@Slf4j
public class SmsNotificationDecorator implements NotificationSender {

    /**
     * Componente base decorado (por ejemplo, {@link EmailNotificationSender}).
     */
    private final NotificationSender wrapped;

    /**
     * Constructor que recibe el componente base a decorar.
     *
     * @param wrapped instancia del {@link NotificationSender} base.
     */
    public SmsNotificationDecorator(NotificationSender wrapped) {
        this.wrapped = wrapped;
    }

    /**
     * Envía una notificación combinando correo electrónico y SMS.
     * <ul>
     *     <li>Primero envía la notificación base (correo electrónico).</li>
     *     <li>Luego, si existen números telefónicos, envía un SMS a cada uno.</li>
     * </ul>
     *
     * @param event evento de notificación con los datos del mensaje y destinatarios.
     */
    @Override
    public void send(NotificationEvent event) {
        // Envío base (correo electrónico)
        wrapped.send(event);

        // Envío complementario por SMS
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

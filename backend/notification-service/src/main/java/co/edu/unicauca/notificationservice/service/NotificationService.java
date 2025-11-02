package co.edu.unicauca.notificationservice.service;

import lombok.extern.slf4j.Slf4j;
import co.edu.unicauca.notificationservice.model.NotificationEvent;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    public void processNotification(NotificationEvent event) {
        log.info("📨 Recibida notificación tipo: {}", event.getType());

        for (String email : event.getRecipientEmails()) {
            switch (event.getType()) {
                case "auth.user.created" -> log.info("📧 Bienvenido docente: {}", email);
                case "project.created" -> log.info("📧 Coordinador, nuevo Formato A recibido. Enviar a: {}", email);
                case "coordinator.formata.approved" -> log.info("📧 Proyecto aprobado. Notificar a: {}", email);
                case "coordinator.formata.rejected" -> log.info("📧 Proyecto rechazado. Notificar a: {}", email);
                case "department.proposal.approved" -> log.info("📧 Anteproyecto aprobado. Notificar a: {}", email);
                default -> log.warn("⚠️ Tipo de evento desconocido: {}", event.getType());
            }
        }
    }
}


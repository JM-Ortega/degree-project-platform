package co.edu.unicauca.notificationservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {
    private String type; // Ej: project.created, coordinator.formata.approved
    private List<String> recipientEmails;
    private String subject;
    private String message;
    private List<String> recipientPhones;
    private LocalDateTime timestamp;
}


package co.edu.unicauca.coordinatorservice.service;

import co.edu.unicauca.coordinatorservice.entity.Estudiante;
import co.edu.unicauca.coordinatorservice.infra.DTOS.EstadoFormatoA;
import co.edu.unicauca.coordinatorservice.entity.FormatoA;
import co.edu.unicauca.coordinatorservice.infra.DTOSInternos.NotificationEvent;
import co.edu.unicauca.coordinatorservice.repository.FormatoARepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class FormatoAService {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${messaging.exchange.main}")
    private String mainExchange;

    @Value("${messaging.routing.formatAApprovedByCoordinator}")
    private String routingKeyFormatAApproved;

    private final FormatoARepository formatoARepository;

    public FormatoAService(FormatoARepository formatoARepository, RabbitTemplate rabbitTemplate) {
        this.formatoARepository = formatoARepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    public FormatoA actualizarFormato(Long id, MultipartFile archivo, String nuevoEstado,  String nombreArchivo, String horaActual) throws IOException {
        FormatoA formato = formatoARepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Formato A no encontrado con id: " + id));

        if (formato.getNroVersion() > 3) {
            throw new IllegalStateException("No se pueden subir más de 3 versiones del Formato A para este proyecto.");
        }

        formato.setBlob(archivo.getBytes());
        formato.setNombreFormatoA(nombreArchivo);

        // Convertir la cadena a LocalDate
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDate fecha = LocalDate.parse(horaActual, formatter);
        formato.setFechaSubida(fecha);

        // Determinar estado
        String eventType;
        String subject;
        String message;

        if (nuevoEstado.equalsIgnoreCase("rechazado")) {
            formato.setEstadoFormatoA(EstadoFormatoA.OBSERVADO);
            eventType = "coordinator.formata.rejected";
            subject = "Formato A Rechazado";
            message = String.format("El Formato A del proyecto '%s' fue RECHAZADO por el coordinador.", formato.getNombreProyecto());
        } else {
            formato.setEstadoFormatoA(EstadoFormatoA.APROBADO);
            eventType = "coordinator.formata.approved";
            subject = "Formato A Aprobado";
            message = String.format("El Formato A del proyecto '%s' fue APROBADO por el coordinador.", formato.getNombreProyecto());
        }

        formatoARepository.save(formato);

        FormatoA dto = new FormatoA();
        dto.setId(formato.getId());
        dto.setProyectoId(formato.getId());
        dto.setNroVersion(formato.getNroVersion());
        dto.setNombreFormatoA(formato.getNombreFormatoA());
        dto.setFechaSubida(formato.getFechaSubida());
        dto.setBlob(formato.getBlob());
        dto.setEstadoFormatoA(formato.getEstadoFormatoA());

        // 1. Publicar el evento funcional para otros microservicios
        rabbitTemplate.convertAndSend(mainExchange, routingKeyFormatAApproved, dto);
        log.info("📤 Evento de actualización de Formato A enviado: {}", routingKeyFormatAApproved);

        // 2. Publicar el evento de notificación
        List<String> destinatarios = new ArrayList<>();
        List<String> celulares = new ArrayList<>();

        // Director
        destinatarios.add(formato.getDirector().getEmail());
        celulares.add(formato.getDirector().getCelular());

        if (formato.getCoodirector() != null && formato.getCoodirector().getEmail() != null){
            destinatarios.add(formato.getCoodirector().getEmail());
            celulares.add(formato.getCoodirector().getCelular());
        }

        for(Estudiante e : formato.getEstudiantes()){
            destinatarios.add(e.getEmail());
            celulares.add(e.getCelular());
        }

        NotificationEvent notificationEvent = new NotificationEvent(
                eventType,
                destinatarios,
                subject,
                message,
                celulares,
                LocalDateTime.now()
        );

        rabbitTemplate.convertAndSend(mainExchange, "notification.send." + eventType, notificationEvent);
        log.info("📨 Evento de notificación enviado a notificación-service: {}", notificationEvent);

        return formato;
    }
}

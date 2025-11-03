package co.edu.unicauca.coordinatorservice.service;

import co.edu.unicauca.coordinatorservice.entity.Estudiante;
import co.edu.unicauca.coordinatorservice.entity.FormatoA;
import co.edu.unicauca.coordinatorservice.infra.DTOS.EstadoFormatoA;
import co.edu.unicauca.coordinatorservice.repository.FormatoARepository;
import co.edu.unicauca.shared.contracts.events.notification.NotificationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class FormatoAService {

    private final RabbitTemplate rabbitTemplate;
    private final FormatoARepository formatoARepository;

    @Value("${messaging.exchange.main}")
    private String mainExchange;

    @Value("${messaging.routing.formatAApprovedByCoordinator}")
    private String routingKeyFormatAApproved;

    public FormatoAService(FormatoARepository formatoARepository, RabbitTemplate rabbitTemplate) {
        this.formatoARepository = formatoARepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Actualiza un Formato A existente, registra la versión y publica los eventos correspondientes.
     */
    public FormatoA actualizarFormato(Long id, MultipartFile archivo, String nuevoEstado,
                                      String nombreArchivo, String horaActual) throws IOException {

        // Buscar el Formato A por ID
        FormatoA formato = formatoARepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Formato A no encontrado con id: " + id));

        // Validar número máximo de versiones
        if (formato.getNroVersion() > 3) {
            throw new IllegalStateException("No se permiten más de 3 versiones del Formato A.");
        }

        // Actualizar archivo y metadatos
        formato.setBlob(archivo.getBytes());
        formato.setNombreFormatoA(nombreArchivo);

        // Registrar fecha y hora de carga
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime fechaHora = LocalDateTime.parse(horaActual, formatter);
        formato.setFechaSubida(fechaHora.toLocalDate());


        // Determinar estado y tipo de evento
        boolean rechazado = "rechazado".equalsIgnoreCase(nuevoEstado);
        String routingKeyFuncional = rechazado
                ? "coordinator.formata.rejected"
                : "coordinator.formata.approved";
        String subject = rechazado ? "Formato A Rechazado" : "Formato A Aprobado";
        String message = String.format("El Formato A del proyecto '%s' fue %s por el coordinador.",
                formato.getNombreProyecto(), rechazado ? "RECHAZADO" : "APROBADO");

        formato.setEstadoFormatoA(rechazado ? EstadoFormatoA.OBSERVADO : EstadoFormatoA.APROBADO);
        formatoARepository.save(formato);

        // Construir DTO liviano para publicar
        FormatoA dto = new FormatoA();
        dto.setId(formato.getId());
        dto.setProyectoId(formato.getId());
        dto.setNroVersion(formato.getNroVersion());
        dto.setNombreFormatoA(formato.getNombreFormatoA());
        dto.setFechaSubida(formato.getFechaSubida());
        dto.setEstadoFormatoA(formato.getEstadoFormatoA());

        // Publicar evento funcional
        log.info("Converter activo: {}", rabbitTemplate.getMessageConverter().getClass().getName());
        rabbitTemplate.convertAndSend(mainExchange, routingKeyFuncional, dto);
        log.info("📤 Evento funcional publicado: {}", routingKeyFuncional);

        // Construir destinatarios de notificación
        List<String> destinatarios = new ArrayList<>();
        List<String> celulares = new ArrayList<>();

        destinatarios.add(formato.getDirector().getEmail());
        celulares.add(formato.getDirector().getCelular());

        if (formato.getCoodirector() != null && formato.getCoodirector().getEmail() != null) {
            destinatarios.add(formato.getCoodirector().getEmail());
            celulares.add(formato.getCoodirector().getCelular());
        }

        for (Estudiante e : formato.getEstudiantes()) {
            destinatarios.add(e.getEmail());
            celulares.add(e.getCelular());
        }

        // Crear y publicar evento de notificación
        NotificationEvent notificationEvent = new NotificationEvent(
                routingKeyFuncional,
                destinatarios,
                subject,
                message,
                celulares,
                OffsetDateTime.now(ZoneOffset.UTC)
        );

        rabbitTemplate.convertAndSend(mainExchange, "notification.send." + routingKeyFuncional, notificationEvent);
        log.info("📨 Evento de notificación publicado: {}", routingKeyFuncional);

        return formato;
    }
}

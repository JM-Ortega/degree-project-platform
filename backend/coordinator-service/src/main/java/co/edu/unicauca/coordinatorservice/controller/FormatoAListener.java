package co.edu.unicauca.coordinatorservice.controller;

import co.edu.unicauca.coordinatorservice.entity.FormatoA;
import co.edu.unicauca.coordinatorservice.infra.FormatoADTO;
import co.edu.unicauca.coordinatorservice.repository.FormatoARepository;
import co.edu.unicauca.coordinatorservice.service.IntegrationService;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Optional;

@Component
public class FormatoAListener {
    private final FormatoARepository formatoARepository;

    public FormatoAListener(FormatoARepository formatoARepository) {
        this.formatoARepository = formatoARepository;
    }

    /**
     * Escucha mensajes provenientes de la cola del coordinador.
     * Estos mensajes llegan desde el microservicio de la fuente de la verdad
     * cuando se crea o actualiza un FormatoA o un Proyecto.
     */
    @RabbitListener(queues = "${messaging.queues.coordinator}")
    @Transactional
    public void handleFormatoAEvent(FormatoADTO dto) {
        System.out.println("📩 [RabbitMQ] Mensaje recibido en CoordinatorService: " + dto.getNombre());

        // Buscar si ya existe el FormatoA en la base de datos local
        Optional<FormatoA> existingFormato = formatoARepository.findById(dto.getId());

        FormatoA formato = existingFormato.orElse(new FormatoA());

        // Actualizar campos con la información nueva
        formato.setId(dto.getId());
        formato.setProyectoId(dto.getProyectoId());
        formato.setNroVersion(dto.getNroVersion());
        formato.setNombre(dto.getNombre());
        formato.setFechaSubida(dto.getFechaSubida());
        formato.setEstado(dto.getEstado());
        formato.setTipoTrabajoGrado(dto.getTipoTrabajoGrado());

        if (dto.getArchivo() != null) {
            formato.setArchivoBase64(Base64.getEncoder().encodeToString(dto.getArchivo()));
        }

        // Si el DTO trae estos campos (depende de la integración)
        if (dto.getEstudiantes() != null) formato.setEstudiantes(dto.getEstudiantes());
        if (dto.getDirector() != null) formato.setDirector(dto.getDirector());
        if (dto.getCoodirector() != null) formato.setCoodirector(dto.getCoodirector());

        formatoARepository.save(formato);

        System.out.println("[CoordinatorService] FormatoA guardado/actualizado correctamente: " + formato.getNombre());
    }
}

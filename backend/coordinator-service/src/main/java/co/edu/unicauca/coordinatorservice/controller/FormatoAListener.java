package co.edu.unicauca.coordinatorservice.controller;

import co.edu.unicauca.coordinatorservice.entity.*;
import co.edu.unicauca.coordinatorservice.infra.DTOS.EstadoFormatoA;
import co.edu.unicauca.coordinatorservice.infra.DTOS.EstudianteDTO;
import co.edu.unicauca.coordinatorservice.infra.DTOS.FormatoADTO;
import co.edu.unicauca.coordinatorservice.infra.DTOS.ProyectoDTO;
import co.edu.unicauca.coordinatorservice.infra.DTOS.TipoProyecto;
import co.edu.unicauca.coordinatorservice.repository.DocenteRepository;
import co.edu.unicauca.coordinatorservice.repository.EstudianteRepository;
import co.edu.unicauca.coordinatorservice.repository.FormatoARepository;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
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
    @RabbitListener(queues = "${messaging.queues.project}")
    @Transactional
    public void handleFormatoAEvent(ProyectoDTO dto) {
        if (dto == null) {
            System.err.println("⚠️ [RabbitMQ] Se recibió un mensaje nulo en CoordinatorService.");
            return;
        }

        System.out.println("📩 [RabbitMQ] Mensaje recibido en CoordinatorService: " + dto.getFormatoA().getNombreFormatoA());

        // Buscar si ya existe el FormatoA
        Optional<FormatoA> existingFormato = formatoARepository.findByProyectoId(dto.getId());
        FormatoA formato = existingFormato.orElse(new FormatoA());

        // Actualizar campos con la información nueva
        formato.setProyectoId(dto.getId());
        formato.setNroVersion(dto.getFormatoA().getNroVersion());
        formato.setNombreFormatoA(dto.getFormatoA().getNombreFormatoA());
        formato.setFechaSubida(dto.getFormatoA().getFechaSubida());
        formato.setBlob(dto.getFormatoA().getBlob());

        if (dto.getEstado() != null) {
            try {
                formato.setEstadoFormatoA(EstadoFormatoA.valueOf(dto.getFormatoA().getEstado().toString()));
            } catch (IllegalArgumentException e) {
                System.err.println("⚠️ Estado inválido recibido: " + dto.getFormatoA().getEstado());
            }
        } else {
            System.err.println("⚠️ FormatoA recibido sin estado (proyectoId=" + dto.getId() + ")");
            // Opcional: asignar un valor por defecto
            formato.setEstadoFormatoA(EstadoFormatoA.PENDIENTE);
        }

        formatoARepository.save(formato);

        System.out.println("[CoordinatorService] FormatoA guardado/actualizado correctamente: "
                + formato.getNombreProyecto() + " | Versión: " + formato.getNroVersion());
    }
}

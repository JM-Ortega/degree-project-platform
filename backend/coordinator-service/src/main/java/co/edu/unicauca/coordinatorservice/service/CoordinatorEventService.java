package co.edu.unicauca.coordinatorservice.service;

import co.edu.unicauca.coordinatorservice.controller.RabbitProducer;
import co.edu.unicauca.coordinatorservice.entity.FormatoA;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CoordinatorEventService {
    private final RabbitProducer rabbitProducer;

    @Value("${messaging.routing.formatAApprovedByCoordinator}")
    private String formatAApprovedRoutingKey;

    public CoordinatorEventService(RabbitProducer rabbitProducer) {
        this.rabbitProducer = rabbitProducer;
    }

    /**
     * Publica un evento cuando un coordinador aprueba un FormatoA.
     */
    public void publishFormatoAApproved(FormatoA formatoA) {
        Map<String, Object> message = new HashMap<>();
        message.put("formatoAId", formatoA.getId());
        message.put("proyectoId", formatoA.getProyectoId());
        message.put("estado", formatoA.getEstadoFormatoA().name());
        message.put("fechaAprobacion", java.time.LocalDate.now());
        message.put("tipoTrabajoGrado", formatoA.getTipoProyecto());

        rabbitProducer.sendMessage(formatAApprovedRoutingKey, message);
        System.out.println("Evento publicado: FormatoA aprobado -> " + formatoA.getNombre());
    }
}

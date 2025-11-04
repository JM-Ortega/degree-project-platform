package co.edu.unicauca.coordinatorservice.controller;

import co.edu.unicauca.coordinatorservice.entity.Coordinador;
import co.edu.unicauca.coordinatorservice.infra.DTOS.Programa;
import co.edu.unicauca.shared.contracts.model.Rol;
import co.edu.unicauca.coordinatorservice.repository.CoordinadorRepository;
import co.edu.unicauca.shared.contracts.events.auth.UserCreatedEvent;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class CoordinadorListener {
    private final CoordinadorRepository coordinadorRepository;

    public CoordinadorListener(CoordinadorRepository coordinadorRepository) {
        this.coordinadorRepository = coordinadorRepository;
    }

    @RabbitListener(queues = "${messaging.queues.auth}")
    @Transactional
    public void recibirCoordinador(UserCreatedEvent dto) {
        // Verifica si el usuario tiene el rol COORDINADOR
        boolean esCoordinador = dto.roles().stream()
                .anyMatch(r -> r.toString().equalsIgnoreCase("COORDINADOR"));

        if (!esCoordinador) {
            // Ignorar si no es coordinador
            return;
        }

        System.out.println("📩 [RabbitMQ] Mensaje recibido en CoordinatorService: " + dto.nombre());

        // Buscar si ya existe el coordinador
        Optional<Coordinador> existente = coordinadorRepository.findByCorreo(dto.email());
        Coordinador coordinador = existente.orElse(new Coordinador());

        // Asignar campos
        coordinador.setNombres(dto.nombre());
        coordinador.setCorreo(dto.email());
        coordinador.setPrograma(Programa.valueOf(dto.programa().toString()));

        coordinadorRepository.save(coordinador);

        System.out.println("[CoordinatorService] Coordinador guardado/actualizado correctamente: " + coordinador.getNombres());
    }
}

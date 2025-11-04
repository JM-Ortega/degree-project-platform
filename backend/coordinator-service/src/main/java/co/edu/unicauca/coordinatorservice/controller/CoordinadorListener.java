package co.edu.unicauca.coordinatorservice.controller;

import co.edu.unicauca.coordinatorservice.entity.Coordinador;
import co.edu.unicauca.coordinatorservice.infra.DTOS.Programa;
import co.edu.unicauca.coordinatorservice.infra.DTOS.CoordinadorDTO;
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
        System.out.println("📩 [RabbitMQ] Mensaje recibido en CoordinatorService: " + dto.nombre());

        // Buscar si ya existe el coordinador en la base de datos local
        Optional<Coordinador> existingFormato = coordinadorRepository.findByCorreo(dto.email());

        Coordinador coordinador = existingFormato.orElse(new Coordinador());

        coordinador.setNombres(dto.nombre());
        coordinador.setCorreo(dto.email());
        coordinador.setPrograma(Programa.valueOf(dto.programa().toString()));

        coordinadorRepository.save(coordinador);
        System.out.println("[CoordinatorService] Coordinador guardado/actualizado correctamente, nombre: " + coordinador.getNombres());
    }

}

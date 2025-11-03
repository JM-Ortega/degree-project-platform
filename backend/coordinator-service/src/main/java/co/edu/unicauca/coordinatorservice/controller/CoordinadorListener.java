package co.edu.unicauca.coordinatorservice.controller;

import co.edu.unicauca.coordinatorservice.entity.Coordinador;
import co.edu.unicauca.coordinatorservice.infra.DTOS.Programa;
import co.edu.unicauca.coordinatorservice.infra.DTOS.CoordinadorDTO;
import co.edu.unicauca.coordinatorservice.repository.CoordinadorRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class CoordinadorListener {
    private final CoordinadorRepository coordinadorRepository;

    public CoordinadorListener(CoordinadorRepository coordinadorRepository) {
        this.coordinadorRepository = coordinadorRepository;
    }

    //@RabbitListener(queues = "${messaging.queues.project}")
    @Transactional
    public void recibirCoordinador(CoordinadorDTO dto) {
        System.out.println("📩 [RabbitMQ] Mensaje recibido en CoordinatorService: " + dto.getNombres());

        // Buscar si ya existe el coordinador en la base de datos local
        Optional<Coordinador> existingFormato = coordinadorRepository.findByCorreo(dto.getEmail());

        Coordinador coordinador = existingFormato.orElse(new Coordinador());

        coordinador.setCodigo(dto.getCodigo());
        coordinador.setNombres(dto.getNombres());
        coordinador.setApellidos(dto.getApellidos());
        coordinador.setCorreo(dto.getEmail());
        coordinador.setPrograma(Programa.valueOf(dto.getPrograma().toString()));

        coordinadorRepository.save(coordinador);
        System.out.println("[CoordinatorService] Coordinador guardado/actualizado correctamente, nombre: " + coordinador.getNombres() + " " + coordinador.getApellidos());
    }

}

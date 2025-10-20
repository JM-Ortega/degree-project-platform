package co.edu.unicauca.coordinatorservice.controller;

import co.edu.unicauca.coordinatorservice.entity.Coordinador;
import co.edu.unicauca.coordinatorservice.infra.CoordinadorInfoDTO;
import co.edu.unicauca.coordinatorservice.repository.CoordinadorRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/coordinador")
public class CoordinadorController {
    private final CoordinadorRepository coordinadorRepository;

    public CoordinadorController(CoordinadorRepository coordinadorRepository) {
        this.coordinadorRepository = coordinadorRepository;
    }

    @GetMapping("/{id}/info")
    public CoordinadorInfoDTO getCoordinadorInfo(@PathVariable Long id) {
        Coordinador c = coordinadorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No encontrado"));
        return new CoordinadorInfoDTO(c.getNombres() + " " + c.getApellidos(), c.getPrograma().toString());
    }
}

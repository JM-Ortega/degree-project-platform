package co.edu.unicauca.coordinatorservice.controller;

import co.edu.unicauca.coordinatorservice.entity.Coordinador;
import co.edu.unicauca.coordinatorservice.infra.DTOS.CoordinadorDTO;
import co.edu.unicauca.coordinatorservice.infra.DTOSInternos.CoordinadorResumenDTO;
import co.edu.unicauca.coordinatorservice.infra.DTOSInternos.FormatoAResumenDTO;
import co.edu.unicauca.coordinatorservice.repository.CoordinadorRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/coordinadores")
public class CoordinadorController {
    private final CoordinadorRepository coordinadorRepository;

    public CoordinadorController(CoordinadorRepository coordinadorRepository) {
        this.coordinadorRepository = coordinadorRepository;
    }

    @GetMapping("/{correo}/info")
    public CoordinadorResumenDTO getCoordinadorInfo(@PathVariable String correo) {
        Coordinador c = coordinadorRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("No encontrado"));
        return new CoordinadorResumenDTO(c.getNombres() + " " + c.getApellidos(), c.getPrograma().toString(), c.getCorreo());
    }
}
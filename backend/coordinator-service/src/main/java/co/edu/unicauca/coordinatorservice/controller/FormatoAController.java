package co.edu.unicauca.coordinatorservice.controller;

import co.edu.unicauca.coordinatorservice.entity.FormatoA;
import co.edu.unicauca.coordinatorservice.repository.FormatoARepository;
import co.edu.unicauca.coordinatorservice.service.CoordinatorEventService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/formatoA")
public class FormatoAController {
    private final FormatoARepository formatoARepository;
    private final CoordinatorEventService eventService;

    public FormatoAController(FormatoARepository formatoARepository,
                              CoordinatorEventService eventService) {
        this.formatoARepository = formatoARepository;
        this.eventService = eventService;
    }

    @PutMapping("/{id}/approve")
    public String approveFormatoA(@PathVariable Long id) {
        FormatoA formato = formatoARepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FormatoA no encontrado"));

        formato.setEstado(co.edu.unicauca.coordinatorservice.entity.EstadoFormatoA.APROBADO);
        formatoARepository.save(formato);

        eventService.publishFormatoAApproved(formato);

        return "FormatoA aprobado y evento publicado correctamente.";
    }

    @GetMapping("/listar")
    public List<FormatoA> listarTodos() {
        return formatoARepository.findAll();
    }

    @GetMapping("/{id}")
    public FormatoA obtenerPorId(@PathVariable Long id) {
        return formatoARepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FormatoA no encontrado"));
    }
}

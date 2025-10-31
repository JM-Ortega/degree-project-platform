package co.edu.unicauca.coordinatorservice.controller;

import co.edu.unicauca.coordinatorservice.entity.EstadoFormatoA;
import co.edu.unicauca.coordinatorservice.entity.FormatoA;
import co.edu.unicauca.coordinatorservice.repository.FormatoARepository;
import co.edu.unicauca.coordinatorservice.service.CoordinatorEventService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
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

        formato.setEstadoFormatoA(EstadoFormatoA.APROBADO);
        formatoARepository.save(formato);

        eventService.publishFormatoAApproved(formato);

        return "FormatoA aprobado y evento publicado correctamente.";
    }

    @GetMapping("/listar")
    public List<FormatoA> listarTodos() {
        return formatoARepository.findAll()
                .stream()
                .map(f -> new FormatoA(
                        f.getId(),
                        f.getProyectoId(),
                        f.getNroVersion(),
                        f.getNombre(),
                        f.getFechaSubida(),
                        f.getBlob(),
                        f.getEstadoFormatoA(),
                        f.getEstudiantesEmail(),
                        f.getDirector(),
                        f.getCoodirector(),
                        f.getTipoProyecto(),
                        f.getEstadoProyecto()
                ))
                .toList();
    }

    @GetMapping("/{id}")
    public FormatoA obtenerPorId(@PathVariable Long id) {
        return formatoARepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FormatoA no encontrado"));
    }

    @GetMapping("/descargar/{id}")
    public ResponseEntity<ByteArrayResource> descargarArchivo(@PathVariable Long id) {
        var formatoA = formatoARepository.findById(id).orElse(null);
        if (formatoA == null || formatoA.getBlob() == null) {
            return ResponseEntity.notFound().build();
        }

        ByteArrayResource recurso = new ByteArrayResource(formatoA.getBlob());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"formatoA_" + formatoA.getId() + ".pdf\"")
                // Tipo de archivo genérico binario
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(formatoA.getBlob().length)
                .body(recurso);
    }
}

package co.edu.unicauca.coordinatorservice.controller;

import co.edu.unicauca.coordinatorservice.entity.FormatoA;
import co.edu.unicauca.coordinatorservice.infra.FormatoADTO;
import co.edu.unicauca.coordinatorservice.repository.FormatoARepository;
import co.edu.unicauca.coordinatorservice.service.CoordinatorEventService;
import org.springframework.http.HttpHeaders;
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

        formato.setEstado(co.edu.unicauca.coordinatorservice.entity.EstadoFormatoA.APROBADO);
        formatoARepository.save(formato);

        eventService.publishFormatoAApproved(formato);

        return "FormatoA aprobado y evento publicado correctamente.";
    }

    @GetMapping("/listar")
    public List<FormatoADTO> listarTodos() {
        return formatoARepository.findAll()
                .stream()
                .map(f -> new FormatoADTO(
                        f.getId(),
                        f.getEstudiantes(),
                        f.getDirector(),
                        f.getCoodirector(),
                        f.getNroVersion(),
                        f.getNombre(),
                        f.getFechaSubida(),
                        f.getEstado().name(),
                        f.getTipoTrabajoGrado()
                ))
                .toList();
    }

    @GetMapping("/{id}")
    public FormatoA obtenerPorId(@PathVariable Long id) {
        return formatoARepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FormatoA no encontrado"));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> descargarArchivo(@PathVariable Long id) {
        FormatoA formato = formatoARepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FormatoA no encontrado"));

        if (formato.getArchivoBase64() == null) {
            return ResponseEntity.notFound().build();
        }

        byte[] archivoBytes = Base64.getDecoder().decode(formato.getArchivoBase64());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + formato.getNombre())
                .body(archivoBytes);
    }
}

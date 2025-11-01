package co.edu.unicauca.coordinatorservice.controller;

import co.edu.unicauca.coordinatorservice.entity.EstadoFormatoA;
import co.edu.unicauca.coordinatorservice.entity.FormatoA;
import co.edu.unicauca.coordinatorservice.infra.DTOSInternos.FormatoAResumenDTO;
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
    public ResponseEntity<List<FormatoAResumenDTO>> listarFormatosAResumen() {
        List<FormatoAResumenDTO> lista = formatoARepository.findAll().stream()
                .map(f -> new FormatoAResumenDTO(
                        f.getId(),
                        f.getNombreProyecto(),
                        f.getDirector().getNombres() + " " + f.getDirector().getApellidos(),
                        f.getTipoProyecto().toString(),
                        f.getFechaSubida(),
                        f.getEstadoFormatoA(),
                        f.getNroVersion()
                ))
                .toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public FormatoA obtenerPorId(@PathVariable Long id) {
        return formatoARepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FormatoA no encontrado"));
    }

    @GetMapping("/descargar/{id}")
    public ResponseEntity<byte[]> descargarArchivo(@PathVariable Long id) {
        FormatoA formatoA = formatoARepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Formato A no encontrado"));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + formatoA.getNombreProyecto() + ".pdf\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(formatoA.getBlob());
    }
}

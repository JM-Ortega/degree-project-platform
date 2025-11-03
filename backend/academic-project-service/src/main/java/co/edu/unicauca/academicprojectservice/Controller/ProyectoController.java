package co.edu.unicauca.academicprojectservice.Controller;

import co.edu.unicauca.academicprojectservice.Entity.*;
import co.edu.unicauca.academicprojectservice.Repository.ProyectoRepository;
import co.edu.unicauca.academicprojectservice.Service.ProyectoService;
import co.edu.unicauca.academicprojectservice.infra.dto.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/proyectos")
public class ProyectoController {
    private final ProyectoService proyectoService;
    private final ProyectoRepository proyectoRepository;

    public ProyectoController(ProyectoService proyectoService, ProyectoRepository proyectoRepository) {
        this.proyectoService = proyectoService;
        this.proyectoRepository = proyectoRepository;
    }

    @GetMapping("/docente/{correo}")
    public ResponseEntity<List<ProyectoInfoDTO>> listarPorDocente(
            @PathVariable("correo") String correo,
            @RequestParam(value = "filtro", required = false) String filtro
    ) {
        List<ProyectoInfoDTO> proyectos = proyectoService.listarInfoPorCorreoDocente(correo, filtro);
        return ResponseEntity.ok(proyectos);
    }

    @GetMapping("/listar/{correo}")
    public ResponseEntity<List<ProyectoEstudianteDTO>> listarPorEstudiante(@PathVariable String correo) {
        List<Proyecto> proyectos = proyectoRepository.findByEstudianteCorreo(correo);

        List<ProyectoEstudianteDTO> lista = proyectos.stream()
                .map(p -> new ProyectoEstudianteDTO(
                        p.getId(),
                        p.getTitulo(),
                        p.getDirector().getNombres() + " " + p.getDirector().getApellidos(),
                        p.getTipoProyecto().toString(),
                        p.getEstadoProyecto().toString()
                ))
                .toList();

        return ResponseEntity.ok(lista);
    }

    @PostMapping("/crearConArchivos")
    public ResponseEntity<String> crearProyecto(@RequestBody ProyectoDTO dto) {
        try {
            proyectoService.crearProyectoConArchivos(dto);
            return ResponseEntity.ok("Proyecto creado exitosamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear el proyecto: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/enforceAutoCancel")
    public ResponseEntity<EstadoProyecto> enforceAutoCancelIfNeeded(@PathVariable("id") long proyectoId) {
        EstadoProyecto estado = proyectoService.enforceAutoCancelIfNeeded(proyectoId);
        return ResponseEntity.ok(estado);
    }

    @GetMapping("/{proyectoId}/formatoA/max-version")
    public ResponseEntity<Integer> getMaxVersionFormatoA(@PathVariable Long proyectoId) {
        int maxVersion = proyectoService.getMaxVersionFormatoA(proyectoId);
        return ResponseEntity.ok(maxVersion);
    }

    @GetMapping("/resubmit/{proyectoId}")
    public ResponseEntity<Boolean> canResubmit(@PathVariable Long proyectoId) {
        boolean puede = proyectoService.canResubmit(proyectoId);
        return ResponseEntity.ok(puede);
    }

    @GetMapping("/observacionesFA/{proyectoId}")
    public ResponseEntity<Boolean> tieneObservacionesFA(@PathVariable Long proyectoId) {
        boolean tiene = proyectoService.tieneObservaciones(proyectoId);
        return ResponseEntity.ok(tiene);
    }

    @GetMapping("/existeProyecto/{proyectoId}")
    public ResponseEntity<Boolean> existeProyecto(@PathVariable Long proyectoId) {
        boolean tiene = proyectoService.existeProyecto(proyectoId);
        return ResponseEntity.ok(tiene);
    }

    @GetMapping("/estadoProyecto/{proyectoId}")
    public ResponseEntity<String> estadoProyecto(@PathVariable Long proyectoId) {
        String estado = proyectoService.estadoProyecto(proyectoId);
        return ResponseEntity.ok(estado);
    }

    @PostMapping("/insertarFormatoAProyecto/{proyectoId}")
    public ResponseEntity<String> insertarFormatoAProyecto(@PathVariable Long proyectoId, @RequestBody FormatoA formatoA) {
        proyectoService.insertarFormatoAEnProyecto(proyectoId, formatoA);
        return ResponseEntity.ok("Formato A insertado correctamente");
    }

    @GetMapping("/ultimoFormatoAConObservaciones/{proyectoId}")
    public ResponseEntity<?> obtenerUltimoFormatoAConObservaciones(@PathVariable Long proyectoId) {
        try {
            FormatoA formato = proyectoService.obtenerUltimoFormatoAConObservaciones(proyectoId);
            if (formato == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No se encontró un Formato A con observaciones para el proyecto " + proyectoId);
            }
            return ResponseEntity.ok(formato);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener el Formato A: " + e.getMessage());
        }
    }

    @PostMapping("/actualizarFormatoA/{proyectoId}")
    public ResponseEntity<String> actualizarFormatoA(@PathVariable Long proyectoId, @RequestBody EstadoRequest request) {
        proyectoService.actualizarFormatoA(proyectoId, request.getEstado());
        return ResponseEntity.ok("Formato A actualizado correctamente");
    }

    @GetMapping("/countProyectosBy")
    public ResponseEntity<Integer> countProyectosByEstadoYTipo(@RequestParam TipoProyecto tipoProyecto, @RequestParam EstadoProyecto estadoProyecto, @RequestParam String correoDocente) {
        int count = proyectoService.countProyectosByEstadoYTipo(tipoProyecto, estadoProyecto, correoDocente);
        return ResponseEntity.ok(count);
    }


    @GetMapping("/docente/{correo}/anteproyectos")
    public ResponseEntity<List<AnteproyectoDTO>> listarAnteproyectosDocente(
            @PathVariable("correo") String correo,
            @RequestParam(value = "filtro", required = false) String filtro
    ) {
        List<AnteproyectoDTO> lista = proyectoService.listarAnteproyectosDocente(correo, filtro);
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{proyectoId}/anteproyecto")
    public ResponseEntity<AnteproyectoDTO> obtenerAnteproyecto(@PathVariable long proyectoId) {
        try {
            AnteproyectoDTO anteproyecto = proyectoService.obtenerAnteproyecto(proyectoId);
            return ResponseEntity.ok(anteproyecto);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{proyectoId}/ultimo-formatoA-observado")
    public ResponseEntity<FormatoADTO> obtenerUltimoFormatoAConObservaciones(@PathVariable long proyectoId) {
        try {
            FormatoADTO formatoADTO = proyectoService.obtenerUltimoFormatoAConObservaciones(proyectoId);
            return ResponseEntity.ok(formatoADTO);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}

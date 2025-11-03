package co.edu.unicauca.academicprojectservice.Controller;

import co.edu.unicauca.academicprojectservice.Service.EstudianteService;
import co.edu.unicauca.academicprojectservice.Service.ProyectoService;
import co.edu.unicauca.academicprojectservice.infra.dto.AnteproyectoDTO;
import co.edu.unicauca.academicprojectservice.infra.dto.EstudianteDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/estudiantes")
public class EstudianteController {
    @Autowired
    private EstudianteService estudianteService;
    @Autowired
    private ProyectoService proyectoService;

    @GetMapping("/libre/{correo}")
    public ResponseEntity<Boolean> estudianteLibre(@PathVariable String correo) throws Exception {
        if (!estudianteService.existeEstudiantePorCorreo(correo)) {
            throw new Exception();
        }
        boolean libre = !estudianteService.estudianteTieneProyectoEnTramitePorCorreo(correo);
        return ResponseEntity.ok(libre);
    }

    @GetMapping("/existe/{correo}")
    public ResponseEntity<Boolean> existeEstudiante(@PathVariable String correo) {
        boolean existe = estudianteService.existeEstudiantePorCorreo(correo);
        return ResponseEntity.ok(existe);
    }

    @GetMapping
    public ResponseEntity<EstudianteDTO> obtenerEstudiante(@RequestParam String correo) {
        try {
            EstudianteDTO dto = estudianteService.obtenerEstudiantePorCorreo(correo);
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/agregarEstudiante")
    public ResponseEntity<EstudianteDTO> agregarEstudiante(@RequestBody EstudianteDTO dto) {
        estudianteService.agregarEstudiante(dto);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/tieneProyectoEnTramite/{correo}")
    public ResponseEntity<Boolean> estudianteTieneProyectoEnTramitePorCorreo(@PathVariable String correo) {
        boolean existe = estudianteService.estudianteTieneProyectoEnTramitePorCorreo(correo);
        return ResponseEntity.ok(existe);
    }

    @GetMapping("/tieneFormatoAAprobado/{correo}")
    public ResponseEntity<Boolean> estudianteTieneFormatoAAprobado(@PathVariable String correo) {
        boolean existe = estudianteService.estudianteTieneFormatoAAprobado(correo);
        return ResponseEntity.ok(existe);
    }

    @PostMapping("/asociarAnteproyecto/{correo}")
    public ResponseEntity<String> asociarAnteproyectoAProyecto(
            @PathVariable String correo,
            @RequestBody AnteproyectoDTO anteproyectoDTO) {
        try {
            proyectoService.asociarAnteproyectoAProyecto(correo, anteproyectoDTO);
            return ResponseEntity.ok("Anteproyecto asociado correctamente al proyecto del estudiante");
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @GetMapping("/{correo}/tieneAnteproyecto")
    public ResponseEntity<Boolean> estudianteTieneAnteproyecto(@PathVariable String correo) {
        try {
            boolean tiene = estudianteService.estudianteTieneAnteproyectoAsociado(correo);
            return ResponseEntity.ok(tiene);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }
}

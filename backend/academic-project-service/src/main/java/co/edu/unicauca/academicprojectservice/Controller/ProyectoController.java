package co.edu.unicauca.academicprojectservice.Controller;

import co.edu.unicauca.academicprojectservice.Entity.Proyecto;
import co.edu.unicauca.academicprojectservice.Service.ProyectoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/proyectos")
public class ProyectoController {
    private final ProyectoService proyectoService;

    public ProyectoController(ProyectoService proyectoService) {
        this.proyectoService = proyectoService;
    }

    @GetMapping("/docente/{id}")
    public ResponseEntity<List<Proyecto>> listarPorDocente(
            @PathVariable("id") Long docenteId,
            @RequestParam(value = "filtro", required = false) String filtro
    ) {
        List<Proyecto> proyectos = proyectoService.listarPorDocente(docenteId, filtro);
        return ResponseEntity.ok(proyectos);
    }
}

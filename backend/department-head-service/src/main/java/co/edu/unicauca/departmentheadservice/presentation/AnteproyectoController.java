package co.edu.unicauca.departmentheadservice.presentation;

import co.edu.unicauca.departmentheadservice.entities.Anteproyecto;
import co.edu.unicauca.departmentheadservice.services.AnteproyectoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/")
public class AnteproyectoController {

    private final AnteproyectoService anteproyectoService;

    public AnteproyectoController(AnteproyectoService anteproyectoService) {
        this.anteproyectoService = anteproyectoService;
    }

    @GetMapping("/sin-evaluadores")
    @Operation(summary = "Obtener todos los Anteproyectos sin evaluadores",
            description = "Este endpoint devuelve todos los Anteproyectos que no tienen evaluadores asignados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de Anteproyectos sin evaluadores"),
            @ApiResponse(responseCode = "404", description = "No se encontraron Anteproyectos sin evaluadores")
    })
    public List<Anteproyecto> obtenerAnteproyectosSinEvaluadores() {
        return anteproyectoService.obtenerAnteproyectosSinEvaluadores();
    }

    /**
     * Endpoint para buscar Anteproyectos por nombre o ID.
     *
     * @param nombre el nombre del anteproyecto a buscar.
     * @param id     el ID del anteproyecto a buscar (como String para mayor flexibilidad).
     * @return lista de Anteproyectos que coinciden con el nombre o ID.
     */
    @GetMapping("/buscar")
    @Operation(
            summary = "Buscar Anteproyectos por nombre o ID",
            description = "Este endpoint permite buscar Anteproyectos por nombre o ID, proporcionando un parámetro o ambos."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de Anteproyectos que coinciden con el nombre o ID"),
            @ApiResponse(responseCode = "404", description = "No se encontraron Anteproyectos con esos parámetros")
    })
    public List<Anteproyecto> buscarPorNombreOIdSinEvaluadores(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String id) {

        return anteproyectoService.buscarPorNombreOIdSinEvaluadores(nombre, id);
    }
}
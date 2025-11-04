package co.edu.unicauca.departmentheadservice.services;

import co.edu.unicauca.departmentheadservice.access.AnteproyectoRepository;
import co.edu.unicauca.departmentheadservice.entities.Anteproyecto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AnteproyectoService {

    private final AnteproyectoRepository anteproyectoRepository;

    public AnteproyectoService(AnteproyectoRepository anteproyectoRepository) {
        this.anteproyectoRepository = anteproyectoRepository;
    }

    public List<Anteproyecto> obtenerAnteproyectosSinEvaluadores() {
        return anteproyectoRepository.findByEvaluadoresIsEmpty();
    }

    /**
     * Busca los Anteproyectos sin evaluadores, filtrando por título o ID.
     *
     * @param nombre el título del anteproyecto (puede ser null o vacío)
     * @param id     el ID del anteproyecto como String (puede ser null o vacío)
     * @return lista de Anteproyectos sin evaluadores que coinciden con el título o ID.
     */
    public List<Anteproyecto> buscarPorNombreOIdSinEvaluadores(String nombre, String id) {
        Set<Anteproyecto> resultado = new HashSet<>();

        boolean buscarPorNombre = nombre != null && !nombre.trim().isEmpty();
        boolean buscarPorId = id != null && !id.trim().isEmpty();

        // Si no hay criterios de búsqueda, devolver todos
        if (!buscarPorNombre && !buscarPorId) {
            return anteproyectoRepository.findByEvaluadoresIsEmpty();
        }

        // Buscar por nombre (si se proporciona)
        if (buscarPorNombre) {
            String nombreBusqueda = nombre.trim();
            List<Anteproyecto> porNombre = anteproyectoRepository
                .findByEvaluadoresIsEmptyAndTituloContainingIgnoreCase(nombreBusqueda);
            resultado.addAll(porNombre);
        }

        // Buscar por ID (si se proporciona)
        if (buscarPorId) {
            String idBusqueda = id.trim();
            try {
                // Intentar buscar como ID numérico
                Long idLong = Long.parseLong(idBusqueda);
                List<Anteproyecto> porId = anteproyectoRepository
                    .findByEvaluadoresIsEmptyAndId(idLong);
                resultado.addAll(porId);
            } catch (NumberFormatException e) {
                // Si no es numérico, buscar en otros campos (como código)
                // Por ejemplo, si tienes un campo código:
                // List<Anteproyecto> porCodigo = anteproyectoRepository
                //     .findByEvaluadoresIsEmptyAndCodigoContainingIgnoreCase(idBusqueda);
                // resultado.addAll(porCodigo);

                // Por ahora, solo log el error
                System.err.println("ID no numérico proporcionado: " + idBusqueda);
            }
        }

        return new ArrayList<>(resultado);
    }
}
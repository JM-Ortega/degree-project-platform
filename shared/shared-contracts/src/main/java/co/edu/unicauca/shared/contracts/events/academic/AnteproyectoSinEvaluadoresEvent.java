package co.edu.unicauca.shared.contracts.events.academic;

import java.time.LocalDate;

public record AnteproyectoSinEvaluadoresEvent(
        String anteproyectoId,    // Identificador único del Anteproyecto
        String titulo,            // Título del Anteproyecto
        String descripcion,       // Descripción del Anteproyecto
        LocalDate fechaCreacion,  // Fecha de creacion del Anteproyecto
        String departamento       // Departamento que aprueba el anteproyecto
) {
}

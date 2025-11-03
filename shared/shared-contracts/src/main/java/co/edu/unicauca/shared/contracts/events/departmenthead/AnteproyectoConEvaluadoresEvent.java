package co.edu.unicauca.shared.contracts.events.departmenthead;

import java.util.List;

public record AnteproyectoConEvaluadoresEvent(
        String anteproyectoId,    // Identificador único del Anteproyecto
        String titulo,            // Título del Anteproyecto
        String departamento,      // Departamento que aprueba el anteproyecto
        List<String> evaluadores  // Lista de correos o IDs de evaluadores asignados
) {
}

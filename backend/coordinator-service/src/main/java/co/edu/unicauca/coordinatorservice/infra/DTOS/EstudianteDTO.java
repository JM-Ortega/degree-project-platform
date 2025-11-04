package co.edu.unicauca.coordinatorservice.infra.DTOS;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class EstudianteDTO extends PersonaDTO {
    private String codigo;
    private Programa programa;
    private List<ProyectoDTO> trabajos;
}


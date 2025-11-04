package co.edu.unicauca.coordinatorservice.infra.DTOS;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class DocenteDTO extends PersonaDTO {
    private Departamento departamento;
    private List<ProyectoDTO> trabajosComoDirector;
    private List<ProyectoDTO> trabajosComoCodirector;
    private String codigoDocente;
}
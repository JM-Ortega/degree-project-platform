package co.edu.unicauca.coordinatorservice.infra.DTOS;

import co.edu.unicauca.shared.contracts.model.Departamento;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class DocenteDTO extends PersonaDTO {
    private Departamento departamento;
    @JsonBackReference
    private List<ProyectoDTO> trabajosComoDirector;
    @JsonBackReference
    private List<ProyectoDTO> trabajosComoCodirector;
}
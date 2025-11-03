package co.edu.unicauca.academicprojectservice.infra.DTOs;

import co.edu.unicauca.academicprojectservice.Entity.Departamento;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class DocenteDTO extends PersonaDTO {
    private Departamento departamento;
    private List<ProyectoDTOSend> trabajosComoDirector;
    private List<ProyectoDTOSend> trabajosComoCodirector;
}

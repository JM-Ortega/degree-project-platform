package co.edu.unicauca.academicprojectservice.infra.DTOs;

import co.edu.unicauca.academicprojectservice.Entity.Programa;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class EstudianteDTO extends PersonaDTO {
    private String codigo;
    private Programa programa;
    private List<ProyectoDTOSend> trabajos;
}
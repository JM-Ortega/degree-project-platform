package co.edu.unicauca.academicprojectservice.infra.DTOs;

import co.edu.unicauca.academicprojectservice.Entity.Programa;
import co.edu.unicauca.academicprojectservice.Entity.Rol;
import lombok.Data;

@Data
public class PersonaDTO {
    private Long id;
    private String nombres;
    private String apellidos;
    private String celular;
    private Programa programa;
    private String email;
    private String passwordHash;
    private Rol rol;
}

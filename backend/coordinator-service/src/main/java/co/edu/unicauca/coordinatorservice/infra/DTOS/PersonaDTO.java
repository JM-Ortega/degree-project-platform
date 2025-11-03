package co.edu.unicauca.coordinatorservice.infra.DTOS;

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

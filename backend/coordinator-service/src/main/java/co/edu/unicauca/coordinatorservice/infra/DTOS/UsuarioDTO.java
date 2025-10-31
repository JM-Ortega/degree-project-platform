package co.edu.unicauca.coordinatorservice.infra.DTOS;

import lombok.Data;

@Data
public class UsuarioDTO {
    private String email;
    private String passwordHash;
    private Rol rol;
}

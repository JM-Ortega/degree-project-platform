package co.edu.unicauca.coordinatorservice.infra.DTOS;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CoordinadorDTO extends PersonaDTO {
    private String codigo;
    private Programa programa;
}

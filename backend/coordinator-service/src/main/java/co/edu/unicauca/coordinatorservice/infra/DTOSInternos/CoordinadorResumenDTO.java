package co.edu.unicauca.coordinatorservice.infra.DTOSInternos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CoordinadorResumenDTO {
    private String nombreCompleto;
    private String programa;
    private String correo;
}

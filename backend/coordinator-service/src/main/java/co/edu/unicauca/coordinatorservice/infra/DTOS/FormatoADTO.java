package co.edu.unicauca.coordinatorservice.infra.DTOS;

import lombok.Data;
import java.time.LocalDate;

@Data
public class FormatoADTO {
    private Long id;
    private Long proyectoId;
    private int nroVersion;
    private String nombre;
    private LocalDate fechaSubida;
    private byte[] blob;
    private EstadoFormatoA estado;
}

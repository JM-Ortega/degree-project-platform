package co.edu.unicauca.coordinatorservice.infra.DTOS;

import lombok.Data;
import java.time.LocalDate;

@Data
public class FormatoADTO {
    private Long id;
    private Long proyectoId;
    private int nroVersion;
    private String nombreFormatoA;
    private LocalDate fechaSubida;
    private byte[] blob;
    private EstadoFormatoA estado;

    public FormatoADTO(Long id, Long proyectoId, int nroVersion, String nombreFormatoA, LocalDate fechaSubida, byte[] blob, EstadoFormatoA estado) {
        this.id = id;
        this.proyectoId = proyectoId;
        this.nroVersion = nroVersion;
        this.nombreFormatoA = nombreFormatoA;
        this.fechaSubida = fechaSubida;
        this.blob = blob;
        this.estado = estado;
    }
}

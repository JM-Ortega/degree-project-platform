package co.edu.unicauca.academicprojectservice.infra.DTOs;

import co.edu.unicauca.academicprojectservice.Entity.EstadoFormatoA;
import lombok.Data;

import java.util.Date;


@Data
public class FormatoADTO {
    private Long id;
    private Long proyectoId;
    private int nroVersion;
    private String nombreFormatoA;
    private Date fechaSubida;
    private byte[] blob;
    private EstadoFormatoA estado;
}
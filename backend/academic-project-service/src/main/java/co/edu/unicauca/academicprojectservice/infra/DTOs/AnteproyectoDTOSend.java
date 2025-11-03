package co.edu.unicauca.academicprojectservice.infra.DTOs;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class AnteproyectoDTO {
    private Long id;
    private String titulo;
    private String descripcion;
    private Date fechaCreacion;
    private ProyectoDTOSend proyecto;
    private List<DocenteDTO> evaluadores;
}

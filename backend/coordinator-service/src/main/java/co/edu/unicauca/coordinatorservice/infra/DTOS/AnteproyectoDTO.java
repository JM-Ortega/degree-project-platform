package co.edu.unicauca.coordinatorservice.infra.DTOS;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
public class AnteproyectoDTO {
    private Long id;
    private String titulo;
    private String descripcion;
    private LocalDate fechaCreacion;
    private ProyectoDTO proyecto;
    private List<DocenteDTO> evaluadores;
}

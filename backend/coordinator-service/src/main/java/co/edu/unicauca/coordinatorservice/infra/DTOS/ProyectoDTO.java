package co.edu.unicauca.coordinatorservice.infra.DTOS;

import lombok.Data;

import java.util.List;

@Data
public class ProyectoDTO {
    private Long id;
    private String titulo;
    private List<EstudianteDTO> estudiantes;
    private DocenteDTO director;
    private DocenteDTO codirector;
    private AnteproyectoDTO anteproyecto;
    private FormatoADTO formatoA;
    private TipoProyecto tipoProyecto;
    private EstadoProyecto estado;
}

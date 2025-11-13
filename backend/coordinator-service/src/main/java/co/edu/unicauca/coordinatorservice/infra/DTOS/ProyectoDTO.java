package co.edu.unicauca.coordinatorservice.infra.DTOS;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import java.util.List;

@Data
public class ProyectoDTO {
    private Long id;
    private String titulo;
    private TipoProyecto tipoProyecto;
    private EstadoProyecto estado;
    private List<EstudianteDTO> estudiantes;
    private DocenteDTO director;
    private DocenteDTO codirector;
    private AnteproyectoDTO anteproyecto;
    private FormatoADTO formatoA;
}

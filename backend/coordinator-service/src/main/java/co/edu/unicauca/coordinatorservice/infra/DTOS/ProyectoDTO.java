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
    @JsonManagedReference
    private List<EstudianteDTO> estudiantes;
    @JsonManagedReference
    private DocenteDTO director;
    @JsonManagedReference
    private DocenteDTO codirector;
    private AnteproyectoDTO anteproyecto;
    private FormatoADTO formatoA;
}

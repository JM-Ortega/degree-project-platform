package co.edu.unicauca.coordinatorservice.infra.DTOSInternos;

import co.edu.unicauca.coordinatorservice.entity.EstadoFormatoA;
import co.edu.unicauca.coordinatorservice.entity.TipoProyecto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FormatoAResumenDTO {
    private Long id; // para luego usarlo en la descarga
    private String nombreProyecto;
    private String nombreDirector;
    private TipoProyecto tipoProyecto;
    private LocalDate fechaSubida;
    private EstadoFormatoA estadoFormatoA;
}


package co.edu.unicauca.authservice.domain.entities;

import co.edu.unicauca.shared.contracts.model.Programa;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

/**
 * Representa a un coordinador académico dentro de la universidad.
 *
 * <p>Hereda de {@link Persona} y especifica el {@link Programa}
 * académico que coordina. A diferencia de {@link JefeDeDepartamento},
 * no se asocia directamente a un {@code Departamento} sino a un
 * programa concreto.</p>
 */
@Entity
@Table(name = "coordinadores")
@Schema(description = "Entidad que representa a un coordinador de programa académico.")
public class Coordinador extends Persona {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 100)
    @Schema(description = "Programa académico que coordina este usuario.", example = "IngenieriaDeSistemas")
    private Programa programaCoordinado;

    protected Coordinador() {
        // Requerido por JPA
    }

    public Coordinador(String id,
                       String codigo,
                       String nombres,
                       String apellidos,
                       String celular,
                       Programa programa,
                       Usuario usuario,
                       Programa programaCoordinado) {
        super(id, codigo, nombres, apellidos, celular, programa, usuario);
        this.programaCoordinado = programaCoordinado;
    }

    public Programa getProgramaCoordinado() {
        return programaCoordinado;
    }

    public void setProgramaCoordinado(Programa programaCoordinado) {
        this.programaCoordinado = programaCoordinado;
    }
}

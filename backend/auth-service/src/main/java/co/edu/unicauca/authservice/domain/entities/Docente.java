package co.edu.unicauca.authservice.domain.entities;



import co.edu.unicauca.shared.contracts.model.Departamento;
import co.edu.unicauca.shared.contracts.model.Programa;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

/**
 * Representa a un docente registrado en el sistema.
 *
 * <p>Hereda los atributos comunes de {@link Persona}, como
 * nombres, apellidos, celular, programa y usuario asociado.
 * Además, se le asocia un {@link Departamento} que define
 * su unidad académica dentro de la universidad.</p>
 */
@Entity
@Table(name = "docentes")
@Schema(description = "Entidad que representa a un docente de la Facultad.")
public class Docente extends Persona {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 80)
    @Schema(description = "Departamento académico al que pertenece el docente.")
    private Departamento departamento;

    protected Docente() {
        // Constructor protegido requerido por JPA
    }

    public Docente(String id,
                   String codigo,
                   String nombres,
                   String apellidos,
                   String celular,
                   Programa programa,
                   Usuario usuario,
                   Departamento departamento) {
        super(id, codigo, nombres, apellidos, celular, programa, usuario);
        this.departamento = departamento;
    }

    public Departamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
    }
}

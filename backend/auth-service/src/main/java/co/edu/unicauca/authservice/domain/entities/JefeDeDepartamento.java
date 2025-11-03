package co.edu.unicauca.authservice.domain.entities;

import co.edu.unicauca.shared.contracts.model.Programa;
import co.edu.unicauca.shared.contracts.model.Departamento;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

/**
 * Representa al jefe de un departamento académico dentro de la universidad.
 *
 * <p>Hereda de {@link Persona} y agrega el departamento que dirige.</p>
 */
@Entity
@Table(name = "jefes_departamento")
@Schema(description = "Entidad que representa al jefe de un departamento académico.")
public class JefeDeDepartamento extends Persona {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 80)
    @Schema(description = "Departamento a cargo del jefe.")
    private Departamento departamento;

    protected JefeDeDepartamento() {
        // Constructor requerido por JPA
    }

    public JefeDeDepartamento(String id,
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

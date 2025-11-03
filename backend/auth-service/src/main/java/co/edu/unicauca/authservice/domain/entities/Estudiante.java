package co.edu.unicauca.authservice.domain.entities;

import co.edu.unicauca.shared.contracts.model.Programa;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

/**
 * Representa a un estudiante registrado en el sistema.
 *
 * <p>Hereda todos los atributos de {@link Persona}, incluyendo
 * su código institucional, programa académico y cuenta de usuario
 * asociada. No define atributos adicionales.</p>
 */
@Entity
@Table(name = "estudiantes")
@Schema(description = "Entidad que representa a un estudiante de la universidad.")
public class Estudiante extends Persona {

    protected Estudiante() {
        // Constructor protegido requerido por JPA
    }

    public Estudiante(String id,
                      String codigo,
                      String nombres,
                      String apellidos,
                      String celular,
                      Programa programa,
                      Usuario usuario) {
        super(id, codigo, nombres, apellidos, celular, programa, usuario);
    }
}

package co.edu.unicauca.departmentheadservice.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "jefe_departamento")
public class JefeDeDepartamento {

    @Id
    @Column(nullable = false, unique = true)
    private String personaId; // Guardar el ID de la persona (de UserCreatedEvent)

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String departamento;

    // Constructor sin argumentos (requerido por JPA)
    protected JefeDeDepartamento() {}

    // Constructor con parámetros
    public JefeDeDepartamento(String personaId, String nombre, String departamento) {
        this.personaId = personaId;
        this.nombre = nombre;
        this.departamento = departamento;
    }

    // Getters y setters
    public String getPersonaId() {
        return personaId;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setPersonaId(String personaId) {
        this.personaId = personaId;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }
}

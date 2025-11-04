package co.edu.unicauca.departmentheadservice.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "anteproyectos")
public class Anteproyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String descripcion;
    private LocalDate fechaCreacion;

    @ManyToMany
    private List<Docente> evaluadores; // Relación con la entidad Docente

    // Constructor sin argumentos (requerido por JPA)
    protected Anteproyecto() {}

    // Constructor con parámetros
    public Anteproyecto(String titulo, String descripcion, LocalDate fechaCreacion, List<Docente> evaluadores) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaCreacion = fechaCreacion;
        this.evaluadores = evaluadores;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public List<Docente> getEvaluadores() {
        return evaluadores;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public void setEvaluadores(List<Docente> evaluadores) {
        this.evaluadores = evaluadores;
    }
}

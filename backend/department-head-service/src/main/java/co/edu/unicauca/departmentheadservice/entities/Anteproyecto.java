package co.edu.unicauca.departmentheadservice.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(
        name = "anteproyectos",
        uniqueConstraints = @UniqueConstraint(columnNames = "anteproyectoId") // evita duplicados al reenviar eventos
)
public class Anteproyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Campos provenientes del evento ---
    private Long anteproyectoId;   // id global del anteproyecto (viene del academic-service)
    private Long proyectoId;       // id del proyecto original
    private String titulo;
    private String descripcion;
    private LocalDate fechaCreacion;
    private String estudianteCorreo;
    private String directorCorreo;
    private String departamento;

    // --- Relación con docentes evaluadores ---
    @ManyToMany
    private List<Docente> evaluadores; // inicialmente vacío

    // --- Constructores ---
    protected Anteproyecto() {
    }

    public Anteproyecto(Long anteproyectoId,
                        Long proyectoId,
                        String titulo,
                        String descripcion,
                        LocalDate fechaCreacion,
                        List<Docente> evaluadores,
                        String estudianteCorreo,
                        String directorCorreo,
                        String departamento) {
        this.anteproyectoId = anteproyectoId;
        this.proyectoId = proyectoId;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaCreacion = fechaCreacion;
        this.evaluadores = evaluadores;
        this.estudianteCorreo = estudianteCorreo;
        this.directorCorreo = directorCorreo;
        this.departamento = departamento;
    }

    // --- Getters y Setters ---
    public Long getId() {
        return id;
    }

    public Long getAnteproyectoId() {
        return anteproyectoId;
    }

    public void setAnteproyectoId(Long anteproyectoId) {
        this.anteproyectoId = anteproyectoId;
    }

    public Long getProyectoId() {
        return proyectoId;
    }

    public void setProyectoId(Long proyectoId) {
        this.proyectoId = proyectoId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getEstudianteCorreo() {
        return estudianteCorreo;
    }

    public void setEstudianteCorreo(String estudianteCorreo) {
        this.estudianteCorreo = estudianteCorreo;
    }

    public String getDirectorCorreo() {
        return directorCorreo;
    }

    public void setDirectorCorreo(String directorCorreo) {
        this.directorCorreo = directorCorreo;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public List<Docente> getEvaluadores() {
        return evaluadores;
    }

    public void setEvaluadores(List<Docente> evaluadores) {
        this.evaluadores = evaluadores;
    }
}

package co.edu.unicauca.coordinatorservice.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "formato_a")
public class FormatoA {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long proyectoId;
    @ElementCollection
    private List<String> estudiantes;
    private String director;
    private String coodirector;
    private int nroVersion;
    private String nombre;
    private LocalDate fechaSubida;
    private byte[] blob;
    private EstadoFormatoA estado;
    private String tipoTrabajoGrado;

    public FormatoA(Long id, Long proyectoId, List<String> estudiantes, String director, String coodirector, int nroVersion,
                    String nombre, LocalDate fechaSubida, byte[] blob, EstadoFormatoA estado, String tipoTrabajoGrado) {
        this.id = id;
        this.proyectoId = proyectoId;
        this.estudiantes = estudiantes;
        this.director = director;
        this.coodirector = coodirector;
        this.nroVersion = nroVersion;
        this.nombre = nombre;
        this.fechaSubida = fechaSubida;
        this.blob = blob;
        this.estado = estado;
        this.tipoTrabajoGrado = tipoTrabajoGrado;
    }

    public FormatoA() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProyectoId() {
        return proyectoId;
    }

    public void setProyectoId(Long proyectoId) {
        this.proyectoId = proyectoId;
    }

    public List<String> getEstudiantes() {
        return estudiantes;
    }

    public void setEstudiantes(List<String> estudiantes) {
        this.estudiantes = estudiantes;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getCoodirector() {
        return coodirector;
    }

    public void setCoodirector(String coodirector) {
        this.coodirector = coodirector;
    }

    public int getNroVersion() {
        return nroVersion;
    }

    public void setNroVersion(int nroVersion) {
        this.nroVersion = nroVersion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDate getFechaSubida() {
        return fechaSubida;
    }

    public void setFechaSubida(LocalDate fechaSubida) {
        this.fechaSubida = fechaSubida;
    }

    public byte[] getBlob() {
        return blob;
    }

    public void setBlob(byte[] blob) {
        this.blob = blob;
    }

    public EstadoFormatoA getEstado() {
        return estado;
    }

    public void setEstado(EstadoFormatoA estado) {
        this.estado = estado;
    }

    public String getTipoTrabajoGrado() {
        return tipoTrabajoGrado;
    }

    public void setTipoTrabajoGrado(String tipoTrabajoGrado) {
        this.tipoTrabajoGrado = tipoTrabajoGrado;
    }
}

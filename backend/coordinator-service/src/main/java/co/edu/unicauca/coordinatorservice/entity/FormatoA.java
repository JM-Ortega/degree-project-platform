package co.edu.unicauca.coordinatorservice.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import co.edu.unicauca.coordinatorservice.infra.DTOS.EstadoFormatoA;
import co.edu.unicauca.coordinatorservice.infra.DTOS.TipoProyecto;

@Entity
@Table(name = "formato_a")
public class FormatoA implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "proyecto_id", nullable = false)
    private Long proyectoId;

    @Column(name = "nro_version",  nullable = false)
    private int nroVersion;

    @Column(name = "nombre_formato_a")
    private String nombreFormatoA;

    @Column(name = "nombre_proyecto")
    private String nombreProyecto;

    @Column(name = "fecha_subida")
    private LocalDate fechaSubida;

    @Lob
    @Column(name = "blob")
    private byte[] blob;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_formato_a")
    private EstadoFormatoA estadoFormatoA;

    @ManyToMany
    @JoinTable(
            name = "formato_a_estudiantes",
            joinColumns = @JoinColumn(name = "formato_a_id"),
            inverseJoinColumns = @JoinColumn(name = "estudiante_id")
    )
    private List<Estudiante> estudiantes;

    @ManyToOne
    @JoinColumn(name = "director_id")
    private Docente director;

    @ManyToOne
    @JoinColumn(name = "coodirector_id")
    private Docente coodirector;

    @Enumerated(EnumType.STRING)
    private TipoProyecto tipoProyecto;

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

    public int getNroVersion() {
        return nroVersion;
    }

    public void setNroVersion(int nroVersion) {
        this.nroVersion = nroVersion;
    }

    public String getNombreFormatoA() {
        return nombreFormatoA;
    }

    public void setNombreFormatoA(String nombreFormatoA) {
        this.nombreFormatoA = nombreFormatoA;
    }

    public String getNombreProyecto() {
        return nombreProyecto;
    }

    public void setNombreProyecto(String nombreProyecto) {
        this.nombreProyecto = nombreProyecto;
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

    public EstadoFormatoA getEstadoFormatoA() {
        return estadoFormatoA;
    }

    public void setEstadoFormatoA(EstadoFormatoA estadoFormatoA) {
        this.estadoFormatoA = estadoFormatoA;
    }

    public List<Estudiante> getEstudiantes() {
        return estudiantes;
    }

    public void setEstudiantes(List<Estudiante> estudiantes) {
        this.estudiantes = estudiantes;
    }

    public Docente getDirector() {
        return director;
    }

    public void setDirector(Docente director) {
        this.director = director;
    }

    public Docente getCoodirector() {
        return coodirector;
    }

    public void setCoodirector(Docente coodirector) {
        this.coodirector = coodirector;
    }

    public TipoProyecto getTipoProyecto() {
        return tipoProyecto;
    }

    public void setTipoProyecto(TipoProyecto tipoProyecto) {
        this.tipoProyecto = tipoProyecto;
    }
}

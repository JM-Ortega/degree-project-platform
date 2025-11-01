package co.edu.unicauca.coordinatorservice.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "formato_a")
public class FormatoA implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "proyecto_id")
    private Long proyectoId;

    @Column(name = "nro_version")
    private int nroVersion;

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

    // Lista de correos de los estudiantes en formato JSON o TEXT
    @Convert(converter = StringListConverter.class)
    @Column(name = "estudiantes_email", columnDefinition = "TEXT")
    private List<String> estudiantesEmail;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "nombres", column = @Column(name = "director_nombres")),
            @AttributeOverride(name = "apellidos", column = @Column(name = "director_apellidos")),
            @AttributeOverride(name = "email", column = @Column(name = "director_email"))
    })
    private DocenteEmbeddable director;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "nombres", column = @Column(name = "coodirector_nombres")),
            @AttributeOverride(name = "apellidos", column = @Column(name = "coodirector_apellidos")),
            @AttributeOverride(name = "email", column = @Column(name = "coodirector_email"))
    })
    private DocenteEmbeddable coodirector;

    @Enumerated(EnumType.STRING)
    private TipoProyecto tipoProyecto;

    @Enumerated(EnumType.STRING)
    private EstadoProyecto estadoProyecto;

    public FormatoA(Long id, Long proyectoId, int nroVersion, String nombreProyecto, LocalDate fechaSubida, byte[] blob, EstadoFormatoA estadoFormatoA,
                    List<String> estudiantesEmail, DocenteEmbeddable director, DocenteEmbeddable coodirector, TipoProyecto tipoProyecto,
                    EstadoProyecto estadoProyecto) {
        this.id = id;
        this.proyectoId = proyectoId;
        this.nroVersion = nroVersion;
        this.nombreProyecto = nombreProyecto;
        this.fechaSubida = fechaSubida;
        this.blob = blob;
        this.estadoFormatoA = estadoFormatoA;
        this.estudiantesEmail = estudiantesEmail;
        this.director = director;
        this.coodirector = coodirector;
        this.tipoProyecto = tipoProyecto;
        this.estadoProyecto = estadoProyecto;
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

    public int getNroVersion() {
        return nroVersion;
    }

    public void setNroVersion(int nroVersion) {
        this.nroVersion = nroVersion;
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

    public List<String> getEstudiantesEmail() {
        return estudiantesEmail;
    }

    public void setEstudiantesEmail(List<String> estudiantesEmail) {
        this.estudiantesEmail = estudiantesEmail;
    }

    public DocenteEmbeddable getDirector() {
        return director;
    }

    public void setDirector(DocenteEmbeddable director) {
        this.director = director;
    }

    public DocenteEmbeddable getCoodirector() {
        return coodirector;
    }

    public void setCoodirector(DocenteEmbeddable coodirector) {
        this.coodirector = coodirector;
    }

    public TipoProyecto getTipoProyecto() {
        return tipoProyecto;
    }

    public void setTipoProyecto(TipoProyecto tipoProyecto) {
        this.tipoProyecto = tipoProyecto;
    }

    public EstadoProyecto getEstadoProyecto() {
        return estadoProyecto;
    }

    public void setEstadoProyecto(EstadoProyecto estadoProyecto) {
        this.estadoProyecto = estadoProyecto;
    }
}

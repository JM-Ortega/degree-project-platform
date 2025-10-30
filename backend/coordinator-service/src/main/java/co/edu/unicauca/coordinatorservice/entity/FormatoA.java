package co.edu.unicauca.coordinatorservice.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "formato_a")
public class FormatoA {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "proyecto_id")
    private Long proyectoId;

    @Convert(converter = StringListConverter.class)
    @Column(name = "estudiantes", columnDefinition = "TEXT")
    private List<String> estudiantes;

    private String director;

    @Column(name = "coodirector")
    private String coodirector;

    @Column(name = "nro_version")
    private int nroVersion;

    private String nombre;

    @Column(name = "fecha_subida", columnDefinition = "TEXT")
    private LocalDateTime fechaSubida;

    @Enumerated(EnumType.STRING)
    private EstadoFormatoA estado;

    @Column(name = "tipo_trabajo_grado")
    private String tipoTrabajoGrado;

    /**
     * Guardamos el archivo en Base64 como texto.
     * Esto evita problemas de serialización y facilita enviar al front.
     */
    @Column(name = "archivo_base64", columnDefinition = "TEXT")
    private String archivoBase64;

    public FormatoA(Long id, Long proyectoId, List<String> estudiantes, String director, String coodirector, int nroVersion,
                    String nombre, LocalDateTime fechaSubida, String archivoBase64, EstadoFormatoA estado, String tipoTrabajoGrado) {
        this.id = id;
        this.proyectoId = proyectoId;
        this.estudiantes = estudiantes;
        this.director = director;
        this.coodirector = coodirector;
        this.nroVersion = nroVersion;
        this.nombre = nombre;
        this.fechaSubida = fechaSubida;
        this.archivoBase64 = archivoBase64;
        this.estado = estado;
        this.tipoTrabajoGrado = tipoTrabajoGrado;
    }

    public FormatoA() {

    }

    public List<String> getEstudiantes() {
        return estudiantes;
    }

    public void setEstudiantes(List<String> estudiantes) {
        this.estudiantes = estudiantes;
    }

    public Long getProyectoId() {
        return proyectoId;
    }

    public void setProyectoId(Long proyectoId) {
        this.proyectoId = proyectoId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getFechaSubida() {
        return fechaSubida;
    }

    public void setFechaSubida(LocalDateTime fechaSubida) {
        this.fechaSubida = fechaSubida;
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

    public String getArchivoBase64() {
        return archivoBase64;
    }

    public void setArchivoBase64(String archivoBase64) {
        this.archivoBase64 = archivoBase64;
    }
}

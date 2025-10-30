package co.edu.unicauca.coordinatorservice.infra;

import co.edu.unicauca.coordinatorservice.entity.EstadoFormatoA;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL) // Omite campos nulos al enviar al front
public class FormatoADTO {

    private Long id;
    private Long proyectoId;
    private List<String> estudiantes;
    private String director;
    private String coodirector;
    private int nroVersion;
    private String nombre;
    private LocalDateTime fechaSubida;
    private EstadoFormatoA estado;
    private String tipoTrabajoGrado;

    // Campo opcional: solo presente cuando viene por RabbitMQ
    private String archivoBase64;

    // ──────── Constructores ────────

    public FormatoADTO() {
    }

    // Constructor completo (para mensajes RabbitMQ)
    public FormatoADTO(Long id, Long proyectoId, List<String> estudiantes, String director,
                       String coodirector, int nroVersion, String nombre, LocalDateTime fechaSubida,
                       EstadoFormatoA estado, String tipoTrabajoGrado, String archivoBase64) {
        this.id = id;
        this.proyectoId = proyectoId;
        this.estudiantes = estudiantes;
        this.director = director;
        this.coodirector = coodirector;
        this.nroVersion = nroVersion;
        this.nombre = nombre;
        this.fechaSubida = fechaSubida;
        this.estado = estado;
        this.tipoTrabajoGrado = tipoTrabajoGrado;
        this.archivoBase64 = archivoBase64;
    }

    // Constructor ligero (para enviar al frontend sin blob)
    public FormatoADTO(Long id, List<String> estudiantes, String director,
                       String coodirector, int nroVersion, String nombre,
                       LocalDateTime fechaSubida, String estado, String tipoTrabajoGrado) {
        this.id = id;
        this.estudiantes = estudiantes;
        this.director = director;
        this.coodirector = coodirector;
        this.nroVersion = nroVersion;
        this.nombre = nombre;
        this.fechaSubida = fechaSubida;
        this.estado = EstadoFormatoA.valueOf(estado);
        this.tipoTrabajoGrado = tipoTrabajoGrado;
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

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public List<String> getEstudiantes() {
        return estudiantes;
    }

    public void setEstudiantes(List<String> estudiantes) {
        this.estudiantes = estudiantes;
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

    // --- Este método devuelve el archivo como byte[] (por si necesitas reconstruirlo) ---
    public byte[] getArchivo() {
        if (archivoBase64 == null || archivoBase64.isEmpty()) {
            return null;
        }
        return Base64.getDecoder().decode(archivoBase64);
    }

    // --- Este método permite setear el archivo como byte[] directamente ---
    public void setArchivo(byte[] archivo) {
        if (archivo == null) {
            this.archivoBase64 = null;
        } else {
            this.archivoBase64 = Base64.getEncoder().encodeToString(archivo);
        }
    }
}

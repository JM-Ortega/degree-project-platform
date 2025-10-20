package co.edu.unicauca.coordinatorservice.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "formato_a")
public class FormatoA {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long proyectoId;
    private int nroVersion;
    private String nombre;
    private LocalDate fechaSubida;
    private byte[] blob;
    private EstadoFormatoA estado;

    public FormatoA(Long id, Long proyectoId, int nroVersion, String nombre, LocalDate fechaSubida, byte[] blob, EstadoFormatoA estado) {
        this.id = id;
        this.proyectoId = proyectoId;
        this.nroVersion = nroVersion;
        this.nombre = nombre;
        this.fechaSubida = fechaSubida;
        this.blob = blob;
        this.estado = estado;
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

    public EstadoFormatoA getEstado() {
        return estado;
    }

    public void setEstado(EstadoFormatoA estado) {
        this.estado = estado;
    }

    public byte[] getBlob() {
        return blob;
    }

    public void setBlob(byte[] blob) {
        this.blob = blob;
    }
}

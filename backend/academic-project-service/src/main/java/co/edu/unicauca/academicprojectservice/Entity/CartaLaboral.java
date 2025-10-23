package co.edu.unicauca.academicprojectservice.Entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "cartaLaboral")
public class CartaLaboral {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombreCartaLaboral;
    @Temporal(TemporalType.DATE)
    private Date fechaCreacion;
    private byte[] blob;
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_archivo", nullable = false)
    private EstadoArchivo estado;

    @OneToOne(mappedBy = "cartaLaboral", cascade = CascadeType.ALL)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Proyecto proyecto;

    public CartaLaboral() {}

    public byte[] getBlob() {
        return blob;
    }

    public void setBlob(byte[] blob) {
        this.blob = blob;
    }

    public EstadoArchivo getEstado() {
        return estado;
    }

    public void setEstado(EstadoArchivo estado) {
        this.estado = estado;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreCartaLaboral() {
        return nombreCartaLaboral;
    }

    public void setNombreCartaLaboral(String nombreCartaLaboral) {
        this.nombreCartaLaboral = nombreCartaLaboral;
    }

    public Proyecto getProyecto() {
        return proyecto;
    }

    public void setProyecto(Proyecto proyecto) {
        this.proyecto = proyecto;
    }
}

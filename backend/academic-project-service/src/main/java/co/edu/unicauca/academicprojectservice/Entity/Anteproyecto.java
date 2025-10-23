package co.edu.unicauca.academicprojectservice.Entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "anteproyecto")
public class Anteproyecto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int nroVersion;
    private String nombreAnteproyecto;
    @Temporal(TemporalType.DATE)
    private Date fechaCreacion;
    private byte[] blob;
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_archivo", nullable = false)
    private EstadoArchivo estado;

    @OneToOne(mappedBy = "anteproyecto", cascade = CascadeType.ALL)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Proyecto proyecto;

    public Anteproyecto() {}

    // Getters y setters
    public Long getId() { return id; }

    public Date getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Date fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public Proyecto getProyecto() { return proyecto; }
    public void setProyecto(Proyecto proyecto) { this.proyecto = proyecto; }

    public byte[] getBlob() { return blob; }
    public void setBlob(byte[] blob) { this.blob = blob; }

    public EstadoArchivo getEstado() { return estado; }
    public void setEstado(EstadoArchivo estado) { this.estado = estado; }

    public int getNroVersion() { return nroVersion; }
    public void setNroVersion(int nroVersion) { this.nroVersion = nroVersion; }

    public String getNombreAnteproyecto() { return nombreAnteproyecto; }
    public void setNombreAnteproyecto(String nombreAnteproyecto) { this.nombreAnteproyecto = nombreAnteproyecto; }

}

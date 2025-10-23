package co.edu.unicauca.academicprojectservice.Entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "proyecto")
public class Proyecto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;

    @ManyToMany
    @JoinTable(
            name = "trabajo_estudiantes",
            joinColumns = @JoinColumn(name = "trabajo_id"),
            inverseJoinColumns = @JoinColumn(name = "estudiante_id")
    )
    @com.fasterxml.jackson.annotation.JsonIgnore

    private List<Estudiante> estudiantes;

    @ManyToOne
    @JoinColumn(name = "director_id", nullable = false)
    private Docente director;

    @ManyToOne
    @JoinColumn(name = "codirector_id")
    private Docente codirector;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "anteproyecto_id")
    private Anteproyecto anteproyecto;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "formatoA_id")
    private FormatoA formatoA;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "cartaLaboral_id")
    private CartaLaboral cartaLaboral;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_proyecto", nullable = false)
    private TipoProyecto tipoProyecto;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_proyecto", nullable = false)
    private EstadoProyecto estado;

    public Proyecto() {}

    public Long getId() { return id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public List<Estudiante> getEstudiantes() { return estudiantes; }
    public void setEstudiantes(List<Estudiante> estudiantes) { this.estudiantes = estudiantes; }

    public Docente getDirector() { return director; }
    public void setDirector(Docente director) { this.director = director; }

    public Docente getCodirector() { return codirector; }
    public void setCodirector(Docente codirector) { this.codirector = codirector; }

    public Anteproyecto getAnteproyecto() { return anteproyecto; }
    public void setAnteproyecto(Anteproyecto anteproyecto) { this.anteproyecto = anteproyecto; }

    public TipoProyecto getTipoProyecto() { return tipoProyecto; }
    public void setTipoProyecto(TipoProyecto tipoProyecto) { this.tipoProyecto = tipoProyecto; }

    public FormatoA getFormatoA() { return formatoA; }
    public void setFormatoA(FormatoA formatoA) { this.formatoA = formatoA; }

    public CartaLaboral getCartaLaboral() { return cartaLaboral; }
    public void setCartaLaboral(CartaLaboral cartaLaboral) { this.cartaLaboral = cartaLaboral; }

    public EstadoProyecto getEstado() { return estado; }
    public void setEstado(EstadoProyecto estado) { this.estado = estado; }
}

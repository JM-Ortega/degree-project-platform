package co.edu.unicauca.academicprojectservice.infra.dto;

import co.edu.unicauca.academicprojectservice.Entity.*;

public class ProyectoDTO {
    private long id;
    private String titulo;
    private String estudiante;
    private String director;
    private Anteproyecto anteproyecto;
    private FormatoA formatoA;
    private CartaLaboral cartaLaboral;
    private TipoProyecto tipoProyecto;
    private EstadoProyecto estadoProyecto;

    public long getId() {return id;}
    public void setId(long id) {this.id = id;}

    public Anteproyecto getAnteproyecto() {return anteproyecto;}
    public void setAnteproyecto(Anteproyecto anteproyecto) {this.anteproyecto = anteproyecto;}

    public CartaLaboral getCartaLaboral() {return cartaLaboral;}
    public void setCartaLaboral(CartaLaboral cartaLaboral) {this.cartaLaboral = cartaLaboral;}

    public String getDirector() {return director;}
    public void setDirector(String director) {this.director = director;}

    public EstadoProyecto getEstadoProyecto() {return estadoProyecto;}
    public void setEstadoProyecto(EstadoProyecto estadoProyecto) {this.estadoProyecto = estadoProyecto;}

    public String getEstudiante() {return estudiante;}
    public void setEstudiante(String estudiante) {this.estudiante = estudiante;}

    public FormatoA getFormatoA() {return formatoA;}
    public void setFormatoA(FormatoA formatoA) {this.formatoA = formatoA;}

    public TipoProyecto getTipoProyecto() {return tipoProyecto;}
    public void setTipoProyecto(TipoProyecto tipoProyecto) {this.tipoProyecto = tipoProyecto;}

    public String getTitulo() {return titulo;}
    public void setTitulo(String titulo) {this.titulo = titulo;}
}

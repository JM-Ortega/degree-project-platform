package co.edu.unicauca.frontend.entities;

public class ProyectoDTO {
    private long id;
    private String titulo;
    private String estudiante;
    private String director;
    private AnteproyectoDTO anteproyectoDTO;
    private FormatoADTO formatoADTO;
    private CartaLaboralDTO cartaLaboralDTO;
    private TipoProyecto tipoProyecto;
    private EstadoProyecto estadoProyecto;

    public long getId() {return id;}
    public void setId(long id) {this.id = id;}

    public AnteproyectoDTO getAnteproyecto() {
        return anteproyectoDTO;
    }
    public void setAnteproyecto(AnteproyectoDTO anteproyectoDTO) {
        this.anteproyectoDTO = anteproyectoDTO;
    }

    public CartaLaboralDTO getCartaLaboral() {
        return cartaLaboralDTO;
    }
    public void setCartaLaboral(CartaLaboralDTO cartaLaboralDTO) {
        this.cartaLaboralDTO = cartaLaboralDTO;
    }

    public String getDirector() {
        return director;
    }
    public void setDirector(String director) {
        this.director = director;
    }

    public EstadoProyecto getEstadoProyecto() {
        return estadoProyecto;
    }
    public void setEstadoProyecto(EstadoProyecto estadoProyecto) {
        this.estadoProyecto = estadoProyecto;
    }

    public String getEstudiante() {
        return estudiante;
    }
    public void setEstudiante(String estudiante) {
        this.estudiante = estudiante;
    }

    public FormatoADTO getFormatoA() {
        return formatoADTO;
    }
    public void setFormatoA(FormatoADTO formatoADTO) {
        this.formatoADTO = formatoADTO;
    }

    public TipoProyecto getTipoProyecto() {
        return tipoProyecto;
    }
    public void setTipoProyecto(TipoProyecto tipoProyecto) {
        this.tipoProyecto = tipoProyecto;
    }

    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
}

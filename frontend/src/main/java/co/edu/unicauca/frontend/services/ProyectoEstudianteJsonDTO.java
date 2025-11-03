package co.edu.unicauca.frontend.services;

public class ProyectoEstudianteJsonDTO {
    private Long id;
    private String titulo;
    private String nombreDirector;
    private String tipoProyecto;
    private String estadoProyecto;

    public ProyectoEstudianteJsonDTO() {} // necesario para Jackson

    // getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getNombreDirector() { return nombreDirector; }
    public void setNombreDirector(String nombreDirector) { this.nombreDirector = nombreDirector; }

    public String getTipoProyecto() { return tipoProyecto; }
    public void setTipoProyecto(String tipoProyecto) { this.tipoProyecto = tipoProyecto; }

    public String getEstadoProyecto() { return estadoProyecto; }
    public void setEstadoProyecto(String estadoProyecto) { this.estadoProyecto = estadoProyecto; }
}


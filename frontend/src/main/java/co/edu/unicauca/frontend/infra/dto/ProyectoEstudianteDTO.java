package co.edu.unicauca.frontend.infra.dto;

import javafx.beans.property.*;
import java.time.LocalDate;

public class ProyectoEstudianteDTO {
    private LongProperty id = new SimpleLongProperty();
    private StringProperty titulo = new SimpleStringProperty();
    private StringProperty nombreDirector = new SimpleStringProperty();
    private StringProperty tipoProyecto = new SimpleStringProperty();
    private StringProperty estadoProyecto = new SimpleStringProperty();

    public ProyectoEstudianteDTO(Long id, String titulo, String nombreDirector,
                           String tipoProyecto, String estadoProyecto) {
        this.id = new SimpleLongProperty(id);
        this.titulo = new SimpleStringProperty(titulo);
        this.nombreDirector = new SimpleStringProperty(nombreDirector);
        this.tipoProyecto = new SimpleStringProperty(tipoProyecto);
        this.estadoProyecto = new SimpleStringProperty(estadoProyecto);
    }

    // Getters para las columnas
    public Long getId() { return id.get(); }
    public String getTitulo() { return titulo.get(); }
    public String getNombreDirector() { return nombreDirector.get(); }
    public String getTipoProyecto() { return tipoProyecto.get(); }
    public String getEstadoProyecto() { return estadoProyecto.get(); }

    // Properties (para TableView)
    public LongProperty idProperty() { return id; }
    public StringProperty tituloProperty() { return titulo; }
    public StringProperty nombreDirectorProperty() { return nombreDirector; }
    public StringProperty tipoProyectoProperty() { return tipoProyecto; }
    public StringProperty estadoProyectoProperty() { return estadoProyecto; }
}

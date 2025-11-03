package co.edu.unicauca.frontend.infra.dto;

import javafx.beans.property.*;
import java.time.LocalDate;

public class ProyectoEstudianteDTO {
    private LongProperty id = new SimpleLongProperty();
    private StringProperty titulo = new SimpleStringProperty();
    private StringProperty nombreDirector = new SimpleStringProperty();
    private StringProperty tipoProyecto = new SimpleStringProperty();
    private ObjectProperty<LocalDate> fechaEmision = new SimpleObjectProperty<>();
    private StringProperty estadoProyecto = new SimpleStringProperty();

    public ProyectoEstudianteDTO(Long id, String titulo, String nombreDirector,
                           String tipoProyecto, LocalDate fechaEmision, String estadoProyecto) {
        this.id = new SimpleLongProperty(id);
        this.titulo = new SimpleStringProperty(titulo);
        this.nombreDirector = new SimpleStringProperty(nombreDirector);
        this.tipoProyecto = new SimpleStringProperty(tipoProyecto);
        this.fechaEmision = new SimpleObjectProperty<>(fechaEmision);
        this.estadoProyecto = new SimpleStringProperty(estadoProyecto);
    }

    // Getters para las columnas
    public Long getId() { return id.get(); }
    public String getTitulo() { return titulo.get(); }
    public String getNombreDirector() { return nombreDirector.get(); }
    public String getTipoProyecto() { return tipoProyecto.get(); }
    public LocalDate getFechaEmision() { return fechaEmision.get(); }
    public String getEstadoProyecto() { return estadoProyecto.get(); }

    // Properties (para TableView)
    public LongProperty idProperty() { return id; }
    public StringProperty tituloProperty() { return titulo; }
    public StringProperty nombreDirectorProperty() { return nombreDirector; }
    public StringProperty tipoProyectoProperty() { return tipoProyecto; }
    public ObjectProperty<LocalDate> fechaEmisionProperty() { return fechaEmision; }
    public StringProperty estadoProyectoProperty() { return estadoProyecto; }
}

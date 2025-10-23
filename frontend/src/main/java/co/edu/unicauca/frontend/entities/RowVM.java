package co.edu.unicauca.frontend.entities;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;

public class RowVM {
    private final long archivoId;
    private final long proyectoId;
    private final StringProperty nombreProyecto = new SimpleStringProperty();
    private final StringProperty nombreDocente = new SimpleStringProperty();
    private final StringProperty tipoA = new SimpleStringProperty();
    private final StringProperty tipoP = new SimpleStringProperty();
    private final StringProperty fecha = new SimpleStringProperty();
    private final StringProperty estado = new SimpleStringProperty();
    private final StringProperty correoProfesor = new SimpleStringProperty();
    private final StringProperty correoEstudiante = new SimpleStringProperty();

    public RowVM(long archivoId, long proyectoId, String nombreProyecto, String nombreDocente, String tipoA, String tipoP,
                 LocalDate fecha, String estado, String correoProfesor, String correoEstudiante) {
        this.archivoId = archivoId;
        this.proyectoId = proyectoId;
        this.nombreProyecto.set(nombreProyecto);
        this.nombreDocente.set(nombreDocente);
        this.tipoA.set(tipoA);
        this.tipoP.set(tipoP);
        this.fecha.set(String.valueOf(fecha));
        this.estado.set(estado);
        this.correoProfesor.set(correoProfesor);
        this.correoEstudiante.set(correoEstudiante);
    }

    public long archivoId() {return archivoId;}
    public long proyectoId() {return proyectoId;}
    public StringProperty nombreProyectoProperty() { return nombreProyecto; }
    public StringProperty nombreDocenteProperty() { return nombreDocente; }
    public StringProperty tipoAProperty() { return tipoA; }
    public StringProperty tipoPProperty() { return tipoP; }
    public StringProperty fechaProperty() { return fecha; }
    public StringProperty estadoProperty() { return estado; }
    public StringProperty correoProfesor() { return correoProfesor; }
    public StringProperty correoEstudiante() { return correoEstudiante; }
}

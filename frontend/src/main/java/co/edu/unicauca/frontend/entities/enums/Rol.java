package co.edu.unicauca.frontend.entities.enums;

/**
 * Roles globales reconocidos por todos los microservicios.
 */
public enum Rol {
    ESTUDIANTE("Estudiante"),
    DOCENTE("Docente"),
    COORDINADOR("Coordinador"),
    JEFE_DE_DEPARTAMENTO("Jefe de Departamento");

    private final String nombreDisplay;

    Rol(String nombreDisplay) {
        this.nombreDisplay = nombreDisplay;
    }

    @Override
    public String toString() {
        return nombreDisplay;
    }

    public String getNombreDisplay() {
        return nombreDisplay;
    }
}
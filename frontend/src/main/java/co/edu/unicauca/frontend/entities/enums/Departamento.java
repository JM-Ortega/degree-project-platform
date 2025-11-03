package co.edu.unicauca.frontend.entities.enums;

public enum Departamento {
    SISTEMAS("Sistemas"),
    ELECTRONICA_INSTRUMENTACION_Y_CONTROL("Electrónica, Instrumentación y Control"),
    TELEMATICA("Telemática"),
    TELECOMUNICACIONES("Telecomunicaciones");

    private final String nombreDisplay;

    Departamento(String nombreDisplay) {
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
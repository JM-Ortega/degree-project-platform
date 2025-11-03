package co.edu.unicauca.frontend.entities.enums;

public enum Programa {
    INGENIERIA_DE_SISTEMAS("Ingeniería de Sistemas"),
    INGENIERIA_ELECTRONICA_Y_TELECOMUNICACIONES("Ingeniería Electrónica y Telecomunicaciones"),
    AUTOMATICA_INDUSTRIAL("Automática Industrial"),
    TECNOLOGIA_EN_TELEMATICA("Tecnología en Telemática");

    private final String nombreDisplay;

    Programa(String nombreDisplay) {
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
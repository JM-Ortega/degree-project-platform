package co.edu.unicauca.coordinatorservice.entity;

public enum TipoProyecto {
    TRABAJO_DE_INVESTIGACION ("Trabajo de investigación"),
    PRACTICA_PROFESIONAL("Practica profecional");

    private final String displayName;

    TipoProyecto(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}

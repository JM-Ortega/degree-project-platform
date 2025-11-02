package co.edu.unicauca.coordinatorservice.infra.DTOSInternos;

public class FormatoAUpdateRequest {
    private String estadoFormatoA;
    private String blob;

    // Getters y Setters
    public String getEstadoFormatoA() {
        return estadoFormatoA;
    }

    public void setEstadoFormatoA(String estadoFormatoA) {
        this.estadoFormatoA = estadoFormatoA;
    }

    public String getBlob() {
        return blob;
    }

    public void setBlob(String blob) {
        this.blob = blob;
    }
}


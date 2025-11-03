package co.edu.unicauca.frontend.entities;

import java.util.Date;

public class CartaLaboralDTO {
    private String nombreCartaLaboral;
    private Date fechaCreacion;
    private byte[] blob;

    public byte[] getBlob() {
        return blob;
    }

    public void setBlob(byte[] blob) {
        this.blob = blob;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getNombreCartaLaboral() {
        return nombreCartaLaboral;
    }

    public void setNombreCartaLaboral(String nombreCartaLaboral) {
        this.nombreCartaLaboral = nombreCartaLaboral;
    }

}

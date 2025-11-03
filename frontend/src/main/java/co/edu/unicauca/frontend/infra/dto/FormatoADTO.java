package co.edu.unicauca.frontend.entities;

import java.util.Date;

public class FormatoADTO {
    private String nombreFormato;
    private byte[] blob;
    private int nroVersion;
    private Date fechaCreacion;
    private EstadoArchivo estado;

    public byte[] getBlob() {
        return blob;
    }
    public void setBlob(byte[] blob) {
        this.blob = blob;
    }

    public EstadoArchivo getEstado() {
        return estado;
    }
    public void setEstado(EstadoArchivo estado) {
        this.estado = estado;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }
    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getNombreFormato() {
        return nombreFormato;
    }
    public void setNombreFormato(String nombreFormato) {
        this.nombreFormato = nombreFormato;
    }

    public int getNroVersion() {
        return nroVersion;
    }
    public void setNroVersion(int nroVersion) {
        this.nroVersion = nroVersion;
    }
}

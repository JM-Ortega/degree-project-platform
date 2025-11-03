package co.edu.unicauca.academicprojectservice.infra.dto;

import co.edu.unicauca.academicprojectservice.Entity.EstadoArchivo;

public class EstadoRequest {
    private EstadoArchivo estado;

    public EstadoArchivo getEstado() {
        return estado;
    }

    public void setEstado(EstadoArchivo estado) {
        this.estado = estado;
    }
}


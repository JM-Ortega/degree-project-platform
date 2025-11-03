package co.edu.unicauca.academicprojectservice.infra.dto;

import co.edu.unicauca.academicprojectservice.Entity.EstadoFormatoA;

public class EstadoRequest {
    private EstadoFormatoA estado;

    public EstadoFormatoA getEstado() {
        return estado;
    }

    public void setEstado(EstadoFormatoA estado) {
        this.estado = estado;
    }
}


package co.edu.unicauca.frontend.entities;

import co.edu.unicauca.frontend.infra.dto.UsuarioDTO;

public class SesionFront {

    private static SesionFront instancia;
    private UsuarioDTO usuarioActivo;

    private SesionFront() {}

    public static SesionFront getInstancia() {
        if (instancia == null) {
            instancia = new SesionFront();
        }
        return instancia;
    }

    public UsuarioDTO getUsuarioActivo() {
        return usuarioActivo;
    }

    public void setUsuarioActivo(UsuarioDTO usuarioActivo) {
        this.usuarioActivo = usuarioActivo;
    }
}

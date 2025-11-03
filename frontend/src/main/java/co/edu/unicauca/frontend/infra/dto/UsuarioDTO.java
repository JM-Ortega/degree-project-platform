package co.edu.unicauca.frontend.infra.dto;

public class UsuarioDTO {
    private String nombre;
    private String correo;
    private String rol;

    public UsuarioDTO() {}

    public String getNombre() {return nombre;}
    public void setNombre(String nombre) {this.nombre = nombre;}

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getRol() {return rol;}
    public void setRol(String rol) {this.rol = rol;}
}
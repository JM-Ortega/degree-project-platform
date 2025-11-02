package co.edu.unicauca.coordinatorservice.entity;

import jakarta.persistence.*;

@Embeddable
public class DocenteEmbeddable {
    private String nombres;
    private String apellidos;
    private String email;

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

package co.edu.unicauca.frontend.dto;

import java.util.List;

/**
 * DTO que el frontend envía al gateway para registrar una persona.
 * Debe coincidir con la estructura que espera el auth-service.
 */
public class RegistroPersonaDto {

    private String nombres;
    private String apellidos;
    private String email;
    private String password;
    private String celular;
    /**
     * Nombre del enum Programa tal como lo define shared-contracts.
     * Ej: "IngenieriaDeSistemas"
     */
    private String programa;
    /**
     * Lista de nombres de los roles. Ej: ["Estudiante"] o ["Docente","Estudiante"]
     */
    private List<String> roles;
    /**
     * Puede ser null si el rol no lo requiere.
     */
    private String departamento;

    public RegistroPersonaDto() {
    }

    public RegistroPersonaDto(String nombres,
                              String apellidos,
                              String email,
                              String password,
                              String celular,
                              String programa,
                              List<String> roles,
                              String departamento) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.email = email;
        this.password = password;
        this.celular = celular;
        this.programa = programa;
        this.roles = roles;
        this.departamento = departamento;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getPrograma() {
        return programa;
    }

    public void setPrograma(String programa) {
        this.programa = programa;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }
}

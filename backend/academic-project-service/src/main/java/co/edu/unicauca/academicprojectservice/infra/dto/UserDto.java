package co.edu.unicauca.academicprojectservice.infra.dto;

public class UserDto {
    private String nombres;
    private String apellidos;
    private String correo;
    private String celular;
    private String Rol;
    private String departamento;
    private String programa;
    private String codigo;

    public UserDto() {}

    public String getPrograma() {return programa;}
    public void setPrograma(String programa) {this.programa = programa;}

    public String getApellidos() {return apellidos;}
    public void setApellidos(String apellidos) {this.apellidos = apellidos;}

    public String getCelular() {return celular;}
    public void setCelular(String celular) {this.celular = celular;}

    public String getCodigo() {return codigo;}
    public void setCodigo(String codigo) {this.codigo = codigo;}

    public String getDepartamento() {return departamento;}
    public void setDepartamento(String departamento) {this.departamento = departamento;}

    public String getCorreo() {return correo;}
    public void setCorreo(String correo) {this.correo = correo;}

    public String getNombres() {return nombres;}
    public void setNombres(String nombres) {this.nombres = nombres;}

    public String getRol() {return Rol;}
    public void setRol(String rol) {Rol = rol;}
}

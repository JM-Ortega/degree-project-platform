package co.edu.unicauca.frontend.entities;


public class DocenteDTO {
    private Long id;
    private String nombre;
    private String correo;
    private String departamento;

    public DocenteDTO() {}

    public DocenteDTO(Long id, String nombre, String correo, String departamento) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.departamento = departamento;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }
}
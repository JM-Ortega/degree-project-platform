package co.edu.unicauca.academicprojectservice.Entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "docente")
public class Docente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombres;
    private String apellidos;
    private String celular;
    private String correo;
    @Enumerated(EnumType.STRING)
    private Departamento departamento;
    @OneToMany(mappedBy = "director")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Proyecto> trabajosComoDirector;
    @OneToMany(mappedBy = "codirector")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Proyecto> trabajosComoCodirector;
    private String codigoDocente;

    public Docente() {}

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getCodigoDocente() {
        return codigoDocente;
    }

    public void setCodigoDocente(String codigoDocente) {
        this.codigoDocente = codigoDocente;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public Departamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public List<Proyecto> getTrabajosComoCodirector() {
        return trabajosComoCodirector;
    }

    public void setTrabajosComoCodirector(List<Proyecto> trabajosComoCodirector) {
        this.trabajosComoCodirector = trabajosComoCodirector;
    }

    public List<Proyecto> getTrabajosComoDirector() {
        return trabajosComoDirector;
    }

    public void setTrabajosComoDirector(List<Proyecto> trabajosComoDirector) {
        this.trabajosComoDirector = trabajosComoDirector;
    }
}

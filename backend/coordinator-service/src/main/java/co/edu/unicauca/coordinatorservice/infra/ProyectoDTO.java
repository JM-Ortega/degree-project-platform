package co.edu.unicauca.coordinatorservice.infra;

import jakarta.persistence.*;

import java.util.List;

public class ProyectoDTO {
    private Long id;
    private String titulo;
    private List<String> estudiantes;
    private String nombreDir;
    private String nombreCodir;
    private String anteproyecto;
    private String tipoTrabajoGrado;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public List<String> getEstudiantes() {
        return estudiantes;
    }

    public void setEstudiantes(List<String> estudiantes) {
        this.estudiantes = estudiantes;
    }

    public String getNombreDir() {
        return nombreDir;
    }

    public void setNombreDir(String nombreDir) {
        this.nombreDir = nombreDir;
    }

    public String getNombreCodir() {
        return nombreCodir;
    }

    public void setNombreCodir(String nombreCodir) {
        this.nombreCodir = nombreCodir;
    }

    public String getAnteproyecto() {
        return anteproyecto;
    }

    public void setAnteproyecto(String anteproyecto) {
        this.anteproyecto = anteproyecto;
    }

    public String getTipoTrabajoGrado() {
        return tipoTrabajoGrado;
    }

    public void setTipoTrabajoGrado(String tipoTrabajoGrado) {
        this.tipoTrabajoGrado = tipoTrabajoGrado;
    }
}

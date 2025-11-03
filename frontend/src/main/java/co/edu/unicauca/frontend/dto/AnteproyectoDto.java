package co.edu.unicauca.frontend.dto;

import java.util.ArrayList;
import java.util.List;

public class AnteproyectoDto {

    private Long id;
    private String titulo;
    private String descripcion; // Descripción del anteproyecto
    private String fechaCreacion; // Fecha de creación del anteproyecto
    private List<String> evaluadores = new ArrayList<>(); // Inicializamos vacío

    // Getters and Setters
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public List<String> getEvaluadores() {
        return evaluadores;
    }

    public void setEvaluadores(List<String> evaluadores) {
        this.evaluadores = evaluadores;
    }
}

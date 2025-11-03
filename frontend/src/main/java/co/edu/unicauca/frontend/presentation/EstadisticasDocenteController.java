package co.edu.unicauca.frontend.presentation;

import java.net.URL;
import java.util.ResourceBundle;

import co.edu.unicauca.frontend.entities.SesionFront;
import co.edu.unicauca.frontend.infra.dto.UsuarioDTO;
import co.edu.unicauca.frontend.services.DocenteService;
import co.edu.unicauca.frontend.services.EstudianteService;
import co.edu.unicauca.frontend.services.Observer;
import co.edu.unicauca.frontend.services.ProyectoService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.BarChart;

public class EstadisticasDocenteController implements Initializable, Observer {
    
    @FXML private BarChart<String, Number> BarChartEstadisticas;
    private XYChart.Series<String, Number> seriesTesis;
    private XYChart.Series<String, Number> seriesPractica;
    private ProyectoService proyectoService;
    private EstudianteService estudianteService;
    private DocenteService docenteService;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        seriesTesis = new XYChart.Series();
        seriesTesis.setName("TESIS");
        seriesPractica = new XYChart.Series();
        seriesPractica.setName("PRACTICA PROFESIONAL");
        BarChartEstadisticas.getData().addAll(seriesTesis, seriesPractica);
    }

    private void cargarEstadisticas() {
        seriesTesis.getData().add(new XYChart.Data("TERMINADOS", obtenerCantidad("TRABAJO_DE_INVESTIGACION", "TERMINADO")));
        seriesTesis.getData().add(new XYChart.Data("RECHAZADOS", obtenerCantidad("TRABAJO_DE_INVESTIGACION", "RECHAZADO")));
        seriesTesis.getData().add(new XYChart.Data("EN TRAMITE", obtenerCantidad("TRABAJO_DE_INVESTIGACION", "EN_TRAMITE")));

        seriesPractica.getData().add(new XYChart.Data("TERMINADOS", obtenerCantidad("PRACTICA_PROFESIONAL", "TERMINADO")));
        seriesPractica.getData().add(new XYChart.Data("RECHAZADOS", obtenerCantidad("PRACTICA_PROFESIONAL", "RECHAZADO")));
        seriesPractica.getData().add(new XYChart.Data("EN TRAMITE", obtenerCantidad("PRACTICA_PROFESIONAL", "EN_TRAMITE")));

    }

    private int obtenerCantidad(String tipo, String estado){
        UsuarioDTO docente = SesionFront.getInstancia().getUsuarioActivo();
        return proyectoService.countProyectosByEstadoYTipo(tipo, estado, docente.getCorreo());
    }

    public void setServices(DocenteService docenteService, ProyectoService proyectoService, EstudianteService estudianteService) {
        this.proyectoService =  proyectoService;
        this.docenteService = docenteService;
        this.estudianteService = estudianteService;
        this.proyectoService.addObserver(this);
        cargarEstadisticas();
    }

    @Override
    public void update() {
        cargarEstadisticas();
    }
}

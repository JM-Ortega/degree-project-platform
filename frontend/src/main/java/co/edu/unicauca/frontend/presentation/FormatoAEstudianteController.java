package co.edu.unicauca.frontend.presentation;

import co.edu.unicauca.frontend.infra.dto.ProyectoEstudianteDTO;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class FormatoAEstudianteController //implements Initializable
{
    @FXML
    private Label nombreEstudiante;
    @FXML private TableView<ProyectoEstudianteDTO> tabla;
    @FXML private TableColumn<ProyectoEstudianteDTO, String> colTipo;
    @FXML private TableColumn<ProyectoEstudianteDTO, String> colTitulo;
    @FXML private TableColumn<ProyectoEstudianteDTO, LocalDate> colFechaEmision;
    @FXML private TableColumn<ProyectoEstudianteDTO, String> colEstado;
    @FXML private TableColumn<ProyectoEstudianteDTO, Void> colDescarga;
    @FXML private Label LabelInfo;

//    @Override
//    public void initialize(URL url, ResourceBundle rb) {
//        configurarColumnas();
//        configurarColumnaEstado();
//        //cargarDatos();
//    }
//
//    private void configurarColumnas() {
//        colTipo.setCellValueFactory(cellData ->
//                new SimpleStringProperty(cellData.getValue().getTipo().toString())
//        );
//
//        colTitulo.setCellValueFactory(cellData -> {
//            Proyecto proyecto = proyectosCache.get(cellData.getValue().getId());
//            return new SimpleStringProperty(proyecto != null ? proyecto.getTitulo() : "N/A");
//        });
//
//        colFechaEmision.setCellValueFactory(cellData ->
//                new SimpleStringProperty(cellData.getValue().getArchivo().getFechaSubida())
//        );
//
//        colEstado.setCellValueFactory(cellData ->
//                new SimpleStringProperty(cellData.getValue().getArchivo().getEstado().toString())
//        );
//
//        colVersion.setCellValueFactory(cellData ->
//                new SimpleIntegerProperty(cellData.getValue().getArchivo().getNroVersion()).asObject()
//        );
//
//        configurarColumnaAcciones();
//    }
}

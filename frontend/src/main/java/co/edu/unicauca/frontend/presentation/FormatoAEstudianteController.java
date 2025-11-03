package co.edu.unicauca.frontend.presentation;

import co.edu.unicauca.frontend.entities.SesionFront;
import co.edu.unicauca.frontend.infra.dto.ProyectoEstudianteDTO;
import co.edu.unicauca.frontend.infra.dto.UsuarioDTO;
import co.edu.unicauca.frontend.services.ProyectoEstudianteService;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class FormatoAEstudianteController implements Initializable {
    @FXML private TableView<ProyectoEstudianteDTO> tabla;
    @FXML private TableColumn<ProyectoEstudianteDTO, String> colTitulo;
    @FXML private TableColumn<ProyectoEstudianteDTO, String> colTipo;
    @FXML private TableColumn<ProyectoEstudianteDTO, String> colDirector;
    @FXML private TableColumn<ProyectoEstudianteDTO, LocalDate> colFecha;
    @FXML private TableColumn<ProyectoEstudianteDTO, String> colEstado;
    @FXML private TableColumn<ProyectoEstudianteDTO, Void> colDescarga;


    private final ProyectoEstudianteService proyectoService = new ProyectoEstudianteService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarColumnas();
        configurarColumnaEstado();
        cargarDatos();
    }

    public void configurarColumnas(){
        colTitulo.setCellValueFactory(cell -> cell.getValue().tituloProperty());

        colTipo.setCellValueFactory(cellData -> {
            String tipo = cellData.getValue().getTipoProyecto();
            String tipoP;
            switch (tipo) {
                case "TRABAJO_DE_INVESTIGACION":
                    tipoP = "Trabajo de investigación";
                    break;
                case "PRACTICA_PROFESIONAL":
                    tipoP = "Práctica profesional";
                    break;
                default:
                    tipoP = tipo;
                    break;
            }
            return new ReadOnlyStringWrapper(tipoP);
        });

        colDirector.setCellValueFactory(cell -> cell.getValue().nombreDirectorProperty());
        colFecha.setCellValueFactory(cell -> cell.getValue().fechaEmisionProperty());
        colEstado.setCellValueFactory(cell -> cell.getValue().estadoProyectoProperty());
    }

    private void configurarColumnaEstado() {
        colEstado.setCellFactory(col -> new TableCell<ProyectoEstudianteDTO, String>() {
            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(String estado, boolean empty) {
                super.updateItem(estado, empty);
                if (empty || estado == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                Image img = null;
                String e = estado.trim().toUpperCase();
                switch (e) {
                    case "APROBADO" -> img = loadImage("/co/unicauca/workflow/degree_project/images/aprobado.png");
                    case "OBSERVADO" -> img = loadImage("/co/unicauca/workflow/degree_project/images/observado.png");
                    case "PENDIENTE" -> img = loadImage("/co/unicauca/workflow/degree_project/images/pendiente.png");
                    default -> img = null;
                }

                if (img != null) {
                    imageView.setImage(img);
                    setGraphic(imageView);
                    setText(null);
                } else {
                    setGraphic(null);
                    setText(estado);
                }
            }
        });
    }

    private Image loadImage(String resourcePath) {
        var is = getClass().getResourceAsStream(resourcePath);
        return (is == null) ? null : new Image(is);
    }

    public void cargarDatos() {
        try {
            var proyectos = proyectoService.obtenerProyectosEstudiante();
            tabla.setItems(FXCollections.observableArrayList(proyectos));
        } catch (Exception e) {
            e.printStackTrace();
            alerta(Alert.AlertType.ERROR, "Error", null, "Error al cargar los datos: " + e.getMessage());
        }
    }

    private void alerta(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

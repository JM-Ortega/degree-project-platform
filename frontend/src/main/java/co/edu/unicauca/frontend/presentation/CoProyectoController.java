package co.edu.unicauca.frontend.presentation;

import co.edu.unicauca.frontend.entities.FormatoAResumen;
import co.edu.unicauca.frontend.services.FormatoAClient;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class CoProyectoController implements Initializable {
    @FXML
    private TableView<FormatoAResumen> tabla;

    @FXML
    private TableColumn<FormatoAResumen, String> colNombreProyecto;
    @FXML
    private TableColumn<FormatoAResumen, String> colNombreProfesor;
    @FXML
    private TableColumn<FormatoAResumen, String> colTipoP;
    @FXML
    private TableColumn<FormatoAResumen, LocalDate> colFecha;
    @FXML
    private TableColumn<FormatoAResumen, String> colEstado;
    @FXML
    private TableColumn<FormatoAResumen, Number> colVersion;

    @FXML
    private TableColumn<FormatoAResumen, Void> colDescargar;

    private CoordinadorController parent;
    private final FormatoAClient formatoAClient = new FormatoAClient();

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        // Asignar columnas
        colNombreProyecto.setCellValueFactory(cellData -> cellData.getValue().nombreProyectoProperty());
        colNombreProfesor.setCellValueFactory(cellData -> cellData.getValue().nombreDirectorProperty());
        colTipoP.setCellValueFactory(cellData -> cellData.getValue().tipoProyectoProperty());
        colFecha.setCellValueFactory(cellData -> cellData.getValue().fechaSubidaProperty());
        colEstado.setCellValueFactory(cellData -> cellData.getValue().estadoFormatoAProperty());
        colVersion.setCellValueFactory(cellData -> cellData.getValue().nroVersionProperty());

        configurarColumnaEstado();
        agregarBotonDescargar();
        cargarTabla();
    }

    private void agregarBotonDescargar() {
        colDescargar.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Descargar");

            {
                btn.setOnAction(event -> {
                    FormatoAResumen formato = getTableView().getItems().get(getIndex());
                    descargarArchivo(formato);
                    System.out.println("Descargar formato con id: " + formato.getId());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });
        configurarColumnaDescargar();
    }

    private void cargarTabla() {
        try {
            var formatos = formatoAClient.obtenerFormatosAResumen();
            tabla.setItems(FXCollections.observableArrayList(formatos));
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error al cargar los datos: " + e.getMessage()).show();
        }
    }

    private void descargarArchivo(FormatoAResumen formato) {
        try {
            byte[] archivo = formatoAClient.descargarFormatoA(formato.getId());
            Path destino = Path.of(System.getProperty("user.home"), "Descargas", formato.getNombreProyecto() + ".pdf");
            Files.createDirectories(destino.getParent());
            try (FileOutputStream fos = new FileOutputStream(destino.toFile())) {
                fos.write(archivo);
            }
            new Alert(Alert.AlertType.INFORMATION, "Archivo descargado en:\n" + destino).show();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error al descargar archivo: " + e.getMessage()).show();
        }
    }

    public void setParentController(CoordinadorController parent) {
        this.parent = parent;
    }

    private void alerta(Alert.AlertType type, String title, String header, String content) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(header);
        a.setContentText(content);
        a.showAndWait();
    }

    private void configurarColumnaEstado() {
        colEstado.setCellFactory(column -> new TableCell<FormatoAResumen, String>() {
            private final Button estadoBtn = new Button();

            {
                estadoBtn.setOnAction(e -> {
                    FormatoAResumen formato = getTableView().getItems().get(getIndex());
                    if (formato == null) return;

                    String estadoActual = formato.getEstadoFormatoA();

                    if ("PENDIENTE".equalsIgnoreCase(estadoActual)) {
                        if (parent != null) {
                            parent.loadUI(
                                    "/co/edu/unicauca/frontend/view/Coordinador_Observaciones.fxml",
                                    formato
                            );
                        }
                    } else {
                        alerta(Alert.AlertType.WARNING,
                                "Acción no permitida",
                                null,
                                "Este proyecto ya fue evaluado. No se puede volver a evaluar.");
                    }
                });
            }

            @Override
            protected void updateItem(String estado, boolean empty) {
                super.updateItem(estado, empty);

                if (empty || estado == null) {
                    setGraphic(null);
                    return;
                }

                estadoBtn.setText(estado.toUpperCase());
                estadoBtn.getStyleClass().clear(); // Limpia clases previas

                Image icon = null;
                if ("PENDIENTE".equalsIgnoreCase(estado)) {
                    estadoBtn.getStyleClass().add("estado-rojo");
                    icon = new Image(getClass().getResourceAsStream(
                            "/co/edu/unicauca/frontend/images/ojo_abierto.png"));
                } else if ("OBSERVADO".equalsIgnoreCase(estado)) {
                    estadoBtn.getStyleClass().add("estado-verde");
                    icon = new Image(getClass().getResourceAsStream(
                            "/co/edu/unicauca/frontend/images/ojo_cerrado.png"));
                } else {
                    estadoBtn.getStyleClass().add("estado-gris");
                }

                if (icon != null) {
                    ImageView iv = new ImageView(icon);
                    iv.setFitWidth(24);
                    iv.setFitHeight(22);
                    estadoBtn.setGraphic(iv);
                } else {
                    estadoBtn.setGraphic(null);
                }

                setGraphic(estadoBtn);
            }
        });
    }

    private void configurarColumnaDescargar() {
        colDescargar.setCellFactory(col -> new TableCell<>() {
            private final Button btnDescargar = new Button();
            private final ImageView imgView;

            {
                btnDescargar.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

                imgView = new ImageView(new Image(
                        getClass().getResourceAsStream("/co/edu/unicauca/frontend/images/descargar.png")
                ));
                imgView.setFitWidth(20);
                imgView.setFitHeight(20);
                btnDescargar.setGraphic(imgView);

                btnDescargar.setOnAction(event -> {
                    FormatoAResumen formato = getTableView().getItems().get(getIndex());
                    if (formato != null) {
                        try {
                            FormatoAClient client = new FormatoAClient();
                            byte[] data = client.descargarFormatoA(formato.getId());

                            FileChooser fileChooser = new FileChooser();
                            fileChooser.setTitle("Guardar archivo PDF");
                            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));
                            fileChooser.setInitialFileName(formato.getNombreProyecto() + ".pdf");

                            File file = fileChooser.showSaveDialog(null);
                            if (file != null) {
                                Files.write(file.toPath(), data);
                            }

                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Descarga exitosa");
                            alert.setHeaderText(null);
                            alert.setContentText("El archivo se descargó en tu escritorio ✅");
                            alert.showAndWait();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error al descargar");
                            alert.setHeaderText(null);
                            alert.setContentText("No se pudo descargar el archivo 😢");
                            alert.showAndWait();
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnDescargar);
                }
            }
        });
    }
}

package co.edu.unicauca.frontend.presentation;

import co.edu.unicauca.frontend.entities.Archivo;
import co.edu.unicauca.frontend.entities.RowVM;
import co.edu.unicauca.frontend.services.FormatoAClient;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.ResourceBundle;

public class Co_Proyecto_Controller implements Initializable {
    @FXML
    private TableView<RowVM> tabla;
    @FXML
    private TableColumn<RowVM, String> colNombreProyecto;
    @FXML
    private TableColumn<RowVM, String> colNombreProfesor;
    @FXML
    private TableColumn<RowVM, String> colTipoA;
    @FXML
    private TableColumn<RowVM, String> colTipoP;
    @FXML
    private TableColumn<RowVM, String> colFecha;
    @FXML
    private TableColumn<RowVM, RowVM> colEstado;
    @FXML
    private TableColumn<RowVM, RowVM> colDescargar;

    private CoordinadorController parent;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarTabla();
        cargarTabla();
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

    private void configurarTabla() {
        colNombreProyecto.setCellValueFactory(d -> d.getValue().nombreProyectoProperty());
        colNombreProfesor.setCellValueFactory(d -> d.getValue().nombreDocenteProperty());
        colTipoA.setCellValueFactory(d -> d.getValue().tipoAProperty());
        colTipoP.setCellValueFactory(d -> d.getValue().tipoPProperty());
        colFecha.setCellValueFactory(d -> d.getValue().fechaProperty());

        colFecha.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String soloFecha = item.split(" ")[0];
                    setText(soloFecha);
                }
            }
        });

        colEstado.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        configurarColumnaEstado();
        configurarColumnaDescargar();
        tabla.getColumns().forEach(col -> col.setReorderable(false));
        colNombreProyecto.setSortable(false);
        colNombreProfesor.setSortable(false);
        colTipoA.setSortable(false);
        colTipoP.setSortable(false);
        colFecha.setSortable(false);
        colEstado.setSortable(false);
        colDescargar.setSortable(false);
    }

    private void cargarTabla() {
        FormatoAClient formatoAClient = new FormatoAClient();

        try {
            List<Archivo> archivos = formatoAClient.listarTodosArchivos();

            ObservableList<RowVM> rows = FXCollections.observableArrayList();

            for (Archivo a : archivos) {
                rows.add(new RowVM(
                        a.getId(),
                        a.getProyectoId(),
                        a.getTituloProyecto(),
                        a.getDirector(),
                        a.getTipoArchivo(),
                        a.getTipoTrabajoGrado(),
                        a.getFechaSubida(),
                        a.getEstado().name(),
                        a.getCorreoDirector(),
                        a.getCorreoEstudiante()
                ));
            }

            // Ordenar pendientes primero
            rows.sort((r1, r2) -> {
                if (r1.estadoProperty().get().equalsIgnoreCase("Pendiente") &&
                        !r2.estadoProperty().get().equalsIgnoreCase("Pendiente")) {
                    return -1;
                } else if (!r1.estadoProperty().get().equalsIgnoreCase("Pendiente") &&
                        r2.estadoProperty().get().equalsIgnoreCase("Pendiente")) {
                    return 1;
                }
                return 0;
            });

            tabla.setItems(rows);
            tabla.refresh();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void configurarColumnaEstado() {
        colEstado.setCellFactory(column -> new TableCell<RowVM, RowVM>() {
            private final Button estadoBtn = new Button();

            {
                estadoBtn.setOnAction(e -> {
                    RowVM row = getItem();
                    if (row == null) return;

                    String estadoActual = row.estadoProperty().get();

                    if ("PENDIENTE".equalsIgnoreCase(estadoActual)) {
                        /*
                        if (parent != null) {
                            parent.loadUI(
                                    "/co/unicauca/workflow/degree_project/view/Coordinador_Observaciones",
                                    row //pasamos el RowVM
                            );
                        }
                        */
                    } else {
                        alerta(Alert.AlertType.WARNING,
                                "Acción no permitida",
                                null,
                                "Este proyecto ya fue evaluado. No se puede volver a evaluar.");
                    }
                });
            }

            @Override
            protected void updateItem(RowVM row, boolean empty) {
                super.updateItem(row, empty);

                if (empty || row == null) {
                    setGraphic(null);
                } else {
                    String estado = row.estadoProperty().get();
                    estadoBtn.setText(estado);

                    Image icon = null;
                    if ("PENDIENTE".equalsIgnoreCase(estado)) {
                        estadoBtn.getStyleClass().add("estado-rojo");
                        icon = new Image(getClass().getResourceAsStream(
                                "/co/unicauca/workflow/degree_project/images/ojo_abierto.png"));
                    } else if ("OBSERVADO".equalsIgnoreCase(estado)) {
                        estadoBtn.getStyleClass().add("estado-verde");
                        icon = new Image(getClass().getResourceAsStream(
                                "/co/unicauca/workflow/degree_project/images/ojo_cerrado.png"));
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
            }
        });
    }

    private void configurarColumnaDescargar() {
        colDescargar.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        colDescargar.setCellFactory(col -> new TableCell<>() {
            private final Button btnDescargar = new Button();
            private final ImageView imgView;

            {
                btnDescargar.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

                imgView = new ImageView(
                        new Image(getClass().getResourceAsStream(
                                "/co/unicauca/workflow/degree_project/images/descargar.png"))
                );
                imgView.setFitWidth(20);
                imgView.setFitHeight(20);
                btnDescargar.setGraphic(imgView);

//                btnDescargar.setOnAction(event -> {
//                    RowVM r = getItem();
//                    if (r != null) descargarObservaciones(r);
//                });
            }

            @Override
            protected void updateItem(RowVM row, boolean empty) {
                super.updateItem(row, empty);
                setGraphic(empty || row == null ? null : btnDescargar);
            }
        });
    }

//    private void descargarObservaciones(RowVM row) {
//        try {
//            proyectoService.enforceAutoCancelIfNeeded(row.archivoId());
//            var arch = proyectoService.obtenerFormatoA(row.archivoId());
//            if (arch == null) {
//                alerta(Alert.AlertType.WARNING,
//                        "Formato A no encontrado",
//                        null,
//                        "No hay Formato A disponible con observaciones para este proyecto.");
//                return;
//            }
//
//            FileChooser fc = new FileChooser();
//            fc.setTitle("Guardar Formato A con observaciones");
//            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
//            fc.setInitialFileName(arch.getNombreArchivo());
//
//            File dest = fc.showSaveDialog(tabla.getScene().getWindow());
//            if (dest == null) return;
//
//            Files.write(dest.toPath(), arch.getBlob());
//
//            alerta(Alert.AlertType.INFORMATION,
//                    "Archivo descargado",
//                    null,
//                    "El archivo se guardó exitosamente en:\n" + dest.getAbsolutePath());
//
//            cargarTabla();
//        } catch (Exception ex) {
//            alerta(Alert.AlertType.ERROR,
//                    "Error al descargar",
//                    null,
//                    ex.getMessage() != null ? ex.getMessage() : "Error desconocido al guardar el archivo.");
//        }
//    }

}

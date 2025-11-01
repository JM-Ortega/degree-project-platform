package co.edu.unicauca.frontend.presentation;

import co.edu.unicauca.frontend.entities.FormatoAResumen;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

public class CoObservacionesController {
    @FXML
    private Label volver;

    @FXML
    private ComboBox<String> cbxValoracion;

    @FXML
    private Label lblFechaEntrega;

    @FXML
    private Label lblNombreProyecto;

    @FXML
    private Label lblVersionArchivo;

    @FXML
    private Label lblTipoProyecto;

    @FXML
    private Button btnArchivo;

    @FXML
    private Label lblArchivo;

    @FXML
    private Button btnEnviar;

    private FormatoAResumen formatoSeleccionado;

    public void setFormatoSeleccionado(FormatoAResumen formato) {
        this.formatoSeleccionado = formato;

        // Si quieres mostrar la información en la vista al cargar:
        if (formato != null) {
            lblNombreProyecto.setText(formato.nombreProyectoProperty().get());
            lblTipoProyecto.setText(formato.getTipoProyecto());
            lblFechaEntrega.setText(formato.getFechaSubida().toString());
            lblVersionArchivo.setText(String.valueOf(formato.getNroVersion()));
        }
    }

//    private CoordinadorController parent;
//    private FormatoAResumen rowActual;
//
//    public void setParentController(CoordinadorController parent) {
//        this.parent = parent;
//    }
//
//    public void setFormatoAResumen(FormatoAResumen row) {
//        this.rowActual = row;
//        lblNombreProyecto.setText(row.nombreProyectoProperty().get());
//        lblTipoProyecto.setText(row.getTipoProyecto());
//        lblFechaEntrega.setText(row.getFechaSubida().toString());
//        lblVersionArchivo.setText(String.valueOf(row.getNroVersion()));
//        lblArchivo.setText("");
//    }

}

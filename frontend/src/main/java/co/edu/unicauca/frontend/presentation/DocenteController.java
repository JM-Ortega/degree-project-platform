package co.edu.unicauca.frontend.presentation;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DocenteController implements Initializable {
    @FXML private Button btnPrincipal;
    @FXML private Button btnFormatoA;
    @FXML private Button btnSalir;
    @FXML private Label nombreDocente;
    @FXML private BorderPane bp;
    @FXML private AnchorPane ap;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        activarBoton(btnPrincipal, btnFormatoA, btnSalir);
        cargarDatos();
    }

    @FXML
    void switchToLogin(ActionEvent event) {

    }

    @FXML
    private void showInfoPrincipal(ActionEvent event) {
        activarBoton(btnPrincipal, btnFormatoA, btnSalir);
        bp.setCenter(ap);
    }

    @FXML
    private void showInfoFormatoA(ActionEvent event) {
        activarBoton(btnFormatoA, btnPrincipal, btnSalir);

    }

    public void cargarDatos() {

    }

    private void activarBoton(Button botonActivo, Button... otros) {
        botonActivo.getStyleClass().remove("btn-default");
        if (!botonActivo.getStyleClass().contains("btn-pressed")) {
            botonActivo.getStyleClass().add("btn-pressed");
        }

        for (Button b : otros) {
            b.getStyleClass().remove("btn-pressed");
            if (!b.getStyleClass().contains("btn-default")) {
                b.getStyleClass().add("btn-default");
            }
        }
    }
}

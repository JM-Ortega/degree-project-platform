package co.edu.unicauca.frontend.presentation;

import co.edu.unicauca.frontend.entities.SesionFront;
import co.edu.unicauca.frontend.infra.dto.UsuarioDTO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class EstudianteController implements Initializable {
    @FXML private Button btnPrincipal;
    @FXML private Button btnFormatoA;
    @FXML private Button btnSalir;
    @FXML private Label nombreEstudiante;
    @FXML private BorderPane bp;
    @FXML private AnchorPane ap;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        activarBoton(btnPrincipal, btnFormatoA, btnSalir);
        cargarDatos();
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

    void cargarDatos() {
        UsuarioDTO docente = SesionFront.getInstancia().getUsuarioActivo();
        if (docente != null) {
            nombreEstudiante.setText(docente.getNombre());
        } else {
            System.err.println("No hay sesión activa");
        }
    }
}

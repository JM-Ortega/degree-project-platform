package co.edu.unicauca.frontend.presentation;

import co.edu.unicauca.frontend.FrontendApp;
import co.edu.unicauca.frontend.entities.CoordinadorResumen;
import co.edu.unicauca.frontend.entities.FormatoAResumen;
import co.edu.unicauca.frontend.services.CoordinadorClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CoordinadorController implements Initializable{
    @FXML
    private AnchorPane contentArea;
    
    @FXML
    private Button btnProyectos;

    @FXML
    private Button btnSalir;
    
    @FXML
    private Label lblNombreCompleto;

    @FXML
    private Label lblPrograma;

    private final CoordinadorClient client = new CoordinadorClient();
    private final ObjectMapper mapper = new ObjectMapper();

    private Button selectedButton = null; // botón actualmente seleccionado
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnSalir.setOnAction(this::onSalir);

        btnProyectos.setOnMouseEntered(e -> btnProyectos.getStyleClass().add("hoverable"));
        btnProyectos.setOnMouseExited(e -> btnProyectos.getStyleClass().remove("hoverable"));
        
        btnSalir.setOnMouseEntered(e -> btnSalir.getStyleClass().add("hoverable"));
        btnSalir.setOnMouseExited(e -> btnSalir.getStyleClass().remove("hoverable"));

        // Configurar eventos para cada botón
        btnProyectos.setOnAction(e -> {
            loadUI("Coordinador_Proyectos");
            selectButton(btnProyectos);
        });

        cargarDatos();
    }

    void cargarDatos() {
        try {
            String json = client.getCoordinadorInfo("laura.gomez@unicauca.edu.co"); // coordinador con correo seteado

            // Si la respuesta contiene un "timestamp" o "error", probablemente es un error del backend
            if (json.contains("\"timestamp\"") || json.contains("\"error\"")) {
                System.err.println("Error del backend: " + json);
                return; // o muestra una alerta al usuario
            }

            CoordinadorResumen info = mapper.readValue(json, CoordinadorResumen.class);

            lblNombreCompleto.setText(info.getNombreCompleto());
            lblPrograma.setText(info.getPrograma());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void selectButton(Button button) {
        // Si ya hay un botón seleccionado, quitarle la clase CSS
        if (selectedButton != null) {
            selectedButton.getStyleClass().remove("selected");
        }
        // Agregar clase al nuevo botón seleccionado
        if (!button.getStyleClass().contains("selected")) {
            button.getStyleClass().add("selected");
        }
        selectedButton = button;
    }

    public void loadUI(String fxml) {
        try {
            FXMLLoader loader = FrontendApp.newLoader("/co/edu/unicauca/frontend/view/" + fxml + ".fxml");
            Parent root = loader.load();

            Object controller = loader.getController();

            if (controller instanceof CoProyectoController cpc) {
                cpc.setParentController(this);
            }

            contentArea.getChildren().setAll(root);
            AnchorPane.setTopAnchor(root, 0.0);
            AnchorPane.setRightAnchor(root, 0.0);
            AnchorPane.setBottomAnchor(root, 0.0);
            AnchorPane.setLeftAnchor(root, 0.0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadUI(String fxmlPath, FormatoAResumen formatoSeleccionado) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Si la vista cargada es Coordinador_Observaciones, pásale el formato
            if (loader.getController() instanceof CoObservacionesController controller) {
                controller.setFormatoSeleccionado(formatoSeleccionado);
            }

            // Cambiar el contenido principal
            contentArea.getChildren().setAll(root);

        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error al cargar vista: " + e.getMessage()).show();
        }
    }

    @FXML
    public void onSalir(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = FrontendApp.newLoader("/co/edu/unicauca/frontend/view/signin.fxml");
            Parent root = loader.load();

            // Obtener la ventana actual (Stage)
            Stage stage = (Stage) btnSalir.getScene().getWindow();

            // Cambiar la escena por la del inicio de sesión
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

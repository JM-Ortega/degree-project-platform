package co.edu.unicauca.frontend;

import co.edu.unicauca.frontend.presentation.navigation.ViewNavigator;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Punto de entrada principal del frontend JavaFX.
 */
public class FrontendApp extends Application {

    @Override
    public void start(Stage stage) {
        // 1. inicializar servicios compartidos
        FrontendServices.init();

        // 2. inicializar el navegador
        ViewNavigator.init(stage);

        // 3. mostrar la primera vista
        ViewNavigator.goTo(
                "/co/edu/unicauca/frontend/view/SignIn.fxml",
                "Inicio de sesión"
        );

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

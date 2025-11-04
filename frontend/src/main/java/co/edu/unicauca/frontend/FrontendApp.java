package co.edu.unicauca.frontend;

import co.edu.unicauca.frontend.presentation.navigation.ViewNavigator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * Punto de entrada principal del frontend JavaFX.
 */
public class FrontendApp extends Application {

    @Override
    public void start(Stage stage) {
        // 1) Inicializar servicios compartidos (definido en main)
        FrontendServices.init();

        // 2) Inicializar el navegador
        ViewNavigator.init(stage);

        // 3) Mostrar la primera vista
        ViewNavigator.goTo(
                "/co/edu/unicauca/frontend/view/SignIn.fxml",
                "Inicio de sesión"
        );

        stage.show();
    }

    public static FXMLLoader newLoader(String resourcePath) {
        return new FXMLLoader(Objects.requireNonNull(
                FrontendApp.class.getResource(resourcePath)
        ));
    }

    public static void main(String[] args) {
        launch(args);
    }
}

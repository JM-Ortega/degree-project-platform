package co.edu.unicauca.frontend;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.Objects;

public class FrontendApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(
                FrontendApp.class.getResource("/co/edu/unicauca/frontend/view/Coordinador.fxml")
        );


        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle("Inicio de Sesión");


        stage.setScene(scene);
        stage.show();
    }

    public static FXMLLoader newLoader(String resourcePath) {
        return new FXMLLoader(Objects.requireNonNull(
                FrontendApp.class.getResource(resourcePath)
        ));
    }
}
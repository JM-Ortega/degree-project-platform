package co.edu.unicauca.frontend;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class FrontendApp extends Application {
    private static Stage primaryStage;
    private static Scene scene;

    @Override
    public void start(Stage stage) throws Exception {
        // Cargar el FXML desde /resources/co/edu/unicauca/frontend/view/
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/co/edu/unicauca/frontend/view/Coordinador.fxml")
        );

        Scene scene = new Scene(loader.load());

        stage.setTitle("Degree Project Platform");
        stage.setScene(scene);
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

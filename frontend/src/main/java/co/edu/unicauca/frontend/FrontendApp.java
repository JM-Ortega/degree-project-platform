package co.edu.unicauca.frontend;

import com.sun.tools.javac.Main;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;

public class FrontendApp extends Application {
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

    public static FXMLLoader newLoader(String path) {
        URL url = Objects.requireNonNull(Main.class.getResource(path),
                () -> "FXML no encontrado: " + path);
        return new FXMLLoader(url);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

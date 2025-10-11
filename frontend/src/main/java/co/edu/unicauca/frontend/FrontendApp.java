package co.edu.unicauca.frontend;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FrontendApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // Cargar el FXML desde /resources/co/edu/unicauca/frontend/view/
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/co/edu/unicauca/frontend/view/signin.fxml")
        );

        Scene scene = new Scene(loader.load());

        stage.setTitle("Degree Project Platform");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

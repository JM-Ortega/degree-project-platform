package co.edu.unicauca.frontend;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FrontendApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(
                FrontendApp.class.getResource("/co/edu/unicauca/frontend/view/LoginView.fxml")
        );


        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle("Inicio de Sesión");


        stage.setScene(scene);
        stage.show();
    }
}

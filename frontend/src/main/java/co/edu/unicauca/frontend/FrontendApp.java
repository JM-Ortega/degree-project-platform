package co.edu.unicauca.frontend;

import co.edu.unicauca.frontend.entities.SesionFront;
import co.edu.unicauca.frontend.infra.dto.UsuarioDTO;
import co.edu.unicauca.frontend.presentation.DocenteController;
import co.edu.unicauca.frontend.services.DocenteService;
import co.edu.unicauca.frontend.services.EstudianteService;
import co.edu.unicauca.frontend.services.ProyectoService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class FrontendApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        UsuarioDTO docentePrueba = new UsuarioDTO();
        docentePrueba.setNombre("Maria Fernanda Fernández");
        docentePrueba.setCorreo("mariafernandez@unicauca.edu.co");
        SesionFront.getInstancia().setUsuarioActivo(docentePrueba);

        DocenteService docenteService = new DocenteService();
        ProyectoService proyectoService = new ProyectoService();
        EstudianteService  estudianteService = new EstudianteService();

        FXMLLoader fxmlLoader = new FXMLLoader(
                FrontendApp.class.getResource("/co/edu/unicauca/frontend/view/Docente.fxml")
        );
        Parent root = fxmlLoader.load();

        DocenteController docenteController = fxmlLoader.getController();
        docenteController.setServices(docenteService, proyectoService, estudianteService);

        Scene scene = new Scene(root);
        stage.setTitle("Docente - Prueba");
        stage.setScene(scene);
        stage.show();
    }

    public static FXMLLoader newLoader(String resourcePath) {
        return new FXMLLoader(Objects.requireNonNull(
                FrontendApp.class.getResource(resourcePath)
        ));
    }
}

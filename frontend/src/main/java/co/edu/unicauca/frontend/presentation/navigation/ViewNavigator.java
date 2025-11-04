package co.edu.unicauca.frontend.presentation.navigation;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

/**
 * Componente responsable de gestionar la navegación entre vistas (FXML)
 * dentro de la aplicación JavaFX.
 *
 * <p>
 * Mantiene una referencia al {@link Stage} principal y permite reemplazar
 * la raíz de la escena actual por el contenido de otro archivo FXML,
 * actualizando opcionalmente el título de la ventana.
 * </p>
 *
 * <p>
 * Debe inicializarse una única vez al inicio de la aplicación mediante
 * {@link #init(Stage)}. Posteriormente, cualquier controlador puede invocar
 * {@link #goTo(String, String)} para cambiar de vista.
 * </p>
 */
public final class ViewNavigator {

    /**
     * Ventana principal de la aplicación.
     */
    private static Stage primaryStage;

    /**
     * Constructor privado para impedir la instanciación.
     */
    private ViewNavigator() {
        // Clase utilitaria; no se debe instanciar.
    }

    /**
     * Inicializa el navegador con el {@link Stage} principal.
     *
     * @param stage instancia del {@link Stage} que se utilizará para mostrar las vistas.
     */
    public static void init(Stage stage) {
        primaryStage = stage;
    }

    /**
     * Carga el archivo FXML indicado y lo establece como contenido raíz
     * de la escena del {@link Stage} principal.
     *
     * <p>
     * Si el {@link Stage} aún no tiene una escena asociada, se crea una nueva.
     * Si ya existe una escena, únicamente se reemplaza su raíz y se
     * actualizan las hojas de estilo para que la escena use exclusivamente
     * las que declara el FXML actual.
     * </p>
     *
     * @param fxmlPath ruta del archivo FXML dentro del classpath.
     *                 Debe ser una ruta absoluta desde {@code /}.
     *                 Ejemplo: {@code /co/edu/unicauca/frontend/view/SignIn.fxml}
     * @param title    título que se mostrará en la ventana (puede ser {@code null} o vacío).
     * @throws IllegalStateException si el navegador no ha sido inicializado mediante {@link #init(Stage)}.
     * @throws RuntimeException      si ocurre un error al cargar el archivo FXML.
     */
    public static void goTo(String fxmlPath, String title) {
        if (primaryStage == null) {
            throw new IllegalStateException(
                    "ViewNavigator no ha sido inicializado. Debe llamarse ViewNavigator.init(stage) antes de usarlo."
            );
        }

        try {
            URL url = Objects.requireNonNull(
                    ViewNavigator.class.getResource(fxmlPath),
                    () -> "No se encontró el FXML: " + fxmlPath
            );

            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            // Normaliza las hojas de estilo que vengan declaradas en el FXML
            // (por ejemplo stylesheets="@../styles/signup.css")
            normalizeStylesheets(root);

            Scene scene = primaryStage.getScene();
            if (scene == null) {
                // Primera vista: se crea la escena con las hojas de estilo del FXML
                scene = new Scene(root);
                scene.getStylesheets().clear();
                scene.getStylesheets().addAll(root.getStylesheets());
                primaryStage.setScene(scene);
            } else {
                // Vista posterior: se reemplaza el root y se reemplazan las hojas de estilo
                scene.setRoot(root);
                scene.getStylesheets().clear();
                scene.getStylesheets().addAll(root.getStylesheets());
            }

            if (title != null && !title.isBlank()) {
                primaryStage.setTitle(title);
            }

            primaryStage.sizeToScene();
            primaryStage.centerOnScreen();

        } catch (IOException e) {
            throw new RuntimeException("Error al cargar la vista: " + fxmlPath, e);
        }
    }

    /**
     * Normaliza las hojas de estilo declaradas en el nodo raíz, convirtiendo
     * rutas relativas en rutas absolutas dentro del classpath.
     *
     * <p>
     * Esto es útil cuando en el FXML se declara una ruta relativa como
     * {@code stylesheets="@../styles/signin.css"} y luego la vista se usa
     * dentro de una escena que puede no tener el mismo classloader base.
     * </p>
     *
     * @param root nodo raíz cargado desde el FXML.
     */
    private static void normalizeStylesheets(Parent root) {
        if (root.getStylesheets().isEmpty()) {
            return;
        }

        for (int i = 0; i < root.getStylesheets().size(); i++) {
            String stylesheet = root.getStylesheets().get(i);
            // Si ya es una URL absoluta, no se toca
            if (stylesheet.startsWith("file:") || stylesheet.startsWith("jar:") || stylesheet.startsWith("http:") || stylesheet.startsWith("https:")) {
                continue;
            }
            // Se intenta resolver relativa al paquete de ViewNavigator
            URL cssUrl = ViewNavigator.class.getResource(stylesheet);
            if (cssUrl != null) {
                root.getStylesheets().set(i, cssUrl.toExternalForm());
            }
        }
    }
}

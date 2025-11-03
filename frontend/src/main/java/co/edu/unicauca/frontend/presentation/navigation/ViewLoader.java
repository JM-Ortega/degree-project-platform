package co.edu.unicauca.frontend.presentation.navigation;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

/**
 * Utilidad responsable de cargar vistas FXML dentro de un contenedor existente,
 * como el área central de un {@link javafx.scene.layout.BorderPane}.
 *
 * <p>
 * Se utiliza cuando la aplicación mantiene una vista principal fija
 * y requiere reemplazar secciones internas (submódulos) sin cambiar el
 * {@link javafx.stage.Stage} ni crear una nueva escena.
 * </p>
 *
 * <p>
 * Este cargador también asegura que los estilos definidos en el propio
 * archivo FXML (mediante la propiedad {@code stylesheets}) se apliquen
 * correctamente a la vista cargada.
 * </p>
 */
public final class ViewLoader {

    private ViewLoader() {
        // Clase utilitaria; no se debe instanciar.
    }

    /**
     * Carga una vista FXML y la devuelve como nodo JavaFX listo para insertar.
     *
     * <p>
     * Si el FXML incluye una hoja de estilos mediante {@code stylesheets="@..."},
     * este método garantiza que dicha hoja sea aplicada correctamente,
     * resolviendo la ruta a su forma absoluta.
     * </p>
     *
     * @param fxmlPath ruta del archivo FXML dentro del classpath (debe iniciar con "/").
     * @return nodo raíz cargado desde el archivo FXML.
     * @throws RuntimeException si ocurre un error al cargar el archivo o si no existe.
     */
    public static Parent load(String fxmlPath) {
        try {
            URL url = Objects.requireNonNull(
                    ViewLoader.class.getResource(fxmlPath),
                    () -> "No se encontró el archivo FXML: " + fxmlPath
            );

            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            // Aplica los estilos definidos en el propio FXML, resolviendo rutas relativas.
            if (!root.getStylesheets().isEmpty()) {
                for (int i = 0; i < root.getStylesheets().size(); i++) {
                    String stylesheet = root.getStylesheets().get(i);
                    if (!stylesheet.startsWith("file:") && !stylesheet.startsWith("jar:")) {
                        URL cssUrl = ViewLoader.class.getResource(stylesheet);
                        if (cssUrl != null) {
                            root.getStylesheets().set(i, cssUrl.toExternalForm());
                        }
                    }
                }
            }

            return root;

        } catch (IOException e) {
            throw new RuntimeException("Error al cargar subvista: " + fxmlPath, e);
        }
    }

    /**
     * Carga una vista FXML e inserta su contenido dentro de un contenedor existente.
     *
     * @param container contenedor JavaFX donde se mostrará la vista.
     * @param fxmlPath  ruta del archivo FXML dentro del classpath (debe iniciar con "/").
     */
    public static void loadInto(Pane container, String fxmlPath) {
        Parent view = load(fxmlPath);
        container.getChildren().setAll(view);
    }
    public static <T> T loadIntoWithController(Pane container, String fxmlPath) {
        try {
            URL url = Objects.requireNonNull(
                    ViewLoader.class.getResource(fxmlPath),
                    () -> "No se encontró el archivo FXML: " + fxmlPath
            );
            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            // Normaliza estilos relativos -> absolutos (igual que tu load())
            if (!root.getStylesheets().isEmpty()) {
                for (int i = 0; i < root.getStylesheets().size(); i++) {
                    String s = root.getStylesheets().get(i);
                    if (!s.startsWith("file:") && !s.startsWith("jar:") &&
                            !s.startsWith("http:") && !s.startsWith("https:")) {
                        URL cssUrl = ViewLoader.class.getResource(s);
                        if (cssUrl != null) root.getStylesheets().set(i, cssUrl.toExternalForm());
                    }
                }
            }

            container.getChildren().setAll(root);
            return (T) loader.getController();
        } catch (IOException e) {
            throw new RuntimeException("Error al cargar subvista (con controller): " + fxmlPath, e);
        }
    }

}

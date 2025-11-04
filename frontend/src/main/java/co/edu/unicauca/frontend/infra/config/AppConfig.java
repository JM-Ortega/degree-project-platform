package co.edu.unicauca.frontend.infra.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Clase utilitaria encargada de cargar y exponer las propiedades
 * de configuración de la aplicación frontend.
 *
 * <p>
 * Esta clase lee el archivo {@code application.properties} desde el
 * classpath al momento de la carga de la clase y pone sus valores a disposición
 * mediante el método estático {@link #get(String)}.
 * </p>
 *
 * <p>
 * El archivo puede ubicarse en:
 * <ul>
 *   <li><b>/src/main/resources/application.properties</b> (recomendado)</li>
 *   <li><b>/src/main/resources/co/edu/unicauca/frontend/application.properties</b> (alternativo)</li>
 * </ul>
 * </p>
 *
 * <p>
 * Ejemplo de uso:
 * <pre>
 * String baseUrl = AppConfig.get("api.base-url");
 * </pre>
 * </p>
 *
 * <p>
 * En caso de que el archivo no se encuentre o exista un error de lectura,
 * se imprimirá un mensaje de advertencia en la salida de error estándar.
 * </p>
 */
public final class AppConfig {

    /** Propiedades cargadas desde el archivo de configuración. */
    private static final Properties props = new Properties();

    // Bloque estático: se ejecuta una vez al cargar la clase.
    static {
        try (InputStream in = loadConfig()) {
            if (in != null) {
                props.load(in);
            } else {
                System.err.println("No se encontró application.properties en el classpath.");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al cargar la configuración del frontend.", e);
        }
    }

    /**
     * Intenta cargar el archivo de configuración desde las rutas posibles.
     *
     * @return flujo de entrada del archivo de configuración, o {@code null} si no se encuentra.
     */
    private static InputStream loadConfig() {
        // 1. Buscar en la raíz del classpath (recomendado)
        InputStream in = AppConfig.class.getResourceAsStream("/application.properties");

        // 2. Si no se encuentra, intentar en el paquete co/edu/unicauca/frontend/
        if (in == null) {
            in = AppConfig.class.getResourceAsStream("/co/edu/unicauca/frontend/application.properties");
        }

        return in;
    }

    /**
     * Obtiene el valor asociado a una clave del archivo de configuración.
     *
     * @param key nombre de la propiedad que se desea consultar.
     * @return valor de la propiedad, o {@code null} si no existe.
     */
    public static String get(String key) {
        return props.getProperty(key);
    }

    // Evita instanciación.
    private AppConfig() { }
}

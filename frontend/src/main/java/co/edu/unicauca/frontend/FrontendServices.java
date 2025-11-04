package co.edu.unicauca.frontend;

import co.edu.unicauca.frontend.infra.config.AppConfig;
import co.edu.unicauca.frontend.infra.http.HttpAuthApi;
import co.edu.unicauca.frontend.services.auth.AuthApi;
import co.edu.unicauca.frontend.services.auth.AuthServiceFront;

/**
 * Punto de composición principal de los servicios del frontend.
 *
 * <p>
 * Esta clase actúa como un "composition root" del módulo de presentación,
 * encargada de inicializar y proveer las dependencias necesarias para
 * los controladores y servicios de la interfaz JavaFX.
 * </p>
 *
 * <p>
 * Su objetivo es centralizar la creación de instancias de servicios y
 * mantener la aplicación desacoplada de los detalles de configuración,
 * los cuales se cargan desde {@code application.properties} mediante
 * la clase {@link AppConfig}.
 * </p>
 *
 * <p>
 * Debe ser inicializada al inicio de la aplicación mediante
 * {@link #init()}, preferiblemente desde el método
 * {@link co.edu.unicauca.frontend.FrontendApp#start}.
 * </p>
 *
 * <p>
 * Ejemplo de uso:
 * <pre>{@code
 * FrontendServices.init();
 * AuthServiceFront auth = FrontendServices.authService();
 * }</pre>
 * </p>
 */
public final class FrontendServices {

    /** Servicio de autenticación compartido (instancia singleton). */
    private static AuthServiceFront authService;

    /** Constructor privado para evitar instanciación externa. */
    private FrontendServices() { }

    /**
     * Inicializa las dependencias del frontend.
     * <p>
     * Carga la configuración desde el archivo {@code application.properties},
     * crea los clientes HTTP correspondientes y construye las instancias
     * de servicios de aplicación necesarios.
     * </p>
     * <p>
     * Este método debe ejecutarse una sola vez al iniciar la aplicación.
     * </p>
     */
    public static void init() {
        // Leer la URL base del backend (Gateway)
        String baseUrl = AppConfig.get("api.base-url");
        if (baseUrl == null || baseUrl.isBlank()) {
            System.err.println("[Advertencia] No se encontró 'api.base-url' en application.properties. Usando valor por defecto.");
            baseUrl = "http://localhost:8080/api";
        }

        // Leer los endpoints específicos del microservicio de autenticación
        String registerEndpoint = AppConfig.get("api.endpoint.register");
        String loginEndpoint = AppConfig.get("api.endpoint.login");

        // Crear la implementación concreta del cliente HTTP
        AuthApi authApi = new HttpAuthApi(baseUrl, registerEndpoint, loginEndpoint);

        // Instanciar el servicio de aplicación del frontend
        authService = new AuthServiceFront(authApi);

        System.out.println("[INFO] Servicios del frontend inicializados correctamente con base URL: " + baseUrl);
    }

    /**
     * Obtiene la instancia del servicio de autenticación del frontend.
     *
     * @return la instancia única de {@link AuthServiceFront}.
     * @throws IllegalStateException si el método {@link #init()} no ha sido invocado previamente.
     */
    public static AuthServiceFront authService() {
        if (authService == null) {
            throw new IllegalStateException(
                    "FrontendServices no ha sido inicializado. Debe invocarse FrontendServices.init() antes de usar los servicios.");
        }
        return authService;
    }
}

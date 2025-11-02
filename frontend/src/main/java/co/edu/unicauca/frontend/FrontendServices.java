package co.edu.unicauca.frontend;

import co.edu.unicauca.frontend.infra.http.HttpAuthApi;
import co.edu.unicauca.frontend.services.auth.AuthApi;
import co.edu.unicauca.frontend.services.auth.AuthServiceFront;
import co.edu.unicauca.frontend.services.departmenthead.DepartmentHeadServiceFront;
import co.edu.unicauca.frontend.infra.http.HttpDepartmentHeadApi;
import co.edu.unicauca.frontend.infra.config.AppConfig;

/**
 * Punto de composición principal de los servicios del frontend.
 */
public final class FrontendServices {

    private static AuthServiceFront authService;
    private static DepartmentHeadServiceFront departmentHeadService;

    private FrontendServices() { }

    public static void init() {
        String baseUrl = AppConfig.get("api.base-url");
        if (baseUrl == null || baseUrl.isBlank()) {
            System.err.println("[Advertencia] No se encontró 'api.base-url' en application.properties. Usando valor por defecto.");
            baseUrl = "http://localhost:8080/api";
        }

        // Inicializar servicio de autenticación
        String registerEndpoint = AppConfig.get("api.endpoint.register");
        String loginEndpoint = AppConfig.get("api.endpoint.login");
        AuthApi authApi = new HttpAuthApi(baseUrl, registerEndpoint, loginEndpoint);
        authService = new AuthServiceFront(authApi);

        // CORREGIDO: Usar las claves correctas del properties
        String sinEvaluadores = AppConfig.get("api.endpoint.sin-evaluadores");
        String buscar = AppConfig.get("api.endpoint.buscar");

        HttpDepartmentHeadApi departmentHeadApi = new HttpDepartmentHeadApi(baseUrl, sinEvaluadores, buscar);
        departmentHeadService = new DepartmentHeadServiceFront(departmentHeadApi);

        System.out.println("[INFO] Servicios del frontend inicializados correctamente con base URL: " + baseUrl);
        System.out.println("[INFO] Endpoint sin-evaluadores: " + sinEvaluadores);
        System.out.println("[INFO] Endpoint buscar: " + buscar);
    }

    public static AuthServiceFront authService() {
        if (authService == null) {
            throw new IllegalStateException("FrontendServices no ha sido inicializado.");
        }
        return authService;
    }

    public static DepartmentHeadServiceFront departmentHeadService() {
        if (departmentHeadService == null) {
            throw new IllegalStateException("FrontendServices no ha sido inicializado.");
        }
        return departmentHeadService;
    }
}
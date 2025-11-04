package co.edu.unicauca.frontend;

import co.edu.unicauca.frontend.infra.http.HttpAuthApi;
import co.edu.unicauca.frontend.infra.http.HttpDepartmentHeadApi;
import co.edu.unicauca.frontend.infra.config.AppConfig;
import co.edu.unicauca.frontend.services.DocenteService;
import co.edu.unicauca.frontend.services.EstudianteService;
import co.edu.unicauca.frontend.services.ProyectoService;
import co.edu.unicauca.frontend.services.auth.AuthApi;
import co.edu.unicauca.frontend.services.auth.AuthServiceFront;
import co.edu.unicauca.frontend.services.departmenthead.DepartmentHeadServiceFront;
// 👉 nuevos
import co.edu.unicauca.frontend.services.coordinator.CoordinadorClient;
import co.edu.unicauca.frontend.services.coordinator.FormatoService;

/**
 * Punto de composición principal de los servicios del frontend.
 */
public final class FrontendServices {

    private static AuthServiceFront authService;
    private static DepartmentHeadServiceFront departmentHeadService;

    // 👉 nuevos servicios expuestos
    private static CoordinadorClient coordinadorClient;
    private static FormatoService formatoService;

    private static DocenteService docenteService;
    private static EstudianteService estudianteService;
    private static ProyectoService proyectoService;

    private FrontendServices() { }

    public static void init() {
        String baseUrl = AppConfig.get("api.base-url");
        if (baseUrl == null || baseUrl.isBlank()) {
            System.err.println("[Advertencia] No se encontró 'api.base-url' en application.properties. Usando valor por defecto.");
            baseUrl = "http://localhost:8080/api";
        }

        // ================== Auth ==================
        String registerEndpoint = AppConfig.get("api.endpoint.register");
        String loginEndpoint = AppConfig.get("api.endpoint.login");
        AuthApi authApi = new HttpAuthApi(baseUrl, registerEndpoint, loginEndpoint);
        authService = new AuthServiceFront(authApi);

        // ============ Department Head ============
        String sinEvaluadores = AppConfig.get("api.endpoint.sin-evaluadores");
        String buscar = AppConfig.get("api.endpoint.buscar");
        HttpDepartmentHeadApi departmentHeadApi = new HttpDepartmentHeadApi(baseUrl, sinEvaluadores, buscar);
        departmentHeadService = new DepartmentHeadServiceFront(departmentHeadApi);

        // =============== Coordinator ===============
        coordinadorClient = new CoordinadorClient();
        formatoService = new FormatoService();

        docenteService = new DocenteService();
        estudianteService = new EstudianteService();
        proyectoService = new ProyectoService(docenteService, estudianteService);

        System.out.println("[INFO] Servicios del frontend inicializados correctamente con base URL: " + baseUrl);
        System.out.println("[INFO] Endpoint sin-evaluadores: " + sinEvaluadores);
        System.out.println("[INFO] Endpoint buscar: " + buscar);
    }

    public static AuthServiceFront authService() {
        if (authService == null) throw new IllegalStateException("FrontendServices no ha sido inicializado.");
        return authService;
    }

    public static DepartmentHeadServiceFront departmentHeadService() {
        if (departmentHeadService == null) throw new IllegalStateException("FrontendServices no ha sido inicializado.");
        return departmentHeadService;
    }


    public static CoordinadorClient coordinadorClient() {
        if (coordinadorClient == null) throw new IllegalStateException("FrontendServices no ha sido inicializado.");
        return coordinadorClient;
    }

    public static FormatoService formatoService() {
        if (formatoService == null) throw new IllegalStateException("FrontendServices no ha sido inicializado.");
        return formatoService;
    }

    public static DocenteService docenteService() {
        if (docenteService == null) throw new IllegalStateException("FrontendServices no ha sido inicializado.");
        return docenteService;
    }

    public static EstudianteService estudianteService() {
        if (estudianteService == null) throw new IllegalStateException("FrontendServices no ha sido inicializado.");
        return estudianteService;
    }

    public static ProyectoService proyectoService() {
        if (proyectoService == null) throw new IllegalStateException("FrontendServices no ha sido inicializado.");
        return proyectoService;
    }
}

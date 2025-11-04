package co.edu.unicauca.frontend.services;

import co.edu.unicauca.frontend.infra.dto.AnteproyectoDTO;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class EstudianteService {

    private final HttpClient http;
    private final ObjectMapper mapper;
    private final String baseUrlEstudiante;

    public EstudianteService() {
        this("http://localhost:8080/api/academic/estudiantes");
    }

    public EstudianteService(String baseUrlEstudiante) {
        this.http = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        this.mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        this.baseUrlEstudiante = baseUrlEstudiante;
    }

    public boolean estudianteLibrePorCorreo(String correo) {
        return getBoolean("/libre/" + enc(correo));
    }

    public boolean estudianteExistePorCorreo(String correo) {
        return getBoolean("/existe/" + enc(correo));
    }

    public boolean estudianteTieneProyectoEnTramitePorCorreo(String correo) {
        return getBoolean("/tieneProyectoEnTramite/" + enc(correo));
    }

    public boolean estudianteTieneFormatoAAprobado(String correo) {
        return getBoolean("/tieneFormatoAAprobado/" + enc(correo));
    }

    public boolean estudianteTieneAnteproyectoAsociado(String correo) {
        return getBoolean("/" + enc(correo) + "/tieneAnteproyecto");
    }

    public void setAntepAProyectoEst(AnteproyectoDTO a) {
        String correo = a.getEstudianteCorreo();

        if (!estudianteExistePorCorreo(correo)) {
            throw new IllegalArgumentException("El estudiante con el correo ingresado no existe");
        }
        if (!estudianteTieneProyectoEnTramitePorCorreo(correo)) {
            throw new IllegalArgumentException("El estudiante no tiene proyectos asociados");
        }
        if (!estudianteTieneFormatoAAprobado(correo)) {
            throw new IllegalArgumentException("El Formato A del estudiante no está en estado APROBADO");
        }
        if (estudianteTieneAnteproyectoAsociado(correo)) {
            throw new IllegalArgumentException("El estudiante ya tiene un anteproyecto asociado");
        }

        post("/asociarAnteproyecto/" + enc(correo), a);
    }

    // -------------------- helpers --------------------
    private boolean getBoolean(String path) {
        try {
            HttpRequest req = HttpRequest.newBuilder(URI.create(baseUrlEstudiante + path))
                    .timeout(Duration.ofSeconds(8))
                    .GET()
                    .build();
            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            validar2xx(resp);
            return Boolean.parseBoolean(resp.body().trim());
        } catch (Exception ex) {
            throw new RuntimeException("Error al consumir servicio: " + ex.getMessage(), ex);
        }
    }

    private void post(String path, Object body) {
        try {
            String json = mapper.writeValueAsString(body);
            HttpRequest req = HttpRequest.newBuilder(URI.create(baseUrlEstudiante + path))
                    .timeout(Duration.ofSeconds(8))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            validar2xx(resp);
        } catch (Exception ex) {
            throw new RuntimeException("Error al enviar petición POST: " + ex.getMessage(), ex);
        }
    }

    private static void validar2xx(HttpResponse<?> resp) {
        int s = resp.statusCode();
        if (s < 200 || s >= 300) {
            throw new IllegalStateException("HTTP " + s + " - " + resp.body());
        }
    }

    private static String enc(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
}

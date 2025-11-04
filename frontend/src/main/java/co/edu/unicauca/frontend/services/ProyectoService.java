package co.edu.unicauca.frontend.services;

import co.edu.unicauca.frontend.entities.*;
import co.edu.unicauca.frontend.infra.config.PdfValidator;
import co.edu.unicauca.frontend.infra.dto.AnteproyectoDTO;
import co.edu.unicauca.frontend.infra.dto.FormatoADTO;
import co.edu.unicauca.frontend.infra.dto.ProyectoDTO;
import co.edu.unicauca.frontend.infra.dto.ProyectoInfoDTO;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class ProyectoService implements ObservableService {

    private final String baseUrlProyectos;
    private final HttpClient http;
    private final ObjectMapper mapper;
    private final List<Observer> observers = new ArrayList<>();

    DocenteService docenteService = new DocenteService();
    EstudianteService estudianteService = new EstudianteService();

    public ProyectoService() {
        this("http://localhost:8080/api/academic/proyectos");
    }

    public ProyectoService(String baseUrlProyectos) {
        this.baseUrlProyectos = baseUrlProyectos;
        this.http = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        this.mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    // ------------------------- Endpoints GET sencillos -------------------------

    public EstadoProyecto enforceAutoCancelIfNeeded(long proyectoId) {
        String url = baseUrlProyectos + "/" + proyectoId + "/enforceAutoCancel";
        return get(url, EstadoProyecto.class);
    }

    public int maxVersionFormatoA(long id) {
        String url = baseUrlProyectos + "/" + id + "/formatoA/max-version";
        Integer r = get(url, Integer.class);
        return r != null ? r : 0;
    }

    public boolean canResubmit(long proyectoId) {
        String url = baseUrlProyectos + "/resubmit/" + proyectoId;
        Boolean b = get(url, Boolean.class);
        return b != null && b;
    }

    public boolean tieneObservacionesFormatoA(long proyectoId) {
        String url = baseUrlProyectos + "/observacionesFA/" + proyectoId;
        Boolean b = get(url, Boolean.class);
        return b != null && b;
    }

    public boolean existeProyecto(long proyectoId) {
        String url = baseUrlProyectos + "/existeProyecto/" + proyectoId;
        Boolean b = get(url, Boolean.class);
        return b != null && b;
    }

    public String getEstadoProyecto(long proyectoId) {
        String url = baseUrlProyectos + "/estadoProyecto/" + proyectoId;
        return get(url, String.class);
    }

    // ------------------------- Listados -------------------------

    public List<ProyectoInfoDTO> listarProyectosDocente(String correoDocente, String filtro) {
        String url = baseUrlProyectos + "/docente/" + enc(correoDocente);
        if (filtro != null && !filtro.isBlank()) {
            url += "?filtro=" + enc(filtro);
        }
        return getList(url, ProyectoInfoDTO.class);
    }

    public List<AnteproyectoDTO> listarAnteproyectosDocente(String correo, String filtro) {
        String url = baseUrlProyectos + "/docente/" + enc(correo) + "/anteproyectos";
        if (filtro != null && !filtro.isBlank()) {
            url += "?filtro=" + enc(filtro);
        }
        return getList(url, AnteproyectoDTO.class);
    }

    // ------------------------- Crear / modificar -------------------------

    public void crearProyecto(ProyectoDTO proyecto) {
        try {
            if (!docenteService.docenteTieneCupo(proyecto.getDirector())) {
                throw new IllegalStateException("El docente alcanzó el límite de 7 proyectos en curso");
            }
            if (!estudianteService.estudianteExistePorCorreo(proyecto.getEstudiante())) {
                throw new IllegalArgumentException("El correo no pertenece a un estudiante");
            }
            if (!estudianteService.estudianteLibrePorCorreo(proyecto.getEstudiante())) {
                throw new IllegalStateException("El estudiante ya tiene un proyecto en curso");
            }

            postJson(baseUrlProyectos + "/crearConArchivos", proyecto);
            notifyObservers();
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al crear el proyecto: " + e.getMessage(), e);
        }
    }

    public void insertarFormatoA(FormatoADTO formatoADTO, long proyectoId) {
        String url = baseUrlProyectos + "/insertarFormatoAProyecto/" + proyectoId;
        postJson(url, formatoADTO);
    }

    public FormatoADTO subirNuevaVersionFormatoA(long proyectoId, FormatoADTO formatoADTO) {
        if (!existeProyecto(proyectoId))
            throw new IllegalArgumentException("Proyecto no existe");

        String estado = getEstadoProyecto(proyectoId);
        if (!"EN_TRAMITE".equalsIgnoreCase(estado))
            throw new IllegalStateException("El proyecto no está en curso");

        int max = maxVersionFormatoA(proyectoId);
        if (max >= 3)
            throw new IllegalStateException("Se alcanzó el máximo de 3 versiones del Formato A");

        PdfValidator.assertPdf(formatoADTO.getNombreFormato(), formatoADTO.getBlob());

        formatoADTO.setNroVersion(max + 1);
        formatoADTO.setEstado(EstadoArchivo.PENDIENTE);

        insertarFormatoA(formatoADTO, proyectoId);
        notifyObservers();
        return formatoADTO;
    }

    public int countProyectosByEstadoYTipo(String tipo, String estado, String correo) {
        try {
            TipoProyecto tipoEnum = TipoProyecto.valueOf(tipo.toUpperCase().replace(" ", "_"));
            EstadoProyecto estadoEnum = EstadoProyecto.valueOf(estado.toUpperCase().replace(" ", "_"));
            String url = String.format("%s/countProyectosBy?tipoProyecto=%s&estadoProyecto=%s&correoDocente=%s",
                    baseUrlProyectos, enc(tipoEnum.name()), enc(estadoEnum.name()), enc(correo));
            Integer r = get(url, Integer.class);
            return r != null ? r : 0;
        } catch (Exception e) {
            System.err.println("Error al contar proyectos: " + e.getMessage());
            return 0;
        }
    }

    public AnteproyectoDTO obtenerAnteproyecto(long proyectoId) {
        String url = baseUrlProyectos + "/" + proyectoId + "/anteproyecto";
        AnteproyectoDTO dto = get(url, AnteproyectoDTO.class);
        if (dto == null) {
            throw new RuntimeException("No se encontró anteproyecto para el proyecto con ID: " + proyectoId);
        }
        return dto;
    }

    public FormatoADTO obtenerUltimoFormatoAConObservaciones(long proyectoId) {
        String url = baseUrlProyectos + "/ultimoFormatoAConObservaciones/" + proyectoId;
        FormatoADTO dto = get(url, FormatoADTO.class);
        if (dto == null) {
            throw new RuntimeException("No se encontró un Formato A observado para el proyecto con ID: " + proyectoId);
        }
        return dto;
    }

    // ------------------------- Observer pattern -------------------------

    @Override
    public void addObserver(Observer o) { observers.add(o); }

    @Override
    public void removeObserver(Observer o) { observers.remove(o); }

    @Override
    public void notifyObservers() { observers.forEach(Observer::update); }

    // ------------------------- HTTP helpers -------------------------

    private <T> T get(String url, Class<T> type) {
        try {
            HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();
            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            ensure2xx(resp);
            if (type == String.class) {
                @SuppressWarnings("unchecked") T cast = (T) resp.body();
                return cast;
            }
            return mapper.readValue(resp.body(), type);
        } catch (Exception ex) {
            throw new RuntimeException("GET " + url + " falló: " + ex.getMessage(), ex);
        }
    }

    private <T> List<T> getList(String url, Class<T> elementType) {
        try {
            HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                    .timeout(Duration.ofSeconds(12))
                    .GET()
                    .build();
            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            ensure2xx(resp);
            return mapper.readValue(
                    resp.body(),
                    mapper.getTypeFactory().constructCollectionType(List.class, elementType)
            );
        } catch (Exception ex) {
            throw new RuntimeException("GET(list) " + url + " falló: " + ex.getMessage(), ex);
        }
    }

    private void postJson(String url, Object body) {
        try {
            String json = mapper.writeValueAsString(body);
            HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                    .timeout(Duration.ofSeconds(12))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            ensure2xx(resp);
        } catch (Exception ex) {
            throw new RuntimeException("POST " + url + " falló: " + ex.getMessage(), ex);
        }
    }

    private static void ensure2xx(HttpResponse<?> resp) {
        int sc = resp.statusCode();
        if (sc < 200 || sc >= 300) {
            throw new IllegalStateException("HTTP " + sc + " - " + resp.body());
        }
    }

    private static String enc(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
}

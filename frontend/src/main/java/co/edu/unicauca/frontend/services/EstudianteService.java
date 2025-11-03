package co.edu.unicauca.frontend.services;

import co.edu.unicauca.frontend.infra.dto.AnteproyectoDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

public class EstudianteService {
    private final RestTemplate restTemplate;
    private final String baseUrlEstudiante = "http://localhost:8080/api/academic/estudiantes";

    public EstudianteService() {
        this.restTemplate = new RestTemplate();
    }

    public boolean estudianteLibrePorCorreo(String correo) {
        try {
            String url = baseUrlEstudiante + "/libre/" + correo;
            return restTemplate.getForObject(url, Boolean.class);
        } catch (Exception e) {
            throw new RuntimeException("Error: ", e);
        }
    }

    public boolean estudianteExistePorCorreo(String correo) {
        try {
            String url = baseUrlEstudiante + "/existe/" + correo;
            return restTemplate.getForObject(url, Boolean.class);
        } catch (Exception e) {
            throw new RuntimeException("Error: ", e);
        }
    }

    public boolean estudianteTieneProyectoEnTramitePorCorreo(String correo) {
        try {
            String url = baseUrlEstudiante + "/tieneProyectoEnTramite/" + correo;
            return restTemplate.getForObject(url, Boolean.class);
        } catch (Exception e) {
            throw new RuntimeException("Error: ", e);
        }
    }

    public boolean estudianteTieneFormatoAAprobado(String correo) {
        try {
            String url = baseUrlEstudiante + "/tieneFormatoAAprobado/" + correo;
            return restTemplate.getForObject(url, Boolean.class);
        } catch (Exception e) {
            throw new RuntimeException("Error: ", e);
        }
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
        String url = baseUrlEstudiante + "/asociarAnteproyecto/" + correo;

        try {
            restTemplate.postForObject(url, a, String.class);
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            String mensajeError = ex.getResponseBodyAsString();
            throw new RuntimeException(mensajeError, ex);
        } catch (Exception ex) {
            throw new RuntimeException("Error inesperado: " + ex.getMessage(), ex);
        }
    }

    public boolean estudianteTieneAnteproyectoAsociado(String correo) {
        String url = baseUrlEstudiante + "/" + correo + "/tieneAnteproyecto";
        try {
            ResponseEntity<Boolean> response = restTemplate.getForEntity(url, Boolean.class);
            return Boolean.TRUE.equals(response.getBody());
        } catch (HttpClientErrorException.NotFound ex) {
            throw new IllegalArgumentException("No se encontró el estudiante con el correo ingresado");
        } catch (Exception ex) {
            throw new RuntimeException("Error al verificar el anteproyecto del estudiante: " + ex.getMessage(), ex);
        }
    }
}

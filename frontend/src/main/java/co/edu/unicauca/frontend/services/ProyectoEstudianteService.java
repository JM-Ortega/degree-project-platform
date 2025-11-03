package co.edu.unicauca.frontend.services;

import co.edu.unicauca.frontend.infra.dto.ProyectoEstudianteDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;

public class ProyectoEstudianteService {

    private static final String BASE_URL = "http://localhost:8080/api/academic/proyectos";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper;

    public ProyectoEstudianteService() {
        mapper = new ObjectMapper();
        // Modulo que permite a Jackson leer LocalDate / LocalDateTime
        mapper.registerModule(new JavaTimeModule());
    }

    public List<ProyectoEstudianteDTO> obtenerProyectosEstudiante() {
        try {
            // Por ahora lo quemo
            String correo = "lau@unicauca.edu.co";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(BASE_URL + "/listar/" + correo))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            List<ProyectoEstudianteDTO> dtoList = mapper.readValue(
                    response.body(),
                    new TypeReference<List<ProyectoEstudianteDTO>>() {
                    }
            );

            // Convertir los DTOs en objetos que usa TableView
            return dtoList.stream()
                    .map(dto -> new ProyectoEstudianteDTO(
                            dto.getId(),
                            dto.getTitulo(),
                            dto.getTipoProyecto(),
                            dto.getNombreDirector(),
                            dto.getFechaEmision(),
                            dto.getEstadoProyecto()
                    ))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
}

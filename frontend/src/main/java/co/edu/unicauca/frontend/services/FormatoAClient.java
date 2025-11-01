package co.edu.unicauca.frontend.services;

import co.edu.unicauca.frontend.entities.FormatoAResumen;
import co.edu.unicauca.frontend.entities.FormatoAResumenDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;

public class FormatoAClient {
    private static final String BASE_URL = "http://localhost:8083/api/formatoA";
    private final ObjectMapper mapper;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public FormatoAClient() {
        mapper = new ObjectMapper();
        // Modulo que permite a Jackson leer LocalDate / LocalDateTime
        mapper.registerModule(new JavaTimeModule());
    }

    public List<FormatoAResumen> obtenerFormatosAResumen() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(BASE_URL + "/listar"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            List<FormatoAResumenDTO> dtoList = mapper.readValue(
                    response.body(),
                    new TypeReference<List<FormatoAResumenDTO>>() {}
            );

            // Convertir los DTOs en objetos que usa TableView
            return dtoList.stream()
                    .map(dto -> new FormatoAResumen(
                            dto.getId(),
                            dto.getNombreProyecto(),
                            dto.getNombreDirector(),
                            dto.getTipoProyecto() != null ? dto.getTipoProyecto().toString() : "",
                            dto.getFechaSubida(),
                            dto.getEstadoFormatoA() != null ? dto.getEstadoFormatoA().toString() : "",
                            dto.getNroVersion()
                    ))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public byte[] descargarFormatoA(Long id) throws Exception {
        URL url = new URL(BASE_URL + "/" + id + "/descargar");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        try (InputStream in = conn.getInputStream()) {
            return in.readAllBytes();
        }
    }
}

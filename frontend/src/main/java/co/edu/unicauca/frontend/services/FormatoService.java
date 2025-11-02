package co.edu.unicauca.frontend.services;

import co.edu.unicauca.frontend.entities.FormatoAResumen;
import co.edu.unicauca.frontend.entities.FormatoAResumenDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

public class FormatoService {
    private static final String BASE_URL = "http://localhost:8083/api/formatoA";

    private static final String BOUNDARY = "---JavaBoundary";
    private static final String LINE_FEED = "\r\n";

    private final ObjectMapper mapper;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public FormatoService() {
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
                    new TypeReference<List<FormatoAResumenDTO>>() {
                    }
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
                            dto.getNroVersion(),
                            dto.getNombreFormatoA()
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

    public HttpResponse<String> actualizarFormato(Long formatoId, String nuevoEstado, File archivo, String nombreArchivo, String horaActual)
            throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest.BodyPublisher body = buildMultipartBody(archivo, nuevoEstado, nombreArchivo, horaActual);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/actualizar/" + formatoId))
                .header("Content-Type", "multipart/form-data; boundary=" + BOUNDARY)
                .PUT(body)
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpRequest.BodyPublisher buildMultipartBody(File archivo, String nuevoEstado, String nombreArchivo, String horaActual) throws IOException {
        StringBuilder sb = new StringBuilder();

        // Campo estado
        sb.append("--").append(BOUNDARY).append(LINE_FEED)
                .append("Content-Disposition: form-data; name=\"nuevoEstado\"").append(LINE_FEED)
                .append(LINE_FEED).append(nuevoEstado).append(LINE_FEED);

        // Campo nombre del archivo
        sb.append("--").append(BOUNDARY).append(LINE_FEED)
                .append("Content-Disposition: form-data; name=\"nombreArchivo\"").append(LINE_FEED)
                .append(LINE_FEED).append(nombreArchivo).append(LINE_FEED);

        // Campo hora actual
        sb.append("--").append(BOUNDARY).append(LINE_FEED)
                .append("Content-Disposition: form-data; name=\"horaActual\"").append(LINE_FEED)
                .append(LINE_FEED).append(horaActual).append(LINE_FEED);

        // Campo archivo PDF
        sb.append("--").append(BOUNDARY).append(LINE_FEED)
                .append("Content-Disposition: form-data; name=\"archivo\"; filename=\"")
                .append(archivo.getName()).append("\"").append(LINE_FEED)
                .append("Content-Type: application/pdf").append(LINE_FEED)
                .append(LINE_FEED);

        // Construcción final
        byte[] inicio = sb.toString().getBytes(StandardCharsets.UTF_8);
        byte[] archivoBytes = Files.readAllBytes(archivo.toPath());
        byte[] fin = (LINE_FEED + "--" + BOUNDARY + "--" + LINE_FEED).getBytes(StandardCharsets.UTF_8);

        return HttpRequest.BodyPublishers.ofByteArrays(List.of(inicio, archivoBytes, fin));
    }
}

package co.edu.unicauca.frontend.services;

import co.edu.unicauca.frontend.entities.Archivo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

public class FormatoAClient {
    private static final String BASE_URL = "http://localhost:8082/api/formatoA"; // cambia el puerto según tu microservicio

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Obtiene todos los archivos FormatoA desde el microservicio coordinador.
     */
    public List<Archivo> listarTodosArchivos() throws IOException {
        String endpoint = BASE_URL + "/listar";
        String jsonResponse = sendGetRequest(endpoint);
        return objectMapper.readValue(jsonResponse, new TypeReference<List<Archivo>>() {});
    }

    /**
     * Busca un archivo FormatoA por su ID.
     */
    public Archivo buscarArchivoPorId(Long id) throws IOException {
        String endpoint = BASE_URL + "/" + id;
        String jsonResponse = sendGetRequest(endpoint);
        return objectMapper.readValue(jsonResponse, Archivo.class);
    }

    /**
     * Método auxiliar para hacer peticiones GET simples.
     */
    private String sendGetRequest(String endpoint) throws IOException {
        URL url = new URL(endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new IOException("Error en la solicitud GET. Código de respuesta: " + responseCode);
        }

        StringBuilder inline = new StringBuilder();
        try (Scanner scanner = new Scanner(url.openStream())) {
            while (scanner.hasNext()) {
                inline.append(scanner.nextLine());
            }
        }
        return inline.toString();
    }
}

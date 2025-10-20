package co.edu.unicauca.frontend.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CoordinadorClient {
    private final HttpClient client = HttpClient.newHttpClient();

    public String getCoordinadorInfo(Long id) throws Exception {
        String url = "http://localhost:8083/api/coordinadores/" + id + "/info";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body(); // Devuelve el JSON como texto
    }
}

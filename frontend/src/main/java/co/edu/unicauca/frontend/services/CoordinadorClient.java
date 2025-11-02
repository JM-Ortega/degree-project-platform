package co.edu.unicauca.frontend.services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CoordinadorClient {
    private final HttpClient client = HttpClient.newHttpClient();

    private static final String BASE_URL = "http://localhost:8083/api/coordinadores";

    public String getCoordinadorInfo(String correo) throws Exception {
        String url = BASE_URL + "/" + correo + "/info";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body(); // Devuelve el JSON como texto
    }
}

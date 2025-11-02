package co.edu.unicauca.frontend.infra.http;

/**
 * Representa una excepción comprobada (checked exception) utilizada para
 * indicar errores ocurridos durante una comunicación HTTP con el backend.
 * <p>
 * Esta excepción encapsula tanto el código de estado HTTP como el cuerpo
 * de la respuesta devuelta por el servidor. Resulta útil para propagar
 * errores específicos de comunicación desde la capa de infraestructura
 * hacia las capas superiores (servicios, controladores o vistas).
 * </p>
 *
 * <p><b>Ejemplo de uso:</b></p>
 * <pre>{@code
 * try {
 *     String response = httpClient.get("/api/usuarios");
 * } catch (HttpClientException ex) {
 *     if (ex.getStatus() == 404) {
 *         System.err.println("Recurso no encontrado: " + ex.getResponseBody());
 *     }
 * }
 * }</pre>
 *
 * @author Juan Ortega
 * @since 1.0
 */
public class HttpClientException extends Exception {

    /**
     * Código de estado HTTP devuelto por el servidor (por ejemplo, 400, 404, 500, etc.).
     */
    private final int status;

    /**
     * Cuerpo de la respuesta HTTP devuelto por el servidor, generalmente en formato JSON.
     */
    private final String responseBody;

    /**
     * Crea una nueva excepción que representa un error HTTP.
     *
     * @param status        el código de estado HTTP devuelto por el servidor.
     * @param responseBody  el cuerpo de la respuesta asociada al error.
     */
    public HttpClientException(int status, String responseBody) {
        super("HTTP " + status + ": " + responseBody);
        this.status = status;
        this.responseBody = responseBody;
    }

    /**
     * Devuelve el código de estado HTTP asociado a la respuesta del servidor.
     *
     * @return el código de estado HTTP (por ejemplo, 400, 401, 500).
     */
    public int getStatus() {
        return status;
    }

    /**
     * Devuelve el cuerpo completo de la respuesta HTTP asociada al error.
     *
     * @return una cadena con el cuerpo de la respuesta, típicamente en formato JSON o texto plano.
     */
    public String getResponseBody() {
        return responseBody;
    }
}

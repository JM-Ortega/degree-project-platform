package co.edu.unicauca.frontend.presentation;

import co.edu.unicauca.frontend.FrontendServices;
import co.edu.unicauca.frontend.dto.RegistroPersonaDto;
import co.edu.unicauca.frontend.infra.http.HttpClientException;
import co.edu.unicauca.frontend.presentation.navigation.ViewNavigator;
import co.edu.unicauca.frontend.services.auth.AuthServiceFront;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Controlador de la vista de registro de usuarios.
 * <p>
 * Lee los datos del formulario, construye el DTO que espera el backend,
 * delega el registro al servicio de aplicación del frontend y muestra
 * los mensajes de validación que correspondan.
 */
public class SignUpController {

    // ================= Campos del formulario =================

    /**
     * Mapper para interpretar errores JSON del backend.
     */
    private final ObjectMapper mapper = new ObjectMapper();
    @FXML
    private TextField txtNombres;
    @FXML
    private TextField txtApellidos;
    /**
     * Campo de correo/usuario institucional (en el FXML se llama USUARIO).
     */
    @FXML
    private TextField txtUsuario;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private TextField txtCelular;
    @FXML
    private ComboBox<String> cbPrograma;
    @FXML
    private CheckBox chkEstudiante;

    // ================= Labels de error =================
    @FXML
    private CheckBox chkDocente;
    @FXML
    private Label errNombres;
    @FXML
    private Label errUsuario;
    @FXML
    private Label errApellidos;
    @FXML
    private Label errPassword;
    @FXML
    private Label errPrograma;
    @FXML
    private Label errCelular;
    @FXML
    private Label errRol;
    /**
     * Label genérico para mostrar errores no asociados a un campo específico.
     */
    @FXML
    private Label lblGeneral;
    /**
     * Servicio de aplicación para autenticación/registro.
     */
    private AuthServiceFront authServiceFront;

    @FXML
    public void initialize() {
        this.authServiceFront = FrontendServices.authService();

        if (cbPrograma != null) {
            cbPrograma.getItems().addAll(
                    "IngenieriaDeSistemas",
                    "IngenieriaElectronica",
                    "IngenieriaCivil"
            );
        }

        if (chkEstudiante != null) {
            chkEstudiante.setSelected(true);
        }

        clearErrors();
    }

    /**
     * Acción del botón "Registrarse".
     * <p>
     * Si el registro es correcto:
     * 1. Muestra un cuadro de diálogo de confirmación.
     * 2. Redirige a la pantalla de inicio de sesión.
     */
    @FXML
    private void handleRegister() {
        clearErrors();

        // 1. Roles seleccionados
        List<String> roles = new ArrayList<>();
        if (chkEstudiante != null && chkEstudiante.isSelected()) {
            roles.add("Estudiante");
        }
        if (chkDocente != null && chkDocente.isSelected()) {
            roles.add("Docente");
        }

        // 2. DTO de registro
        RegistroPersonaDto dto = new RegistroPersonaDto(
                val(txtNombres),
                val(txtApellidos),
                val(txtUsuario).toLowerCase(),
                val(txtPassword),
                val(txtCelular),
                cbPrograma != null ? cbPrograma.getValue() : null,
                roles,
                null  // este formulario no tiene departamento
        );

        try {
            // primero se valida en cliente
            Map<String, String> errors = authServiceFront.register(dto);

            if (!errors.isEmpty()) {
                // son errores de validación de FRONT
                mapErrorsToLabels(errors);
                return;
            }

            // si no hubo errores → éxito
            showSuccessAndGoToLogin();

        } catch (HttpClientException httpEx) {
            // aquí vienen los errores del backend (400, 500...)
            handleBackendError(httpEx);

        } catch (Exception e) {
            // error inesperado
            show(lblGeneral, "Error al registrar: " + e.getMessage());
            showAlert("Error al registrar",
                    "Ocurrió un error inesperado.",
                    e.getMessage());
        }
    }

    /**
     * Acción del enlace inferior "Inicia sesión".
     */
    @FXML
    private void goToLogin() {
        ViewNavigator.goTo(
                "/co/edu/unicauca/frontend/view/SignIn.fxml",
                "Inicio de sesión"
        );
    }

    // =========================================================
    // Manejo de errores de BACKEND
    // =========================================================

    /**
     * Procesa la excepción HTTP proveniente del backend.
     *
     * @param httpEx excepción con el status y el cuerpo JSON.
     */
    private void handleBackendError(HttpClientException httpEx) {
        String body = httpEx.getResponseBody();
        if (body == null || body.isBlank()) {
            // no hay cuerpo → mostrar solo general
            show(lblGeneral, "Error del servidor (" + httpEx.getStatus() + ").");
            showAlert("Registro no completado",
                    "El servidor devolvió un error.",
                    "Código: " + httpEx.getStatus());
            return;
        }

        try {
            // el backend puede mandar:
            // 1) {"error":"Ya existe un usuario con ese correo"}
            // 2) {"email":"mensaje...", "password":"..."}
            Map<String, String> errorMap = mapper.readValue(
                    body,
                    new TypeReference<Map<String, String>>() {
                    }
            );

            boolean mappedToField = false;

            for (Map.Entry<String, String> entry : errorMap.entrySet()) {
                String field = entry.getKey();
                String msg = entry.getValue();

                switch (field) {
                    case "nombres" -> {
                        show(errNombres, msg);
                        mappedToField = true;
                    }
                    case "apellidos" -> {
                        show(errApellidos, msg);
                        mappedToField = true;
                    }
                    case "email" -> {
                        // caso típico: "Ya existe un usuario con ese correo"
                        show(errUsuario, msg);
                        mappedToField = true;
                    }
                    case "password" -> {
                        show(errPassword, msg);
                        mappedToField = true;
                    }
                    case "programa" -> {
                        show(errPrograma, msg);
                        mappedToField = true;
                    }
                    case "celular" -> {
                        show(errCelular, msg);
                        mappedToField = true;
                    }
                    case "roles" -> {
                        show(errRol, msg);
                        mappedToField = true;
                    }
                    case "error" -> {
                        // mensaje general del backend
                        if (msg.toLowerCase().contains("correo") || msg.toLowerCase().contains("email")) {
                            // si menciona el correo → mostrarlo en el label de correo
                            show(errUsuario, msg);
                        } else {
                            // si es un mensaje general → mostrarlo en el general y alerta
                            show(lblGeneral, msg);
                            showAlert("Registro no completado",
                                    "El registro no pudo realizarse.",
                                    msg);
                        }
                        mappedToField = true;
                    }

                    default -> {
                        // clave desconocida → lo mostramos general
                        show(lblGeneral, msg);
                        showAlert("Registro no completado",
                                "El registro no pudo realizarse.",
                                msg);
                        mappedToField = true;
                    }
                }
            }

            if (!mappedToField) {
                // por si el JSON era vacío o no tenía campos esperados
                show(lblGeneral, "Error del servidor.");
                showAlert("Registro no completado",
                        "El registro no pudo realizarse.",
                        body);
            }

        } catch (Exception parseEx) {
            // el body no era JSON → mostrar directo
            show(lblGeneral, body);
            showAlert("Registro no completado",
                    "El registro no pudo realizarse.",
                    body);
        }
    }

    // =========================================================
    // Helpers de UI
    // =========================================================

    private void mapErrorsToLabels(Map<String, String> errors) {
        errors.forEach((field, msg) -> {
            switch (field) {
                case "nombres" -> show(errNombres, msg);
                case "apellidos" -> show(errApellidos, msg);
                case "email" -> show(errUsuario, msg);
                case "password" -> show(errPassword, msg);
                case "programa" -> show(errPrograma, msg);
                case "celular" -> show(errCelular, msg);
                case "roles" -> show(errRol, msg);
                default -> show(lblGeneral, msg);
            }
        });
    }

    private void showSuccessAndGoToLogin() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registro exitoso");
        alert.setHeaderText(null);
        alert.setContentText("El usuario fue registrado correctamente.");
        alert.showAndWait();

        ViewNavigator.goTo(
                "/co/edu/unicauca/frontend/view/SignIn.fxml",
                "Inicio de sesión"
        );
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void clearErrors() {
        hide(errNombres);
        hide(errApellidos);
        hide(errUsuario);
        hide(errPassword);
        hide(errPrograma);
        hide(errCelular);
        hide(errRol);
        hide(lblGeneral);
    }

    private String val(TextInputControl c) {
        if (c == null) return "";
        String t = c.getText();
        return (t == null) ? "" : t.trim();
    }



    private void show(Label lbl, String msg) {
        if (lbl == null) {
            return;
        }
        lbl.setText(msg);
        lbl.getStyleClass().remove("error-hidden");
        lbl.setStyle("-fx-text-fill: red;");
    }

    private void hide(Label lbl) {
        if (lbl == null) {
            return;
        }
        lbl.setText(" ");
        if (!lbl.getStyleClass().contains("error-hidden")) {
            lbl.getStyleClass().add("error-hidden");
        }
    }
}

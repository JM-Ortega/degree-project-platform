package co.edu.unicauca.frontend.presentation;

import co.edu.unicauca.frontend.FrontendServices;
import co.edu.unicauca.frontend.dto.RegistroPersonaDto;
import co.edu.unicauca.frontend.entities.enums.Departamento;
import co.edu.unicauca.frontend.entities.enums.Programa;
import co.edu.unicauca.frontend.entities.enums.Rol;
import co.edu.unicauca.frontend.infra.http.HttpClientException;
import co.edu.unicauca.frontend.presentation.navigation.ViewNavigator;
import co.edu.unicauca.frontend.services.auth.AuthServiceFront;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador de la vista de registro de usuarios.
 */
public class SignUpController {

    // ================= Campos del formulario =================
    private final ObjectMapper mapper = new ObjectMapper();

    @FXML
    private TextField txtNombres;
    @FXML
    private TextField txtApellidos;
    @FXML
    private TextField txtUsuario;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private TextField txtCelular;
    @FXML
    private ComboBox<Programa> cbPrograma;
    @FXML
    private ComboBox<Departamento> cbDepartamento;
    @FXML
    private CheckBox chkEstudiante;
    @FXML
    private CheckBox chkDocente;
    @FXML
    private VBox vboxDepartamento;
    @FXML
    private Label lblDepartamento;

    // ================= Labels de error =================
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
    @FXML
    private Label errDepartamento;
    @FXML
    private Label lblGeneral;

    private AuthServiceFront authServiceFront;

    @FXML
    public void initialize() {
        this.authServiceFront = FrontendServices.authService();

        // Configurar programas
        if (cbPrograma != null) {
            cbPrograma.getItems().addAll(Programa.values());
        }

        // Configurar departamentos
        if (cbDepartamento != null) {
            cbDepartamento.getItems().addAll(Departamento.values());
        }

        // Configurar visibilidad del departamento
        configurarVisibilidadDepartamento();

        if (chkEstudiante != null) {
            chkEstudiante.setSelected(true);
        }

        clearErrors();
    }

    /**
     * Configura la visibilidad del campo departamento
     */
    private void configurarVisibilidadDepartamento() {
        if (chkDocente != null && vboxDepartamento != null && lblDepartamento != null) {
            // Inicialmente ocultar departamento
            ocultarCampoDepartamento();

            // Listener para mostrar/ocultar cuando se selecciona Docente
            chkDocente.selectedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    mostrarCampoDepartamento();
                } else {
                    ocultarCampoDepartamento();
                }
            });
        }
    }

    /**
     * Muestra el campo departamento
     */
    private void mostrarCampoDepartamento() {
        if (vboxDepartamento != null && lblDepartamento != null) {
            vboxDepartamento.setVisible(true);
            vboxDepartamento.setManaged(true);
            lblDepartamento.setVisible(true);
            lblDepartamento.setManaged(true);
        }
    }

    /**
     * Oculta el campo departamento y limpia su valor
     */
    private void ocultarCampoDepartamento() {
        if (vboxDepartamento != null && lblDepartamento != null) {
            vboxDepartamento.setVisible(false);
            vboxDepartamento.setManaged(false);
            lblDepartamento.setVisible(false);
            lblDepartamento.setManaged(false);

            // Limpiar selección y errores
            if (cbDepartamento != null) {
                cbDepartamento.setValue(null);
            }
            hide(errDepartamento);
        }
    }

    @FXML
    private void handleRegister() {
        clearErrors();

        // Map para acumular errores
        Map<String, String> erroresFrontend = new LinkedHashMap<>();

        // 1. Roles seleccionados - ENVIAR NOMBRES REALES DE ENUM
        List<String> roles = new ArrayList<>();
        if (chkEstudiante != null && chkEstudiante.isSelected()) {
            roles.add(Rol.ESTUDIANTE.name()); // Envía "ESTUDIANTE"
        }
        if (chkDocente != null && chkDocente.isSelected()) {
            roles.add(Rol.DOCENTE.name()); // Envía "DOCENTE"
        }

        // Validar que haya al menos un rol seleccionado
        if (roles.isEmpty()) {
            erroresFrontend.put("roles", "Debe seleccionar al menos un rol");
        }

        // 2. Validar programa - ENVIAR NOMBRE REAL DE ENUM
        String programaSeleccionado = null;
        if (cbPrograma != null && cbPrograma.getValue() != null) {
            programaSeleccionado = cbPrograma.getValue().name(); // Envía "INGENIERIA_DE_SISTEMAS"
        } else {
            erroresFrontend.put("programa", "El programa académico es obligatorio");
        }

        // 3. Validar departamento SOLO si es Docente - ENVIAR NOMBRE REAL DE ENUM
        String departamentoSeleccionado = null;
        boolean esDocente = roles.contains(Rol.DOCENTE.name()); // Busca "DOCENTE"

        if (esDocente) {
            if (cbDepartamento != null && cbDepartamento.getValue() != null) {
                departamentoSeleccionado = cbDepartamento.getValue().name(); // Envía "SISTEMAS"
            } else {
                erroresFrontend.put("departamento", "El departamento es obligatorio para docentes");
            }
        }

        // 4. Validar campos obligatorios básicos
        if (val(txtNombres).isEmpty()) {
            erroresFrontend.put("nombres", "El nombre es obligatorio");
        }

        if (val(txtApellidos).isEmpty()) {
            erroresFrontend.put("apellidos", "El apellido es obligatorio");
        }

        if (val(txtUsuario).isEmpty()) {
            erroresFrontend.put("email", "El correo electrónico es obligatorio");
        } else if (!val(txtUsuario).toLowerCase().endsWith("@unicauca.edu.co")) {
            erroresFrontend.put("email", "El correo debe pertenecer al dominio @unicauca.edu.co");
        }

        if (val(txtPassword).isEmpty()) {
            erroresFrontend.put("password", "La contraseña es obligatoria");
        }

        // 5. Si hay errores de frontend, mostrarlos y detener el proceso
        if (!erroresFrontend.isEmpty()) {
            mapErrorsToLabels(erroresFrontend);
            return;
        }

        // 6. DTO de registro (solo si no hay errores de frontend)
        RegistroPersonaDto dto = new RegistroPersonaDto(
                val(txtNombres),
                val(txtApellidos),
                val(txtUsuario).toLowerCase(),
                val(txtPassword),
                val(txtCelular),
                programaSeleccionado, // "INGENIERIA_DE_SISTEMAS"
                roles,                // ["ESTUDIANTE", "DOCENTE"]
                departamentoSeleccionado // "SISTEMAS"
        );

        try {
            // Llamar al servicio para validaciones del backend
            Map<String, String> errorsBackend = authServiceFront.register(dto);

            if (!errorsBackend.isEmpty()) {
                mapErrorsToLabels(errorsBackend);
                return;
            }

            showSuccessAndGoToLogin();

        } catch (HttpClientException httpEx) {
            handleBackendError(httpEx);
        } catch (Exception e) {
            show(lblGeneral, "Error al registrar: " + e.getMessage());
            showAlert("Error al registrar", "Ocurrió un error inesperado.", e.getMessage());
        }
    }

    @FXML
    private void goToLogin() {
        ViewNavigator.goTo("/co/edu/unicauca/frontend/view/SignIn.fxml", "Inicio de sesión");
    }

    // =========================================================
    // Manejo de errores de BACKEND
    // =========================================================

    private void handleBackendError(HttpClientException httpEx) {
        String body = httpEx.getResponseBody();
        if (body == null || body.isBlank()) {
            show(lblGeneral, "Error del servidor (" + httpEx.getStatus() + ").");
            showAlert("Registro no completado", "El servidor devolvió un error.", "Código: " + httpEx.getStatus());
            return;
        }

        try {
            Map<String, String> errorMap = mapper.readValue(body, new TypeReference<Map<String, String>>() {});
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
                    case "departamento" -> {
                        show(errDepartamento, msg);
                        mappedToField = true;
                    }
                    case "error" -> {
                        if (msg.toLowerCase().contains("correo") || msg.toLowerCase().contains("email")) {
                            show(errUsuario, msg);
                        } else {
                            show(lblGeneral, msg);
                            showAlert("Registro no completado", "El registro no pudo realizarse.", msg);
                        }
                        mappedToField = true;
                    }
                    default -> {
                        show(lblGeneral, msg);
                        showAlert("Registro no completado", "El registro no pudo realizarse.", msg);
                        mappedToField = true;
                    }
                }
            }

            if (!mappedToField) {
                show(lblGeneral, "Error del servidor.");
                showAlert("Registro no completado", "El registro no pudo realizarse.", body);
            }

        } catch (Exception parseEx) {
            show(lblGeneral, body);
            showAlert("Registro no completado", "El registro no pudo realizarse.", body);
        }
    }

    // =========================================================
    // Helpers de UI
    // =========================================================

    private void mapErrorsToLabels(Map<String, String> errors) {
        // Primero ocultar todos los errores
        clearErrors();

        // Luego mostrar solo los errores que existen
        errors.forEach((field, msg) -> {
            switch (field) {
                case "nombres" -> show(errNombres, msg);
                case "apellidos" -> show(errApellidos, msg);
                case "email" -> show(errUsuario, msg);
                case "password" -> show(errPassword, msg);
                case "programa" -> show(errPrograma, msg);
                case "celular" -> show(errCelular, msg);
                case "roles" -> show(errRol, msg);
                case "departamento" -> show(errDepartamento, msg);
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
        ViewNavigator.goTo("/co/edu/unicauca/frontend/view/SignIn.fxml", "Inicio de sesión");
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
        hide(errDepartamento);
        hide(lblGeneral);
    }

    private String val(TextInputControl c) {
        if (c == null) return "";
        String t = c.getText();
        return (t == null) ? "" : t.trim();
    }

    private void show(Label lbl, String msg) {
        if (lbl == null) return;
        lbl.setText(msg);
        lbl.getStyleClass().remove("error-hidden");
        lbl.setStyle("-fx-text-fill: red;");
        lbl.setVisible(true);
        lbl.setManaged(true);
    }

    private void hide(Label lbl) {
        if (lbl == null) return;
        lbl.setText(" ");
        if (!lbl.getStyleClass().contains("error-hidden")) {
            lbl.getStyleClass().add("error-hidden");
        }
    }
}
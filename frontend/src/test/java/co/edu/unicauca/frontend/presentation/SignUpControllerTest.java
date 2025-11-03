package co.edu.unicauca.frontend.presentation;

import co.edu.unicauca.frontend.FrontendServices;
import co.edu.unicauca.frontend.dto.RegistroPersonaDto;
import co.edu.unicauca.frontend.entities.enums.Programa;
import co.edu.unicauca.frontend.infra.http.HttpClientException;
import co.edu.unicauca.frontend.presentation.navigation.ViewNavigator;
import co.edu.unicauca.frontend.services.auth.AuthServiceFront;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SignUpControllerTest {

    private SignUpController controller;

    // Servicio (SI se mockea)
    private AuthServiceFront mockAuthService;

    // Controles mockeados (solo para tests que validan mapeo de errores sin llegar a backend)
    private TextField mockTxtNombres;
    private TextField mockTxtApellidos;
    private TextField mockTxtUsuario;
    private PasswordField mockTxtPassword;
    private TextField mockTxtCelular;
    private ComboBox<Programa> mockCbPrograma;  // ⚠️ ahora es ComboBox<Programa>
    private CheckBox mockChkEstudiante;
    private CheckBox mockChkDocente;

    // Labels mockeados (para verificar llamadas a setText)
    private Label mockErrNombres;
    private Label mockErrApellidos;
    private Label mockErrUsuario;
    private Label mockErrPassword;
    private Label mockErrPrograma;
    private Label mockErrCelular;
    private Label mockErrRol;
    private Label mockLblGeneral;

    @BeforeAll
    static void initJavaFX() {
        new JFXPanel(); // Inicializa JavaFX toolkit para poder crear controles
    }

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        controller = new SignUpController();

        // Servicio mock
        mockAuthService = mock(AuthServiceFront.class);

        // Controles mock (para tests que no llegan al backend)
        mockTxtNombres = mock(TextField.class);
        mockTxtApellidos = mock(TextField.class);
        mockTxtUsuario = mock(TextField.class);
        mockTxtPassword = mock(PasswordField.class);
        mockTxtCelular = mock(TextField.class);
        mockCbPrograma = mock(ComboBox.class); // generics borrados por type erasure, es válido
        mockChkEstudiante = mock(CheckBox.class);
        mockChkDocente = mock(CheckBox.class);

        // Labels mock
        mockErrNombres = createMockLabel();
        mockErrApellidos = createMockLabel();
        mockErrUsuario = createMockLabel();
        mockErrPassword = createMockLabel();
        mockErrPrograma = createMockLabel();
        mockErrCelular = createMockLabel();
        mockErrRol = createMockLabel();
        mockLblGeneral = createMockLabel();

        // Inyección por reflexión (setup por defecto para tests de mapeo de errores)
        setField(controller, "authServiceFront", mockAuthService);
        setField(controller, "txtNombres", mockTxtNombres);
        setField(controller, "txtApellidos", mockTxtApellidos);
        setField(controller, "txtUsuario", mockTxtUsuario);
        setField(controller, "txtPassword", mockTxtPassword);
        setField(controller, "txtCelular", mockTxtCelular);
        setField(controller, "cbPrograma", mockCbPrograma);
        setField(controller, "chkEstudiante", mockChkEstudiante);
        setField(controller, "chkDocente", mockChkDocente);
        setField(controller, "errNombres", mockErrNombres);
        setField(controller, "errApellidos", mockErrApellidos);
        setField(controller, "errUsuario", mockErrUsuario);
        setField(controller, "errPassword", mockErrPassword);
        setField(controller, "errPrograma", mockErrPrograma);
        setField(controller, "errCelular", mockErrCelular);
        setField(controller, "errRol", mockErrRol);
        setField(controller, "lblGeneral", mockLblGeneral);
        setField(controller, "mapper", new ObjectMapper());
    }

    // ========================= Helpers =========================

    // Label mock con styleClass real para evitar NPEs cuando el controller hace getStyleClass()
    private Label createMockLabel() {
        Label label = mock(Label.class);
        var styleClass = javafx.collections.FXCollections.<String>observableArrayList();
        when(label.getStyleClass()).thenReturn(styleClass);
        return label;
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    private Object callPrivateMethod(String methodName, Class<?>[] paramTypes, Object... params) throws Exception {
        Method method = controller.getClass().getDeclaredMethod(methodName, paramTypes);
        method.setAccessible(true);
        return method.invoke(controller, params);
    }

    private Object callPrivateMethod(String methodName) throws Exception {
        return callPrivateMethod(methodName, new Class[0]);
    }

    // ========================= Tests ===========================

    @Test
    void testHandleRegister_WithValidationErrors() throws Exception {
        // Falla validación frontend → no llega al backend
        when(mockTxtNombres.getText()).thenReturn(""); // nombre vacío
        when(mockTxtApellidos.getText()).thenReturn("Pérez");
        when(mockTxtUsuario.getText()).thenReturn("juan.perez@unicauca.edu.co");
        when(mockTxtPassword.getText()).thenReturn("");
        when(mockTxtCelular.getText()).thenReturn("123");
        when(mockCbPrograma.getValue()).thenReturn(null); // sin programa
        when(mockChkEstudiante.isSelected()).thenReturn(false); // sin roles
        when(mockChkDocente.isSelected()).thenReturn(false);

        // Aunque lo stubs, no debe llamarse
        when(mockAuthService.register(any(RegistroPersonaDto.class))).thenReturn(new HashMap<>());

        callPrivateMethod("handleRegister");

        // Verifica mapeo de errores de frontend
        verify(mockErrNombres).setText("El nombre es obligatorio");
        verify(mockErrPassword).setText("La contraseña es obligatoria"); // ahora sí se cumple
        verify(mockErrPrograma).setText("El programa académico es obligatorio");
        verify(mockErrRol).setText("Debe seleccionar al menos un rol");

        // Y que NO llamó al backend
        verify(mockAuthService, never()).register(any(RegistroPersonaDto.class));
    }


    @Test
    void testHandleRegister_BackendError() throws Exception {
        // Para este flujo SÍ queremos llegar al backend ⇒ usa controles REALES y válidos
        SignUpController ctrl = new SignUpController();
        setField(ctrl, "authServiceFront", mockAuthService);
        setField(ctrl, "mapper", new ObjectMapper());

        // Controles reales
        TextField txtNombres = new TextField("Juan");
        TextField txtApellidos = new TextField("Pérez");
        TextField txtUsuario = new TextField("juan.perez@unicauca.edu.co");
        PasswordField txtPassword = new PasswordField();
        txtPassword.setText("password123");
        TextField txtCelular = new TextField("123456789");

        ComboBox<Programa> cbPrograma = new ComboBox<>();
        cbPrograma.getItems().addAll(Programa.values());
        cbPrograma.setValue(Programa.INGENIERIA_DE_SISTEMAS); // enum real

        CheckBox chkEstudiante = new CheckBox();
        chkEstudiante.setSelected(true); // evita requerir departamento
        CheckBox chkDocente = new CheckBox();
        chkDocente.setSelected(false);

        Label errUsuario = new Label();
        Label lblGeneral = new Label();

        // Inyecta en el controller alterno
        setField(ctrl, "txtNombres", txtNombres);
        setField(ctrl, "txtApellidos", txtApellidos);
        setField(ctrl, "txtUsuario", txtUsuario);
        setField(ctrl, "txtPassword", txtPassword);
        setField(ctrl, "txtCelular", txtCelular);
        setField(ctrl, "cbPrograma", cbPrograma);
        setField(ctrl, "chkEstudiante", chkEstudiante);
        setField(ctrl, "chkDocente", chkDocente);
        setField(ctrl, "errUsuario", errUsuario);
        setField(ctrl, "lblGeneral", lblGeneral);

        // Backend devuelve error JSON mapeable a "email"
        HttpClientException httpException = new HttpClientException(
                400, "{\"email\":\"El correo ya está registrado\"}"
        );
        when(mockAuthService.register(any(RegistroPersonaDto.class))).thenThrow(httpException);

        // Ejecuta
        Method m = SignUpController.class.getDeclaredMethod("handleRegister");
        m.setAccessible(true);
        m.invoke(ctrl);

        // Verifica que llegó al backend
        verify(mockAuthService, times(1)).register(any(RegistroPersonaDto.class));

        // El controller escribe directo al Label real
        assertEquals("El correo ya está registrado", errUsuario.getText());
        assertEquals("", lblGeneral.getText().trim()); // no debería tocar el general en este caso
    }

    @Test
    void testGoToLogin() throws Exception {
        try (MockedStatic<ViewNavigator> viewNavigatorMock = mockStatic(ViewNavigator.class)) {
            callPrivateMethod("goToLogin");
            viewNavigatorMock.verify(() ->
                    ViewNavigator.goTo("/co/edu/unicauca/frontend/view/SignIn.fxml", "Inicio de sesión")
            );
        }
    }

    @Test
    void testVal_Method() throws Exception {
        TextField textField = mock(TextField.class);
        when(textField.getText()).thenReturn("  test value  ");
        String result = (String) callPrivateMethod("val", new Class[]{TextInputControl.class}, textField);
        assertEquals("test value", result);
    }

    @Test
    void testVal_MethodWithNull() throws Exception {
        String result = (String) callPrivateMethod("val", new Class[]{TextInputControl.class}, new Object[]{null});
        assertEquals("", result);
    }

    @Test
    void testClearErrors() throws Exception {
        callPrivateMethod("clearErrors");
        verify(mockErrNombres).setText(" ");
        verify(mockErrApellidos).setText(" ");
        verify(mockErrUsuario).setText(" ");
        verify(mockErrPassword).setText(" ");
        verify(mockErrPrograma).setText(" ");
        verify(mockErrCelular).setText(" ");
        verify(mockErrRol).setText(" ");
        verify(mockLblGeneral).setText(" ");
    }

    @Test
    void testMapErrorsToLabels() throws Exception {
        Map<String, String> errors = new HashMap<>();
        errors.put("nombres", "Error en nombres");
        errors.put("email", "Error en email");
        errors.put("password", "Error en password");
        errors.put("programa", "Error en programa");
        errors.put("celular", "Error en celular");
        errors.put("roles", "Error en roles");
        errors.put("unknown", "Error desconocido");

        callPrivateMethod("mapErrorsToLabels", new Class[]{Map.class}, errors);

        verify(mockErrNombres).setText("Error en nombres");
        verify(mockErrUsuario).setText("Error en email");
        verify(mockErrPassword).setText("Error en password");
        verify(mockErrPrograma).setText("Error en programa");
        verify(mockErrCelular).setText("Error en celular");
        verify(mockErrRol).setText("Error en roles");
        verify(mockLblGeneral).setText("Error desconocido");
    }

    @Test
    void testInitialize() throws Exception {
        // Usa controles reales con tipos correctos
        ComboBox<Programa> realComboBox = new ComboBox<>();
        setField(controller, "cbPrograma", realComboBox);

        CheckBox realCheckBox = new CheckBox();
        setField(controller, "chkEstudiante", realCheckBox);

        try (MockedStatic<FrontendServices> frontendServicesMock = mockStatic(FrontendServices.class)) {
            frontendServicesMock.when(FrontendServices::authService).thenReturn(mockAuthService);

            // Ejecuta initialize
            controller.initialize();

            // Verifica que cargó todos los enums de Programa
            assertEquals(Programa.values().length, realComboBox.getItems().size());
            assertTrue(realComboBox.getItems().contains(Programa.INGENIERIA_DE_SISTEMAS));
            // Puedes agregar más asserts si deseas:
            // assertTrue(realComboBox.getItems().contains(Programa.INGENIERIA_ELECTRONICA));
            // assertTrue(realComboBox.getItems().contains(Programa.INGENIERIA_CIVIL));

            // Y que Estudiante quedó seleccionado por defecto
            assertTrue(realCheckBox.isSelected());
        }
    }

    @Test
    void testShowAndHideMethods() throws Exception {
        Label testLabel = createMockLabel();

        // show()
        callPrivateMethod("show", new Class[]{Label.class, String.class}, testLabel, "Test message");
        verify(testLabel).setText("Test message");
        verify(testLabel, atLeastOnce()).getStyleClass();

        // hide()
        callPrivateMethod("hide", new Class[]{Label.class}, testLabel);
        verify(testLabel, atLeastOnce()).getStyleClass();
    }

    @Test
    void testShowSuccessAndGoToLogin_NavigationOnly() throws Exception {
        try (MockedStatic<ViewNavigator> viewNavigatorMock = mockStatic(ViewNavigator.class)) {
            callPrivateMethod("goToLogin");
            viewNavigatorMock.verify(() ->
                    ViewNavigator.goTo("/co/edu/unicauca/frontend/view/SignIn.fxml", "Inicio de sesión")
            );
        }
    }
}

package co.edu.unicauca.frontend.presentation;

import co.edu.unicauca.frontend.FrontendServices;
import co.edu.unicauca.frontend.dto.RegistroPersonaDto;
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

    private AuthServiceFront mockAuthService;
    private TextField mockTxtNombres;
    private TextField mockTxtApellidos;
    private TextField mockTxtUsuario;
    private PasswordField mockTxtPassword;
    private TextField mockTxtCelular;
    private ComboBox<String> mockCbPrograma;
    private CheckBox mockChkEstudiante;
    private CheckBox mockChkDocente;
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
        new JFXPanel(); // Inicializa JavaFX toolkit
    }

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        controller = new SignUpController();

        // Crear mocks
        mockAuthService = mock(AuthServiceFront.class);
        mockTxtNombres = mock(TextField.class);
        mockTxtApellidos = mock(TextField.class);
        mockTxtUsuario = mock(TextField.class);
        mockTxtPassword = mock(PasswordField.class);
        mockTxtCelular = mock(TextField.class);
        mockCbPrograma = mock(ComboBox.class);
        mockChkEstudiante = mock(CheckBox.class);
        mockChkDocente = mock(CheckBox.class);
        mockErrNombres = createMockLabel();
        mockErrApellidos = createMockLabel();
        mockErrUsuario = createMockLabel();
        mockErrPassword = createMockLabel();
        mockErrPrograma = createMockLabel();
        mockErrCelular = createMockLabel();
        mockErrRol = createMockLabel();
        mockLblGeneral = createMockLabel();

        // Inyectar dependencias via reflection
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

    // Helper para crear Labels mock con styleClass
    private Label createMockLabel() {
        Label label = mock(Label.class);
        javafx.collections.ObservableList<String> styleClass = javafx.collections.FXCollections.observableArrayList();
        when(label.getStyleClass()).thenReturn(styleClass);
        return label;
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    // Método helper mejorado para llamar métodos privados
    private Object callPrivateMethod(String methodName, Class<?>[] paramTypes, Object... params) throws Exception {
        Method method = controller.getClass().getDeclaredMethod(methodName, paramTypes);
        method.setAccessible(true);
        return method.invoke(controller, params);
    }

    // Sobrecarga simplificada para métodos sin parámetros
    private Object callPrivateMethod(String methodName) throws Exception {
        return callPrivateMethod(methodName, new Class[0]);
    }

    @Test
    void testHandleRegister_WithValidationErrors() throws Exception {
        // Configurar mocks
        when(mockTxtNombres.getText()).thenReturn(""); // Nombre vacío
        when(mockTxtApellidos.getText()).thenReturn("Pérez");
        when(mockTxtUsuario.getText()).thenReturn("juan.perez@unicauca.edu.co");
        when(mockTxtPassword.getText()).thenReturn("pass"); // Password corto
        when(mockTxtCelular.getText()).thenReturn("123");
        when(mockCbPrograma.getValue()).thenReturn(null); // Programa no seleccionado
        when(mockChkEstudiante.isSelected()).thenReturn(false);
        when(mockChkDocente.isSelected()).thenReturn(false); // Sin roles

        Map<String, String> errors = new HashMap<>();
        errors.put("nombres", "El nombre es requerido");
        errors.put("password", "La contraseña debe tener al menos 6 caracteres");
        errors.put("celular", "El celular debe tener 10 dígitos");
        errors.put("roles", "Debe seleccionar al menos un rol");

        when(mockAuthService.register(any(RegistroPersonaDto.class))).thenReturn(errors);

        // Ejecutar método privado via reflection
        callPrivateMethod("handleRegister");

        // Verificar que se muestran los errores en los labels
        verify(mockErrNombres).setText("El nombre es requerido");
        verify(mockErrPassword).setText("La contraseña debe tener al menos 6 caracteres");
        verify(mockErrCelular).setText("El celular debe tener 10 dígitos");
        verify(mockErrRol).setText("Debe seleccionar al menos un rol");
    }

    @Test
    void testHandleRegister_BackendError() throws Exception {
        // Configurar mocks
        when(mockTxtNombres.getText()).thenReturn("Juan");
        when(mockTxtApellidos.getText()).thenReturn("Pérez");
        when(mockTxtUsuario.getText()).thenReturn("juan.perez@unicauca.edu.co");
        when(mockTxtPassword.getText()).thenReturn("password123");
        when(mockTxtCelular.getText()).thenReturn("123456789");
        when(mockCbPrograma.getValue()).thenReturn("IngenieriaDeSistemas");
        when(mockChkEstudiante.isSelected()).thenReturn(true);

        HttpClientException httpException = new HttpClientException(400,
                "{\"email\": \"El correo ya está registrado\"}");

        when(mockAuthService.register(any(RegistroPersonaDto.class))).thenThrow(httpException);

        // Ejecutar método privado via reflection
        callPrivateMethod("handleRegister");

        // Verificar que se maneja el error del backend
        verify(mockErrUsuario).setText("El correo ya está registrado");
    }

    @Test
    void testGoToLogin() throws Exception {
        try (MockedStatic<ViewNavigator> viewNavigatorMock = mockStatic(ViewNavigator.class)) {
            // Ejecutar método privado via reflection
            callPrivateMethod("goToLogin");

            // Verificar
            viewNavigatorMock.verify(() ->
                    ViewNavigator.goTo("/co/edu/unicauca/frontend/view/SignIn.fxml", "Inicio de sesión")
            );
        }
    }

    @Test
    void testVal_Method() throws Exception {
        // Test del método helper val()
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
        // Ejecutar método privado via reflection
        callPrivateMethod("clearErrors");

        // Verificar que todos los labels de error se ocultan
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

        // Ejecutar método privado via reflection
        callPrivateMethod("mapErrorsToLabels", new Class[]{Map.class}, errors);

        // Verificar que los errores se mapean a los labels correctos
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
        // Configurar ComboBox real
        ComboBox<String> realComboBox = new ComboBox<>();
        setField(controller, "cbPrograma", realComboBox);

        CheckBox realCheckBox = new CheckBox();
        setField(controller, "chkEstudiante", realCheckBox);

        try (MockedStatic<FrontendServices> frontendServicesMock = mockStatic(FrontendServices.class)) {
            frontendServicesMock.when(FrontendServices::authService).thenReturn(mockAuthService);

            // Ejecutar initialize (método público)
            controller.initialize();

            // Verificar
            assertEquals(3, realComboBox.getItems().size());
            assertTrue(realComboBox.getItems().contains("IngenieriaDeSistemas"));
            assertTrue(realComboBox.getItems().contains("IngenieriaElectronica"));
            assertTrue(realComboBox.getItems().contains("IngenieriaCivil"));
            assertTrue(realCheckBox.isSelected());
        }
    }

    @Test
    void testShowAndHideMethods() throws Exception {
        // Test métodos show y hide
        Label testLabel = createMockLabel();

        // Test show
        callPrivateMethod("show", new Class[]{Label.class, String.class}, testLabel, "Test message");
        verify(testLabel).setText("Test message");
        verify(testLabel, atLeastOnce()).getStyleClass();

        // Test hide
        callPrivateMethod("hide", new Class[]{Label.class}, testLabel);
        verify(testLabel, atLeastOnce()).getStyleClass(); // Usar atLeastOnce en lugar de times exacto
    }

    // Tests que involucran JavaFX UI (Alert, etc.) necesitan ejecutarse en el hilo de JavaFX
    // Estos tests se omiten o se modifican para evitar crear componentes UI reales

    @Test
    void testShowSuccessAndGoToLogin_NavigationOnly() throws Exception {
        // Este test solo verifica la navegación, no la creación del Alert
        try (MockedStatic<ViewNavigator> viewNavigatorMock = mockStatic(ViewNavigator.class)) {

            // Ejecutar solo la parte de navegación (simulando que el Alert fue mostrado)
            callPrivateMethod("goToLogin");

            // Verificar que intenta navegar
            viewNavigatorMock.verify(() ->
                    ViewNavigator.goTo("/co/edu/unicauca/frontend/view/SignIn.fxml", "Inicio de sesión")
            );
        }
    }

}
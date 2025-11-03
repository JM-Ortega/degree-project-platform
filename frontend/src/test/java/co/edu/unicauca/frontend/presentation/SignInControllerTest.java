package co.edu.unicauca.frontend.presentation;

import co.edu.unicauca.frontend.dto.LoginRequestDto;
import co.edu.unicauca.frontend.dto.SessionInfo;
import co.edu.unicauca.frontend.entities.enums.Rol;
import co.edu.unicauca.frontend.presentation.navigation.ViewNavigator;
import co.edu.unicauca.frontend.services.auth.AuthServiceFront;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SignInControllerTest {

    private SignInController controller;
    private AuthServiceFront authServiceMock;
    private TextField txtCorreo;
    private PasswordField txtContrasena;
    private ComboBox<String> cbRol;
    private Label errCorreo, errContrasena, errRol, errGeneral;

    @BeforeAll
    static void initJavaFX() {
        // Inicializar JavaFX para tests
        new JFXPanel();
    }

    // =========================================================
    // Helpers para usar reflexión
    // =========================================================

    private void setPrivateField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Error al asignar el campo " + fieldName, e);
        }
    }

    private Object invokePrivateMethod(Object target, String methodName, Class<?>[] paramTypes, Object... args) {
        try {
            Method method = target.getClass().getDeclaredMethod(methodName, paramTypes);
            method.setAccessible(true);
            return method.invoke(target, args);
        } catch (Exception e) {
            throw new RuntimeException("Error al invocar el método " + methodName, e);
        }
    }

    // =========================================================
    // Setup de pruebas
    // =========================================================

    @BeforeEach
    void setUp() {
        controller = new SignInController();

        // componentes de UI simulados
        txtCorreo = new TextField();
        txtContrasena = new PasswordField();
        cbRol = new ComboBox<>();
        errCorreo = new Label();
        errContrasena = new Label();
        errRol = new Label();
        errGeneral = new Label();

        cbRol.getItems().addAll("Estudiante", "Docente", "Coordinador", "JefeDeDepartamento");

        // mock del servicio
        authServiceMock = mock(AuthServiceFront.class);

        // inyectar por reflexión
        setPrivateField(controller, "txtCorreo", txtCorreo);
        setPrivateField(controller, "txtContrasena", txtContrasena);
        setPrivateField(controller, "cbRol", cbRol);
        setPrivateField(controller, "errCorreo", errCorreo);
        setPrivateField(controller, "errContrasena", errContrasena);
        setPrivateField(controller, "errRol", errRol);
        setPrivateField(controller, "errGeneral", errGeneral);
        setPrivateField(controller, "authService", authServiceMock);
    }

    // =========================================================
    // Tests
    // =========================================================

    @Test
    void testIngresar_conCamposVacios_muestraErroresLocales() {
        // Configurar campos vacíos
        txtCorreo.setText("");
        txtContrasena.setText("");
        cbRol.setValue(null);

        // Ejecutar sin mocks de JavaFX para evitar problemas de hilo
        try {
            invokePrivateMethod(controller, "ingresar", new Class[]{});
        } catch (Exception e) {
            // Ignorar errores de JavaFX en este test
        }

        // Verificar que se muestran errores de validación
        assertFalse(errCorreo.getText().isEmpty(), "Debe mostrar error en correo");
        assertFalse(errContrasena.getText().isEmpty(), "Debe mostrar error en contraseña");
        assertFalse(errRol.getText().isEmpty(), "Debe mostrar error en rol");

        // Verificar que NO se llama al servicio de autenticación
        verify(authServiceMock, never()).loginAndReturnErrors(any(LoginRequestDto.class));
    }

    @Test
    void testIngresar_conCorreoInvalido_muestraError() {
        // Configurar correo inválido
        txtCorreo.setText("correoInvalido");
        txtContrasena.setText("password123");
        cbRol.setValue("Estudiante");

        try {
            invokePrivateMethod(controller, "ingresar", new Class[]{});
        } catch (Exception e) {
            // Ignorar errores de JavaFX
        }

        assertFalse(errCorreo.getText().isEmpty(), "Debe mostrar error por correo inválido");
        verify(authServiceMock, never()).loginAndReturnErrors(any(LoginRequestDto.class));
    }

    @Test
    void testIngresar_conErrorBackend_muestraMensajes() {
        // Configurar datos válidos en frontend
        txtCorreo.setText("docente@unicauca.edu.co");
        txtContrasena.setText("password123");
        cbRol.setValue("Docente");

        // Simular error en backend
        when(authServiceMock.loginAndReturnErrors(any(LoginRequestDto.class)))
                .thenReturn(Map.of("general", "Credenciales inválidas"));

        try {
            invokePrivateMethod(controller, "ingresar", new Class[]{});
        } catch (Exception e) {
            // Ignorar errores de JavaFX
        }

        assertEquals("Credenciales inválidas", errGeneral.getText(),
                "Debe mostrar el error general del backend");

        // Verificar que se llamó al servicio
        verify(authServiceMock, times(1)).loginAndReturnErrors(any(LoginRequestDto.class));
    }

    @Test
    void testIngresar_conErrorEspecificoBackend_muestraEnCampoCorrespondiente() {
        // Configurar datos válidos
        txtCorreo.setText("estudiante@unicauca.edu.co");
        txtContrasena.setText("password123");
        cbRol.setValue("Estudiante");

        // Simular error específico de email
        when(authServiceMock.loginAndReturnErrors(any(LoginRequestDto.class)))
                .thenReturn(Map.of("email", "Usuario no encontrado"));

        try {
            invokePrivateMethod(controller, "ingresar", new Class[]{});
        } catch (Exception e) {
            // Ignorar errores de JavaFX
        }

        assertEquals("Usuario no encontrado", errCorreo.getText(),
                "Debe mostrar el error específico en el campo correo");
    }

    @Test
    void testGoToRegister_llamaViewNavigator() {
        try (MockedStatic<ViewNavigator> vnMock = Mockito.mockStatic(ViewNavigator.class)) {
            invokePrivateMethod(controller, "goToRegister", new Class[]{});

            vnMock.verify(() -> ViewNavigator.goTo(
                    "/co/edu/unicauca/frontend/view/SignUpView.fxml",
                    "Registro de usuario"), times(1));
        }
    }

    @Test
    void testMapearRol_valoresValidos() throws Exception {
        Method method = SignInController.class.getDeclaredMethod("mapearRol", String.class);
        method.setAccessible(true);

        assertEquals(Rol.ESTUDIANTE, method.invoke(controller, "Estudiante"));
        assertEquals(Rol.DOCENTE, method.invoke(controller, "Docente"));
        assertEquals(Rol.COORDINADOR, method.invoke(controller, "Coordinador"));
        assertEquals(Rol.JEFE_DE_DEPARTAMENTO, method.invoke(controller, "JefeDeDepartamento"));
    }

    @Test
    void testMapearRol_invalido_devuelveEstudiante() throws Exception {
        Method method = SignInController.class.getDeclaredMethod("mapearRol", String.class);
        method.setAccessible(true);

        Rol result = (Rol) method.invoke(controller, "RolInexistente");
        assertEquals(Rol.ESTUDIANTE, result,
                "Debe devolver Estudiante por defecto para roles inválidos");

        result = (Rol) method.invoke(controller, new Object[]{null});
        assertEquals(Rol.ESTUDIANTE, result,
                "Debe devolver Estudiante por defecto para null");
    }

    @Test
    void testTextOrEmpty_TextField() throws Exception {
        Method method = SignInController.class.getDeclaredMethod("textOrEmpty", TextField.class);
        method.setAccessible(true);

        // Test con texto normal
        txtCorreo.setText("  usuario@ejemplo.com  ");
        String result = (String) method.invoke(controller, txtCorreo);
        assertEquals("usuario@ejemplo.com", result,
                "Debe trimar espacios en blanco");

        // Test con null
        txtCorreo.setText(null);
        result = (String) method.invoke(controller, txtCorreo);
        assertEquals("", result, "Debe devolver string vacío para null");
    }

    @Test
    void testTextOrEmpty_PasswordField() throws Exception {
        Method method = SignInController.class.getDeclaredMethod("textOrEmpty", PasswordField.class);
        method.setAccessible(true);

        // Test con contraseña normal
        txtContrasena.setText("password123");
        String result = (String) method.invoke(controller, txtContrasena);
        assertEquals("password123", result);

        // Test con null
        txtContrasena.setText(null);
        result = (String) method.invoke(controller, txtContrasena);
        assertEquals("", result, "Debe devolver string vacío para null");
    }

    @Test
    void testNavigateByRole_JefeDeDepartamento() throws Exception {
        Method method = SignInController.class.getDeclaredMethod("navigateByRole", SessionInfo.class);
        method.setAccessible(true);

        try (MockedStatic<ViewNavigator> vnMock = Mockito.mockStatic(ViewNavigator.class)) {
            SessionInfo session = new SessionInfo("test@unicauca.edu.co", "Test User", Rol.JEFE_DE_DEPARTAMENTO);
            method.invoke(controller, session);

            vnMock.verify(() -> ViewNavigator.goTo(
                    "/co/edu/unicauca/frontend/view/DepartmentHead.fxml",
                    "Panel del jefe de departamento"), times(1));
        }
    }

    @Test
    void testNavigateByRole_sesionNull() throws Exception {
        Method method = SignInController.class.getDeclaredMethod("navigateByRole", SessionInfo.class);
        method.setAccessible(true);

        try (MockedStatic<ViewNavigator> vnMock = Mockito.mockStatic(ViewNavigator.class)) {
            method.invoke(controller, new Object[]{null});

            vnMock.verify(() -> ViewNavigator.goTo(
                    "/co/edu/unicauca/frontend/view/SignIn.fxml",
                    "Inicio de sesión"), times(1));
        }
    }

    @Test
    void testNavigateByRole_rolNull() throws Exception {
        Method method = SignInController.class.getDeclaredMethod("navigateByRole", SessionInfo.class);
        method.setAccessible(true);

        try (MockedStatic<ViewNavigator> vnMock = Mockito.mockStatic(ViewNavigator.class)) {
            SessionInfo session = new SessionInfo("test@unicauca.edu.co", "Test User", null);
            method.invoke(controller, session);

            vnMock.verify(() -> ViewNavigator.goTo(
                    "/co/edu/unicauca/frontend/view/SignIn.fxml",
                    "Inicio de sesión"), times(1));
        }
    }

    @Test
    void testClearAllErrors() throws Exception {
        // Configurar errores previos
        errCorreo.setText("Error previo");
        errContrasena.setText("Error previo");
        errRol.setText("Error previo");
        errGeneral.setText("Error previo");

        Method method = SignInController.class.getDeclaredMethod("clearAllErrors");
        method.setAccessible(true);
        method.invoke(controller);

        assertEquals(" ", errCorreo.getText(), "Debe limpiar error de correo");
        assertEquals(" ", errContrasena.getText(), "Debe limpiar error de contraseña");
        assertEquals(" ", errRol.getText(), "Debe limpiar error de rol");
        assertEquals(" ", errGeneral.getText(), "Debe limpiar error general");
    }

    @Test
    void testShowError() throws Exception {
        Method method = SignInController.class.getDeclaredMethod("showError", Label.class, String.class);
        method.setAccessible(true);

        Label testLabel = new Label();
        method.invoke(controller, testLabel, "Mensaje de error");

        assertEquals("Mensaje de error", testLabel.getText());
        assertTrue(testLabel.getStyle().contains("-fx-text-fill: red;"));
    }

    @Test
    void testClearError() throws Exception {
        Method method = SignInController.class.getDeclaredMethod("clearError", Label.class);
        method.setAccessible(true);

        Label testLabel = new Label();
        testLabel.setText("Error previo");
        testLabel.setStyle("-fx-text-fill: red;");

        method.invoke(controller, testLabel);

        assertEquals(" ", testLabel.getText());
    }
}
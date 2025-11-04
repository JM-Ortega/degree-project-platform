package co.edu.unicauca.authservice.presentation;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    public GlobalExceptionHandlerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHandleIllegalArgument() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid argument");
        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleIllegalArgument(ex);
        assertEquals("Invalid argument", response.getBody().get("error"));
    }

    @Test
    void testHandleValidation() {
        // Crea un objeto BindingResult válido
        BindException bindingResult = new BindException(new Object(), "obj");

        // Crea el error de validación
        FieldError fieldError = new FieldError("obj", "field", "Field is required");

        // Agrega el error al BindingResult
        bindingResult.addError(fieldError);

        // Crea la excepción con el BindingResult
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        // Ejecuta el método que maneja la validación
        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleValidation(ex);

        // Verifica que el error en el cuerpo de la respuesta sea el esperado
        assertEquals("Field is required", response.getBody().get("field"));
    }

    @Test
    void testHandleUnexpected() {
        Exception ex = new Exception("Unexpected error");
        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleUnexpected(ex);
        assertEquals("Ocurrió un error inesperado. Inténtelo más tarde.", response.getBody().get("error"));
    }
}

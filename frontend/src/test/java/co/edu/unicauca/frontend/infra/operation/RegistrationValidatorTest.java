package co.edu.unicauca.frontend.infra.operation;

import co.edu.unicauca.frontend.dto.RegistroPersonaDto;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias para la clase RegistrationValidator.
 * Verifica que las validaciones de datos de registro funcionen correctamente.
 */
class RegistrationValidatorTest {

    @Test
    void testValidate_WithValidData_ShouldReturnNoErrors() {
        // Preparar
        RegistroPersonaDto dto = new RegistroPersonaDto();
        dto.setNombres("Juan Carlos");
        dto.setApellidos("Pérez López");
        dto.setEmail("juan.perez@unicauca.edu.co");
        dto.setPassword("Password123!");
        dto.setPrograma("Ingeniería de Sistemas");
        dto.setRoles(List.of("Estudiante"));
        dto.setCelular("3124567890");
        dto.setDepartamento("");

        // Ejecutar
        Map<String, String> errores = RegistrationValidator.validate(dto);

        // Verificar
        assertTrue(errores.isEmpty(), "No debería haber errores con datos válidos");
    }

    @Test
    void testValidate_WithEmptyNames_ShouldReturnErrors() {
        // Preparar
        RegistroPersonaDto dto = new RegistroPersonaDto();
        dto.setNombres("");
        dto.setApellidos("  ");
        dto.setEmail("test@unicauca.edu.co");
        dto.setPassword("Pass123!");
        dto.setPrograma("Ingeniería Civil");
        dto.setRoles(List.of("Estudiante"));
        dto.setCelular("3124567890");
        dto.setDepartamento("");

        // Ejecutar
        Map<String, String> errores = RegistrationValidator.validate(dto);

        // Verificar
        assertEquals(2, errores.size());
        assertEquals("El nombre es obligatorio", errores.get("nombres"));
        assertEquals("El apellido es obligatorio", errores.get("apellidos"));
    }

    @Test
    void testValidate_WithInvalidEmail_ShouldReturnError() {
        // Preparar
        RegistroPersonaDto dto = new RegistroPersonaDto();
        dto.setNombres("María");
        dto.setApellidos("García");
        dto.setEmail("maria@gmail.com"); // Email no institucional
        dto.setPassword("Pass123!");
        dto.setPrograma("Medicina");
        dto.setRoles(List.of("Estudiante"));
        dto.setCelular("3124567890");
        dto.setDepartamento("");

        // Ejecutar
        Map<String, String> errores = RegistrationValidator.validate(dto);

        // Verificar
        assertEquals(1, errores.size());
        assertEquals("El correo debe pertenecer al dominio @unicauca.edu.co", errores.get("email"));
    }

    @Test
    void testValidate_WithEmptyEmail_ShouldReturnError() {
        // Preparar
        RegistroPersonaDto dto = new RegistroPersonaDto();
        dto.setNombres("Ana");
        dto.setApellidos("Martínez");
        dto.setEmail(null);
        dto.setPassword("Pass123!");
        dto.setPrograma("Derecho");
        dto.setRoles(List.of("Estudiante"));
        dto.setCelular("3124567890");
        dto.setDepartamento("");

        // Ejecutar
        Map<String, String> errores = RegistrationValidator.validate(dto);

        // Verificar
        assertEquals(1, errores.size());
        assertEquals("El correo electrónico es obligatorio", errores.get("email"));
    }

    @Test
    void testValidate_WithWeakPassword_ShouldReturnError() {
        // Preparar
        RegistroPersonaDto dto = new RegistroPersonaDto();
        dto.setNombres("Carlos");
        dto.setApellidos("Rodríguez");
        dto.setEmail("carlos@unicauca.edu.co");
        dto.setPassword("weak"); // Contraseña débil
        dto.setPrograma("Administración");
        dto.setRoles(List.of("Estudiante"));
        dto.setCelular("3124567890");
        dto.setDepartamento("");

        // Ejecutar
        Map<String, String> errores = RegistrationValidator.validate(dto);

        // Verificar
        assertEquals(1, errores.size());
        assertEquals("La contraseña no cumple con los requisitos de seguridad", errores.get("password"));
    }

    @Test
    void testValidate_WithEmptyPassword_ShouldReturnError() {
        // Preparar
        RegistroPersonaDto dto = new RegistroPersonaDto();
        dto.setNombres("Laura");
        dto.setApellidos("Hernández");
        dto.setEmail("laura@unicauca.edu.co");
        dto.setPassword("");
        dto.setPrograma("Psicología");
        dto.setRoles(List.of("Estudiante"));
        dto.setCelular("3124567890");
        dto.setDepartamento("");

        // Ejecutar
        Map<String, String> errores = RegistrationValidator.validate(dto);

        // Verificar
        assertEquals(1, errores.size());
        assertEquals("La contraseña es obligatoria", errores.get("password"));
    }

    @Test
    void testValidate_WithInvalidPhone_ShouldReturnError() {
        // Preparar
        RegistroPersonaDto dto = new RegistroPersonaDto();
        dto.setNombres("Pedro");
        dto.setApellidos("Gómez");
        dto.setEmail("pedro@unicauca.edu.co");
        dto.setPassword("Pass123!");
        dto.setPrograma("Economía");
        dto.setRoles(List.of("Estudiante"));
        dto.setCelular("12345"); // Teléfono inválido
        dto.setDepartamento("");

        // Ejecutar
        Map<String, String> errores = RegistrationValidator.validate(dto);

        // Verificar
        assertEquals(1, errores.size());
        assertEquals("El número de celular debe tener exactamente 10 dígitos", errores.get("celular"));
    }

    @Test
    void testValidate_WithEmptyProgram_ShouldReturnError() {
        // Preparar
        RegistroPersonaDto dto = new RegistroPersonaDto();
        dto.setNombres("Sofía");
        dto.setApellidos("López");
        dto.setEmail("sofia@unicauca.edu.co");
        dto.setPassword("Pass123!");
        dto.setPrograma(null);
        dto.setRoles(List.of("Estudiante"));
        dto.setCelular("3124567890");
        dto.setDepartamento("");

        // Ejecutar
        Map<String, String> errores = RegistrationValidator.validate(dto);

        // Verificar
        assertEquals(1, errores.size());
        assertEquals("El programa académico es obligatorio", errores.get("programa"));
    }

    @Test
    void testValidate_WithNoRoles_ShouldReturnError() {
        // Preparar
        RegistroPersonaDto dto = new RegistroPersonaDto();
        dto.setNombres("Diego");
        dto.setApellidos("Ramírez");
        dto.setEmail("diego@unicauca.edu.co");
        dto.setPassword("Pass123!");
        dto.setPrograma("Arquitectura");
        dto.setRoles(List.of()); // Lista vacía de roles
        dto.setCelular("3124567890");
        dto.setDepartamento("");

        // Ejecutar
        Map<String, String> errores = RegistrationValidator.validate(dto);

        // Verificar
        assertEquals(1, errores.size());
        assertEquals("Debe seleccionar al menos un rol", errores.get("roles"));
    }

    @Test
    void testValidate_WithInvalidRole_ShouldReturnError() {
        // Preparar
        RegistroPersonaDto dto = new RegistroPersonaDto();
        dto.setNombres("Elena");
        dto.setApellidos("Castro");
        dto.setEmail("elena@unicauca.edu.co");
        dto.setPassword("Pass123!");
        dto.setPrograma("Biología");
        dto.setRoles(List.of("Administrador")); // Rol no permitido
        dto.setCelular("3124567890");
        dto.setDepartamento("");

        // Ejecutar
        Map<String, String> errores = RegistrationValidator.validate(dto);

        // Verificar
        assertEquals(1, errores.size());
        assertEquals("No puede registrarse con ese rol. Use Estudiante o Docente.", errores.get("roles"));
    }

    @Test
    void testValidate_WithTeacherRoleAndNoDepartment_ShouldReturnError() {
        // Preparar
        RegistroPersonaDto dto = new RegistroPersonaDto();
        dto.setNombres("Profesor");
        dto.setApellidos("Ejemplo");
        dto.setEmail("profesor@unicauca.edu.co");
        dto.setPassword("Pass123!");
        dto.setPrograma("Ingeniería");
        dto.setRoles(List.of("Docente")); // Rol docente sin departamento
        dto.setCelular("3124567890");
        dto.setDepartamento("");

        // Ejecutar
        Map<String, String> errores = RegistrationValidator.validate(dto);

        // Verificar
        assertEquals(1, errores.size());
        assertEquals("El departamento es obligatorio para docentes y jefes de departamento", errores.get("departamento"));
    }

    @Test
    void testValidate_WithTeacherRoleAndDepartment_ShouldReturnNoErrors() {
        // Preparar
        RegistroPersonaDto dto = new RegistroPersonaDto();
        dto.setNombres("Profesor");
        dto.setApellidos("Ejemplo");
        dto.setEmail("profesor@unicauca.edu.co");
        dto.setPassword("Pass123!");
        dto.setPrograma("Ingeniería");
        dto.setRoles(List.of("Docente"));
        dto.setCelular("3124567890");
        dto.setDepartamento("Ingeniería de Sistemas");

        // Ejecutar
        Map<String, String> errores = RegistrationValidator.validate(dto);

        // Verificar
        assertTrue(errores.isEmpty(), "No debería haber errores con docente y departamento válido");
    }

    @Test
    void testValidate_WithStudentRoleAndNoDepartment_ShouldReturnNoErrors() {
        // Preparar
        RegistroPersonaDto dto = new RegistroPersonaDto();
        dto.setNombres("Estudiante");
        dto.setApellidos("Ejemplo");
        dto.setEmail("estudiante@unicauca.edu.co");
        dto.setPassword("Pass123!");
        dto.setPrograma("Ingeniería de Sistemas");
        dto.setRoles(List.of("Estudiante"));
        dto.setCelular("3124567890");
        dto.setDepartamento("");

        // Ejecutar
        Map<String, String> errores = RegistrationValidator.validate(dto);

        // Verificar
        assertTrue(errores.isEmpty(), "No debería haber errores con estudiante sin departamento");
    }

    @Test
    void testValidate_WithMultipleValidRoles_ShouldReturnNoErrors() {
        // Preparar
        RegistroPersonaDto dto = new RegistroPersonaDto();
        dto.setNombres("Usuario");
        dto.setApellidos("Múltiple");
        dto.setEmail("usuario@unicauca.edu.co");
        dto.setPassword("Pass123!");
        dto.setPrograma("Ingeniería");
        dto.setRoles(List.of("Estudiante", "Docente"));
        dto.setCelular("3124567890");
        dto.setDepartamento("Ingeniería de Sistemas");

        // Ejecutar
        Map<String, String> errores = RegistrationValidator.validate(dto);

        // Verificar
        assertTrue(errores.isEmpty(), "No debería haber errores con múltiples roles válidos");
    }

    @Test
    void testValidate_WithNullCelular_ShouldReturnNoErrors() {
        // Preparar
        RegistroPersonaDto dto = new RegistroPersonaDto();
        dto.setNombres("Usuario");
        dto.setApellidos("Sin Teléfono");
        dto.setEmail("usuario@unicauca.edu.co");
        dto.setPassword("Pass123!");
        dto.setPrograma("Ingeniería");
        dto.setRoles(List.of("Estudiante"));
        dto.setCelular(null); // Celular nulo (opcional)
        dto.setDepartamento("");

        // Ejecutar
        Map<String, String> errores = RegistrationValidator.validate(dto);

        // Verificar
        assertTrue(errores.isEmpty(), "No debería haber errores con celular nulo");
    }

    @Test
    void testValidate_WithEmptyCelular_ShouldReturnNoErrors() {
        // Preparar
        RegistroPersonaDto dto = new RegistroPersonaDto();
        dto.setNombres("Usuario");
        dto.setApellidos("Sin Teléfono");
        dto.setEmail("usuario@unicauca.edu.co");
        dto.setPassword("Pass123!");
        dto.setPrograma("Ingeniería");
        dto.setRoles(List.of("Estudiante"));
        dto.setCelular(""); // Celular vacío (opcional)
        dto.setDepartamento("");

        // Ejecutar
        Map<String, String> errores = RegistrationValidator.validate(dto);

        // Verificar
        assertTrue(errores.isEmpty(), "No debería haber errores con celular vacío");
    }

    @Test
    void testValidate_WithAllFieldsInvalid_ShouldReturnMultipleErrors() {
        // Preparar
        RegistroPersonaDto dto = new RegistroPersonaDto();
        dto.setNombres("");
        dto.setApellidos("");
        dto.setEmail("invalid-email");
        dto.setPassword("weak");
        dto.setPrograma("");
        dto.setRoles(List.of());
        dto.setCelular("123");
        dto.setDepartamento("");

        // Ejecutar
        Map<String, String> errores = RegistrationValidator.validate(dto);

        // Verificar
        assertEquals(7, errores.size());
        assertTrue(errores.containsKey("nombres"));
        assertTrue(errores.containsKey("apellidos"));
        assertTrue(errores.containsKey("email"));
        assertTrue(errores.containsKey("password"));
        assertTrue(errores.containsKey("programa"));
        assertTrue(errores.containsKey("roles"));
        assertTrue(errores.containsKey("celular"));
    }

    @Test
    void testValidate_WithValidEmailVariations_ShouldReturnNoErrors() {
        // Preparar - diferentes formatos válidos de email unicauca
        String[] emailsValidos = {
            "usuario@unicauca.edu.co",
            "usuario.nombre@unicauca.edu.co",
            "usuario123@unicauca.edu.co",
            "u.s.e.r@unicauca.edu.co"
        };

        for (String email : emailsValidos) {
            RegistroPersonaDto dto = new RegistroPersonaDto();
            dto.setNombres("Test");
            dto.setApellidos("User");
            dto.setEmail(email);
            dto.setPassword("Pass123!");
            dto.setPrograma("Test Program");
            dto.setRoles(List.of("Estudiante"));
            dto.setCelular("3124567890");
            dto.setDepartamento("");

            // Ejecutar
            Map<String, String> errores = RegistrationValidator.validate(dto);

            // Verificar
            assertTrue(errores.isEmpty(), "Debería aceptar el email: " + email);
        }
    }

    @Test
    void testValidate_WithValidPasswordVariations_ShouldReturnNoErrors() {
        // Preparar - diferentes formatos válidos de contraseña
        String[] passwordsValidos = {
            "A1@bcde",
            "PASSWORD1!",
            "MiClave123#",
            "Test@2024"
        };

        for (String password : passwordsValidos) {
            RegistroPersonaDto dto = new RegistroPersonaDto();
            dto.setNombres("Test");
            dto.setApellidos("User");
            dto.setEmail("test@unicauca.edu.co");
            dto.setPassword(password);
            dto.setPrograma("Test Program");
            dto.setRoles(List.of("Estudiante"));
            dto.setCelular("3124567890");
            dto.setDepartamento("");

            // Ejecutar
            Map<String, String> errores = RegistrationValidator.validate(dto);

            // Verificar
            assertTrue(errores.isEmpty(), "Debería aceptar la contraseña: " + password);
        }
    }
}
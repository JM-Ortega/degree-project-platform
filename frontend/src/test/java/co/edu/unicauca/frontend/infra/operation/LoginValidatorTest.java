package co.edu.unicauca.frontend.infra.operation;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la clase LoginValidator.
 * Verifica que las validaciones de inicio de sesión funcionen correctamente.
 */
class LoginValidatorTest {

    @Test
    void testValidate_WithValidData_ShouldReturnNoErrors() {
        // Preparar
        String email = "usuario@unicauca.edu.co";
        String password = "miContraseña123";
        String rol = "Estudiante";

        // Ejecutar
        Map<String, String> errores = LoginValidator.validate(email, password, rol);

        // Verificar
        assertTrue(errores.isEmpty(), "No debería haber errores con datos válidos");
    }

    @Test
    void testValidate_WithEmptyEmail_ShouldReturnError() {
        // Preparar
        String email = "";
        String password = "miContraseña123";
        String rol = "Docente";

        // Ejecutar
        Map<String, String> errores = LoginValidator.validate(email, password, rol);

        // Verificar
        assertEquals(1, errores.size());
        assertEquals("El correo electrónico es obligatorio.", errores.get("email"));
    }

    @Test
    void testValidate_WithNullEmail_ShouldReturnError() {
        // Preparar
        String email = null;
        String password = "miContraseña123";
        String rol = "Estudiante";

        // Ejecutar
        Map<String, String> errores = LoginValidator.validate(email, password, rol);

        // Verificar
        assertEquals(1, errores.size());
        assertEquals("El correo electrónico es obligatorio.", errores.get("email"));
    }

    @Test
    void testValidate_WithInvalidEmailDomain_ShouldReturnError() {
        // Preparar
        String email = "usuario@gmail.com";
        String password = "miContraseña123";
        String rol = "Coordinador";

        // Ejecutar
        Map<String, String> errores = LoginValidator.validate(email, password, rol);

        // Verificar
        assertEquals(1, errores.size());
        assertEquals("El correo debe pertenecer al dominio @unicauca.edu.co.", errores.get("email"));
    }

    @Test
    void testValidate_WithEmailWithSpaces_ShouldBeTrimmedAndValid() {
        // Preparar
        String email = "  usuario@unicauca.edu.co  ";
        String password = "miContraseña123";
        String rol = "Estudiante";

        // Ejecutar
        Map<String, String> errores = LoginValidator.validate(email, password, rol);

        // Verificar
        assertTrue(errores.isEmpty(), "Debería aceptar emails con espacios que se pueden recortar");
    }

    @Test
    void testValidate_WithEmptyPassword_ShouldReturnError() {
        // Preparar
        String email = "usuario@unicauca.edu.co";
        String password = "";
        String rol = "JefeDeDepartamento";

        // Ejecutar
        Map<String, String> errores = LoginValidator.validate(email, password, rol);

        // Verificar
        assertEquals(1, errores.size());
        assertEquals("La contraseña es obligatoria.", errores.get("password"));
    }

    @Test
    void testValidate_WithNullPassword_ShouldReturnError() {
        // Preparar
        String email = "usuario@unicauca.edu.co";
        String password = null;
        String rol = "Estudiante";

        // Ejecutar
        Map<String, String> errores = LoginValidator.validate(email, password, rol);

        // Verificar
        assertEquals(1, errores.size());
        assertEquals("La contraseña es obligatoria.", errores.get("password"));
    }

    @Test
    void testValidate_WithPasswordWithSpaces_ShouldBeValid() {
        // Preparar
        String email = "usuario@unicauca.edu.co";
        String password = "  contraseñaConEspacios  ";
        String rol = "Docente";

        // Ejecutar
        Map<String, String> errores = LoginValidator.validate(email, password, rol);

        // Verificar
        assertTrue(errores.isEmpty(), "Debería aceptar contraseñas con espacios");
    }

    @Test
    void testValidate_WithEmptyRole_ShouldReturnError() {
        // Preparar
        String email = "usuario@unicauca.edu.co";
        String password = "miContraseña123";
        String rol = "";

        // Ejecutar
        Map<String, String> errores = LoginValidator.validate(email, password, rol);

        // Verificar
        assertEquals(1, errores.size());
        assertEquals("Debe seleccionar el rol con el que desea ingresar.", errores.get("rol"));
    }

    @Test
    void testValidate_WithNullRole_ShouldReturnError() {
        // Preparar
        String email = "usuario@unicauca.edu.co";
        String password = "miContraseña123";
        String rol = null;

        // Ejecutar
        Map<String, String> errores = LoginValidator.validate(email, password, rol);

        // Verificar
        assertEquals(1, errores.size());
        assertEquals("Debe seleccionar el rol con el que desea ingresar.", errores.get("rol"));
    }

    @Test
    void testValidate_WithRoleWithSpaces_ShouldBeValid() {
        // Preparar
        String email = "usuario@unicauca.edu.co";
        String password = "miContraseña123";
        String rol = "  Estudiante  ";

        // Ejecutar
        Map<String, String> errores = LoginValidator.validate(email, password, rol);

        // Verificar
        assertTrue(errores.isEmpty(), "Debería aceptar roles con espacios que se pueden recortar");
    }

    @Test
    void testValidate_WithAllFieldsEmpty_ShouldReturnMultipleErrors() {
        // Preparar
        String email = "";
        String password = "";
        String rol = "";

        // Ejecutar
        Map<String, String> errores = LoginValidator.validate(email, password, rol);

        // Verificar
        assertEquals(3, errores.size());
        assertEquals("El correo electrónico es obligatorio.", errores.get("email"));
        assertEquals("La contraseña es obligatoria.", errores.get("password"));
        assertEquals("Debe seleccionar el rol con el que desea ingresar.", errores.get("rol"));
    }

    @Test
    void testValidate_WithAllFieldsNull_ShouldReturnMultipleErrors() {
        // Preparar
        String email = null;
        String password = null;
        String rol = null;

        // Ejecutar
        Map<String, String> errores = LoginValidator.validate(email, password, rol);

        // Verificar
        assertEquals(3, errores.size());
        assertEquals("El correo electrónico es obligatorio.", errores.get("email"));
        assertEquals("La contraseña es obligatoria.", errores.get("password"));
        assertEquals("Debe seleccionar el rol con el que desea ingresar.", errores.get("rol"));
    }

    @Test
    void testValidate_WithValidEmailVariations_ShouldReturnNoErrors() {
        // Preparar - diferentes formatos válidos de email unicauca
        String[] emailsValidos = {
                "usuario@unicauca.edu.co",
                "usuario.nombre@unicauca.edu.co",
                "usuario123@unicauca.edu.co",
                "u.s.e.r@unicauca.edu.co",
                "USUARIO@UNICAUCA.EDU.CO", // mayúsculas
                "Usuario@Unicauca.Edu.Co"  // mezcla de mayúsculas y minúsculas
        };

        for (String email : emailsValidos) {
            String password = "miContraseña123";
            String rol = "Estudiante";

            // Ejecutar
            Map<String, String> errores = LoginValidator.validate(email, password, rol);

            // Verificar
            assertTrue(errores.isEmpty(), "Debería aceptar el email: " + email);
        }
    }

    @Test
    void testValidate_WithInvalidEmailVariations_ShouldReturnErrors() {
        // Preparar - diferentes formatos inválidos de email
        String[] emailsInvalidos = {
                "usuario@gmail.com",
                "usuario@hotmail.com",
                "usuario@unicauca.com",        // dominio incorrecto
                "usuario@edu.co",              // dominio incorrecto
                "usuario@unicauca.edu",        // falta .co
                "usuariounicauca.edu.co",      // falta @
                "@unicauca.edu.co",            // falta usuario
                "usuario@",                    // falta dominio
                "usuario@unicauca.educo",      // falta punto
                "usuario@unicauca..edu.co",    // doble punto
                "usuario@unicauca.edu.co.",    // punto al final
                "usuario@-unicauca.edu.co",    // guión al inicio del dominio
        };

        for (String email : emailsInvalidos) {
            String password = "miContraseña123";
            String rol = "Estudiante";

            // Ejecutar
            Map<String, String> errores = LoginValidator.validate(email, password, rol);

            // Verificar
            assertEquals(1, errores.size(), "Debería rechazar el email: " + email);
            assertEquals("El correo debe pertenecer al dominio @unicauca.edu.co.", errores.get("email"));
        }
    }

    @Test
    void testValidate_WithEmailsWithSpaces_ShouldBeTrimmedAndValid() {
        // Preparar - emails con espacios que después del trim son válidos
        String[] emailsConEspaciosValidos = {
                "  usuario@unicauca.edu.co  ",
                " usuario@unicauca.edu.co ",
                "\tusuario@unicauca.edu.co\t", // tabs
                "\nusuario@unicauca.edu.co\n"  // newlines
        };

        for (String email : emailsConEspaciosValidos) {
            String password = "miContraseña123";
            String rol = "Estudiante";

            // Ejecutar
            Map<String, String> errores = LoginValidator.validate(email, password, rol);

            // Verificar
            assertTrue(errores.isEmpty(), "Debería aceptar el email con espacios después del trim: " + email);
        }
    }

    @Test
    void testValidate_WithDifferentValidRoles_ShouldReturnNoErrors() {
        // Preparar - diferentes roles válidos (cualquier texto no vacío es aceptado)
        String[] rolesValidos = {
                "Estudiante",
                "Docente",
                "Coordinador",
                "JefeDeDepartamento",
                "Administrador",
                "Invitado",
                "CualquierRol" // El validador no restringe los roles específicos
        };

        for (String rol : rolesValidos) {
            String email = "usuario@unicauca.edu.co";
            String password = "miContraseña123";

            // Ejecutar
            Map<String, String> errores = LoginValidator.validate(email, password, rol);

            // Verificar
            assertTrue(errores.isEmpty(), "Debería aceptar el rol: " + rol);
        }
    }

    @Test
    void testValidate_OrderOfErrors_ShouldBeEmailPasswordRole() {
        // Preparar
        String email = "";
        String password = "";
        String rol = "";

        // Ejecutar
        Map<String, String> errores = LoginValidator.validate(email, password, rol);

        // Verificar que el orden de los errores sea el esperado
        String[] clavesEsperadas = {"email", "password", "rol"};
        int index = 0;

        for (String clave : errores.keySet()) {
            assertEquals(clavesEsperadas[index], clave, "El orden de los errores debería ser email, password, rol");
            index++;
        }
    }

    @Test
    void testValidate_WithOnlyEmailInvalid_ShouldReturnOnlyEmailError() {
        // Preparar
        String email = "invalid@email.com";
        String password = "passwordValida";
        String rol = "RolValido";

        // Ejecutar
        Map<String, String> errores = LoginValidator.validate(email, password, rol);

        // Verificar
        assertEquals(1, errores.size());
        assertTrue(errores.containsKey("email"));
        assertFalse(errores.containsKey("password"));
        assertFalse(errores.containsKey("rol"));
    }

    @Test
    void testValidate_WithOnlyPasswordInvalid_ShouldReturnOnlyPasswordError() {
        // Preparar
        String email = "usuario@unicauca.edu.co";
        String password = "";
        String rol = "RolValido";

        // Ejecutar
        Map<String, String> errores = LoginValidator.validate(email, password, rol);

        // Verificar
        assertEquals(1, errores.size());
        assertFalse(errores.containsKey("email"));
        assertTrue(errores.containsKey("password"));
        assertFalse(errores.containsKey("rol"));
    }

    @Test
    void testValidate_WithOnlyRoleInvalid_ShouldReturnOnlyRoleError() {
        // Preparar
        String email = "usuario@unicauca.edu.co";
        String password = "passwordValida";
        String rol = "";

        // Ejecutar
        Map<String, String> errores = LoginValidator.validate(email, password, rol);

        // Verificar
        assertEquals(1, errores.size());
        assertFalse(errores.containsKey("email"));
        assertFalse(errores.containsKey("password"));
        assertTrue(errores.containsKey("rol"));
    }
}
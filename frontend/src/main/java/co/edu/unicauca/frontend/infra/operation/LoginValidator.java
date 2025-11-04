package co.edu.unicauca.frontend.infra.operation;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Validador básico para el formulario de inicio de sesión.
 *
 * Solo comprueba presencia de correo, contraseña y rol,
 * y que el correo tenga el dominio institucional.
 */
public final class LoginValidator {

    private LoginValidator() { }

    private static final Pattern EMAIL_UNICAUCA =
            Pattern.compile("^[A-Za-z0-9._%+-]+@unicauca\\.edu\\.co$", Pattern.CASE_INSENSITIVE);

    /**
     * Valida los campos mínimos requeridos para iniciar sesión.
     *
     * @param email    correo institucional.
     * @param password contraseña digitada.
     * @param rol      rol seleccionado en el combo.
     * @return mapa de errores: key = nombreCampo (email, password, rol), value = mensaje.
     */
    public static Map<String, String> validate(String email, String password, String rol) {
        Map<String, String> errors = new LinkedHashMap<>();

        // email
        if (email == null || email.isBlank()) {
            errors.put("email", "El correo electrónico es obligatorio.");
        } else if (!EMAIL_UNICAUCA.matcher(email.trim()).matches()) {
            errors.put("email", "El correo debe pertenecer al dominio @unicauca.edu.co.");
        }

        // password
        if (password == null || password.isBlank()) {
            errors.put("password", "La contraseña es obligatoria.");
        }

        // rol
        if (rol == null || rol.isBlank()) {
            errors.put("rol", "Debe seleccionar el rol con el que desea ingresar.");
        }

        return errors;
    }
}

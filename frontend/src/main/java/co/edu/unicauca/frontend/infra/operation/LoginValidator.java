package co.edu.unicauca.frontend.infra.operation;

import co.edu.unicauca.frontend.infra.validation.ValidationContext;
import co.edu.unicauca.frontend.infra.validation.Validator;
import co.edu.unicauca.frontend.infra.validation.handlers.EmailUnicaucaValidator;
import co.edu.unicauca.frontend.infra.validation.handlers.NotBlankValidator;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Validador para el formulario de inicio de sesión basado en Chain of Responsibility.
 */
public final class LoginValidator {

    private LoginValidator() { }

    public static Map<String, String> validate(String email, String password, String rol) {
        Map<String, String> errors = new LinkedHashMap<>();
        var ctx = new ValidationContext(email, password, rol);

        // Construcción de la cadena
        Validator chain = new EmailUnicaucaValidator(ctx);
        chain.linkWith(new NotBlankValidator(password, "password", "La contraseña es obligatoria."))
             .linkWith(new NotBlankValidator(rol, "rol", "Debe seleccionar el rol con el que desea ingresar."));

        // Ejecución
        chain.validate(errors);
        return errors;
    }
}

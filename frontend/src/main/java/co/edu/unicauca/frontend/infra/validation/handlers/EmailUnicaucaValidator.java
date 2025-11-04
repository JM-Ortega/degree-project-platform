package co.edu.unicauca.frontend.infra.validation.handlers;

import co.edu.unicauca.frontend.infra.validation.ValidationContext;
import co.edu.unicauca.frontend.infra.validation.Validator;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * Valida que el correo sea institucional y no esté vacío.
 */
public class EmailUnicaucaValidator extends Validator {
    private static final Pattern EMAIL =
            Pattern.compile("^[A-Za-z0-9._%+-]+@unicauca\\.edu\\.co$", Pattern.CASE_INSENSITIVE);
    private final ValidationContext ctx;

    public EmailUnicaucaValidator(ValidationContext ctx) {
        this.ctx = ctx;
    }

    @Override
    protected void check(Map<String, String> errors) {
        String email = ctx.email();
        if (email == null || email.isBlank()) {
            errors.put("email", "El correo electrónico es obligatorio.");
        } else if (!EMAIL.matcher(email.trim()).matches()) {
            errors.put("email", "El correo debe pertenecer al dominio @unicauca.edu.co.");
        }
    }
}

package co.edu.unicauca.frontend.infra.validation.handlers;

import co.edu.unicauca.frontend.infra.validation.Validator;

import java.util.Map;

/**
 * Valida que un campo no esté vacío.
 */
public class NotBlankValidator extends Validator {
    private final String value;
    private final String fieldName;
    private final String message;

    public NotBlankValidator(String value, String fieldName, String message) {
        this.value = value;
        this.fieldName = fieldName;
        this.message = message;
    }

    @Override
    protected void check(Map<String, String> errors) {
        if (value == null || value.trim().isEmpty()) {
            errors.put(fieldName, message);
        }
    }
}

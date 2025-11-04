package co.edu.unicauca.frontend.infra.validation;

import java.util.Map;

/**
 * Clase base del patrón Chain of Responsibility para validadores.
 * Cada validador ejecuta su verificación y delega al siguiente en la cadena.
 */
public abstract class Validator {
    private Validator next;

    public Validator linkWith(Validator next) {
        this.next = next;
        return next;
    }

    /** Ejecuta la validación y pasa al siguiente validador si existe. */
    public final void validate(Map<String, String> errors) {
        check(errors);
        if (next != null) {
            next.validate(errors);
        }
    }

    /** Implementación concreta de cada validación. */
    protected abstract void check(Map<String, String> errors);
}

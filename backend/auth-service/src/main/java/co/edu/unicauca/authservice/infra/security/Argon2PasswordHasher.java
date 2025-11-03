package co.edu.unicauca.authservice.infra.security;

import co.edu.unicauca.authservice.services.PasswordHasher;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import de.mkammerer.argon2.Argon2Factory.Argon2Types;
import org.springframework.stereotype.Component;

/**
 * Implementación de {@link PasswordHasher} usando Argon2id.
 * Parámetros pensados para entorno académico (seguro pero no exagerado).
 */
@Component
public class Argon2PasswordHasher implements PasswordHasher {

    // Puedes ajustar estos valores si la máquina está muy lenta
    private static final int ITERATIONS = 3;     // t
    private static final int MEMORY_KB = 65536;  // m (64 MB)
    private static final int PARALLELISM = 1;    // p

    @Override
    public String hash(char[] rawPassword) {
        if (rawPassword == null || rawPassword.length == 0) {
            throw new IllegalArgumentException("La contraseña no puede ser null o vacía");
        }

        Argon2 argon2 = Argon2Factory.create(Argon2Types.ARGON2id);
        try {
            return argon2.hash(ITERATIONS, MEMORY_KB, PARALLELISM, rawPassword);
        } finally {
            argon2.wipeArray(rawPassword);
        }
    }

    @Override
    public boolean verify(char[] plainPassword, String hashedPassword) {
        if (plainPassword == null || plainPassword.length == 0) return false;
        if (hashedPassword == null || hashedPassword.isBlank()) return false;

        Argon2 argon2 = Argon2Factory.create(Argon2Types.ARGON2id);
        try {
            return argon2.verify(hashedPassword, plainPassword);
        } catch (RuntimeException e) {
            return false;
        } finally {
            argon2.wipeArray(plainPassword);
        }
    }
}

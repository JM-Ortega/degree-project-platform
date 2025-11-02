package co.edu.unicauca.authservice.infra.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Argon2PasswordHasherTest {

    private final Argon2PasswordHasher hasher = new Argon2PasswordHasher();

    @Test
    void hashDebeGenerarHashValido() {
        String hash = hasher.hash("ClaveSegura123*".toCharArray());
        assertNotNull(hash);
        assertTrue(hash.startsWith("$argon2id$"), "El hash debe comenzar con el prefijo Argon2id");
        assertTrue(hash.length() > 20, "El hash generado debe tener una longitud razonable");
    }

    @Test
    void verifyDebeRetornarTrueConPasswordCorrecta() {
        char[] password = "ClaveSegura123*".toCharArray();
        String hash = hasher.hash(password);

        boolean valido = hasher.verify("ClaveSegura123*".toCharArray(), hash);
        assertTrue(valido, "La verificación debe ser verdadera para la contraseña correcta");
    }

    @Test
    void verifyDebeRetornarFalseConPasswordIncorrecta() {
        char[] password = "ClaveSegura123*".toCharArray();
        String hash = hasher.hash(password);

        boolean valido = hasher.verify("OtraClave!".toCharArray(), hash);
        assertFalse(valido, "La verificación debe fallar con contraseña incorrecta");
    }

    @Test
    void hashDebeFallarConPasswordVacia() {
        assertThrows(IllegalArgumentException.class, () -> hasher.hash("".toCharArray()));
    }

    @Test
    void verifyDebeRetornarFalseCuandoEntradasInvalidas() {
        assertFalse(hasher.verify(null, null));
        assertFalse(hasher.verify("".toCharArray(), "$argon2id$dummy"));
        assertFalse(hasher.verify("clave".toCharArray(), ""));
    }
}

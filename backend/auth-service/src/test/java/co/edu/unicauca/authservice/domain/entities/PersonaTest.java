package co.edu.unicauca.authservice.domain.entities;

import co.edu.unicauca.shared.contracts.model.Programa;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PersonaTest {

    static class PersonaFake extends Persona {
        public PersonaFake(String id, String codigo, String nombres, String apellidos,
                           String celular, Programa programa, Usuario usuario) {
            super(id, codigo, nombres, apellidos, celular, programa, usuario);
        }
    }

    @Test
    void shouldCreatePersonaWithAllFields() {
        Usuario usuario = new Usuario("test@unicauca.edu.co", "hash", java.util.List.of());
        PersonaFake p = new PersonaFake(
                UUID.randomUUID().toString(),
                "20250001",
                "Juan",
                "Ortega",
                "3150000000",
                Programa.IngenieriaDeSistemas,
                usuario
        );

        assertEquals("20250001", p.getCodigo());
        assertEquals("Juan", p.getNombres());
        assertEquals("Ortega", p.getApellidos());
        assertEquals("3150000000", p.getCelular());
        assertEquals(Programa.IngenieriaDeSistemas, p.getPrograma());
        assertSame(usuario, p.getUsuario());
    }
}

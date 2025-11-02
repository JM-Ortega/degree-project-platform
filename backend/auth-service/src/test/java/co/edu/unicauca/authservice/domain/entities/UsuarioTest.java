package co.edu.unicauca.authservice.domain.entities;

import co.edu.unicauca.shared.contracts.model.Rol;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {

    @Test
    void shouldGenerateIdAndStoreData() {
        Usuario u = new Usuario("juan.perez@unicauca.edu.co", "hash123", List.of(Rol.Docente));

        assertNotNull(u.getId(), "El UUID no debe ser nulo");
        assertEquals("juan.perez@unicauca.edu.co", u.getEmail());
        assertEquals("hash123", u.getPasswordHash());
        assertEquals(1, u.getRoles().size());
        assertTrue(u.getRoles().contains(Rol.Docente));
    }

    @Test
    void shouldAllowUpdatingEmailAndPassword() {
        Usuario u = new Usuario("demo@unicauca.edu.co", "hashA", List.of(Rol.Estudiante));
        u.setEmail("nuevo@unicauca.edu.co");
        u.setPasswordHash("hashB");

        assertEquals("nuevo@unicauca.edu.co", u.getEmail());
        assertEquals("hashB", u.getPasswordHash());
    }

    @Test
    void shouldAllowUpdatingRoles() {
        Usuario u = new Usuario("demo@unicauca.edu.co", "hash", List.of(Rol.Estudiante));
        u.setRoles(List.of(Rol.Estudiante, Rol.Docente));

        assertEquals(2, u.getRoles().size());
        assertTrue(u.getRoles().contains(Rol.Docente));
    }
}

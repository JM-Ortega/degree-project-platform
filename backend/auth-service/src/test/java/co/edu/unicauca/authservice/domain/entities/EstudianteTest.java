package co.edu.unicauca.authservice.domain.entities;

import co.edu.unicauca.shared.contracts.model.Programa;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EstudianteTest {

    @Test
    void shouldCreateEstudiante() {
        Usuario u = new Usuario("estu@unicauca.edu.co", "hash", List.of());
        Estudiante est = new Estudiante(
                UUID.randomUUID().toString(),
                "20250002",
                "Camila",
                "López",
                "3150000001",
                Programa.INGENIERIA_DE_SISTEMAS,
                u
        );

        assertEquals("20250002", est.getCodigo());
        assertEquals("Camila", est.getNombres());
        assertEquals("López", est.getApellidos());
        assertEquals("3150000001", est.getCelular());
        assertEquals(Programa.INGENIERIA_DE_SISTEMAS, est.getPrograma());
        assertSame(u, est.getUsuario());
    }
}

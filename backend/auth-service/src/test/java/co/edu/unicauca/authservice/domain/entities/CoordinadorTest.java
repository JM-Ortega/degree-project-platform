package co.edu.unicauca.authservice.domain.entities;

import co.edu.unicauca.shared.contracts.model.Programa;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CoordinadorTest {

    @Test
    void shouldCreateCoordinadorWithProgram() {
        Usuario u = new Usuario("coord@unicauca.edu.co", "hash", List.of());
        Coordinador c = new Coordinador(
                UUID.randomUUID().toString(),
                "COOR-001",
                "Marta",
                "Ruiz",
                "3170000003",
                Programa.AUTOMATICA_INDUSTRIAL,
                u,
                Programa.AUTOMATICA_INDUSTRIAL
        );

        assertEquals("COOR-001", c.getCodigo());
        assertEquals("Marta", c.getNombres());
        assertEquals("Ruiz", c.getApellidos());
        assertEquals(Programa.AUTOMATICA_INDUSTRIAL, c.getProgramaCoordinado());
    }

    @Test
    void shouldUpdateProgramCoordinated() {
        Usuario u = new Usuario("coord@unicauca.edu.co", "hash", List.of());
        Coordinador c = new Coordinador(
                UUID.randomUUID().toString(),
                "COOR-002",
                "Marta",
                "Ruiz",
                "3170000003",
                Programa.AUTOMATICA_INDUSTRIAL,
                u,
                Programa.AUTOMATICA_INDUSTRIAL
        );

        c.setProgramaCoordinado(Programa.INGENIERIA_DE_SISTEMAS);

        assertEquals(Programa.INGENIERIA_DE_SISTEMAS, c.getProgramaCoordinado());
    }
}

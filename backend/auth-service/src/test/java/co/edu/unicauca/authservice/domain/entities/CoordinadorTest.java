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
                Programa.AutomaticaIndustrial,
                u,
                Programa.AutomaticaIndustrial
        );

        assertEquals("COOR-001", c.getCodigo());
        assertEquals("Marta", c.getNombres());
        assertEquals("Ruiz", c.getApellidos());
        assertEquals(Programa.AutomaticaIndustrial, c.getProgramaCoordinado());
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
                Programa.AutomaticaIndustrial,
                u,
                Programa.AutomaticaIndustrial
        );

        c.setProgramaCoordinado(Programa.IngenieriaDeSistemas);

        assertEquals(Programa.IngenieriaDeSistemas, c.getProgramaCoordinado());
    }
}

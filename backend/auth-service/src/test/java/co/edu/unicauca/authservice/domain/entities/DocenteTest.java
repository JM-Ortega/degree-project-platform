package co.edu.unicauca.authservice.domain.entities;

import co.edu.unicauca.shared.contracts.model.Departamento;
import co.edu.unicauca.shared.contracts.model.Programa;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DocenteTest {

    @Test
    void shouldCreateDocenteWithDepartamento() {
        Usuario u = new Usuario("doc@unicauca.edu.co", "hash", List.of());
        Docente d = new Docente(
                UUID.randomUUID().toString(),
                "DOC-001",
                "Andrés",
                "García",
                "3160000002",
                Programa.INGENIERIA_DE_SISTEMAS,
                u,
                Departamento.SISTEMAS
        );

        assertEquals("DOC-001", d.getCodigo());
        assertEquals("Andrés", d.getNombres());
        assertEquals("García", d.getApellidos());
        assertEquals(Departamento.SISTEMAS, d.getDepartamento());
    }

    @Test
    void shouldAllowChangingDepartamento() {
        Usuario u = new Usuario("doc@unicauca.edu.co", "hash", List.of());
        Docente d = new Docente(
                UUID.randomUUID().toString(),
                "DOC-002",
                "Ana",
                "Vera",
                "3160000003",
                Programa.INGENIERIA_DE_SISTEMAS,
                u,
                Departamento.SISTEMAS
        );

        d.setDepartamento(Departamento.ELECTRONICA_INSTRUMENTACION_Y_CONTROL);

        assertEquals(Departamento.ELECTRONICA_INSTRUMENTACION_Y_CONTROL, d.getDepartamento());
    }
}

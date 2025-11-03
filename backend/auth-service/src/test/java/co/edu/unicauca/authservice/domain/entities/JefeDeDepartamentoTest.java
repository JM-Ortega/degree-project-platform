package co.edu.unicauca.authservice.domain.entities;

import co.edu.unicauca.shared.contracts.model.Departamento;
import co.edu.unicauca.shared.contracts.model.Programa;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JefeDeDepartamentoTest {

    @Test
    void shouldCreateJefeWithDepartment() {
        Usuario u = new Usuario("jefe@unicauca.edu.co", "hash", List.of());
        JefeDeDepartamento j = new JefeDeDepartamento(
                UUID.randomUUID().toString(),
                "JEFE-001",
                "Luis",
                "Torres",
                "3180000004",
                Programa.INGENIERIA_DE_SISTEMAS,
                u,
                Departamento.SISTEMAS
        );

        assertEquals("JEFE-001", j.getCodigo());
        assertEquals("Luis", j.getNombres());
        assertEquals("Torres", j.getApellidos());
        assertEquals(Departamento.SISTEMAS, j.getDepartamento());
    }
}

package co.edu.unicauca.authservice.services;

import co.edu.unicauca.authservice.domain.entities.*;
import co.edu.unicauca.authservice.dto.RegistroPersonaDto;
import co.edu.unicauca.shared.contracts.model.Departamento;
import co.edu.unicauca.shared.contracts.model.Programa;
import co.edu.unicauca.shared.contracts.model.Rol;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PersonaFactoryTest {

    private final PersonaFactory factory = new PersonaFactory();

    private Usuario buildUsuarioDemo(List<Rol> roles) {
        return new Usuario("test@unicauca.edu.co", "hash123", roles);
    }

    @Test
    void debeCrearJefeDeDepartamentoCuandoTieneEseRol() {
        // orden: nombres, apellidos, celular, email, password, programa, roles, departamento
        var dto = new RegistroPersonaDto(
                "Luis",
                "Torres",
                "3180000004",
                "jefe@unicauca.edu.co",
                "Uni123456*",
                Programa.INGENIERIA_DE_SISTEMAS,
                List.of(Rol.JEFE_DE_DEPARTAMENTO, Rol.DOCENTE),
                Departamento.SISTEMAS
        );

        var usuario = buildUsuarioDemo(dto.roles());

        Persona persona = factory.crearDesdeDto(dto, usuario);

        assertTrue(persona instanceof JefeDeDepartamento);
        var jefe = (JefeDeDepartamento) persona;
        assertEquals(Departamento.SISTEMAS, jefe.getDepartamento());
        assertEquals("Luis", jefe.getNombres());
        assertEquals("Torres", jefe.getApellidos());
        assertNotNull(jefe.getUsuario());
    }

    @Test
    void debeCrearCoordinadorCuandoTieneEseRol() {
        var dto = new RegistroPersonaDto(
                "Marta",
                "Ruiz",
                "3170000003",
                "coord@unicauca.edu.co",
                "Uni123456*",
                Programa.AUTOMATICA_INDUSTRIAL,
                List.of(Rol.COORDINADOR),
                null // coordinador no usa departamento
        );

        var usuario = buildUsuarioDemo(dto.roles());

        Persona persona = factory.crearDesdeDto(dto, usuario);

        assertTrue(persona instanceof Coordinador);
        var coord = (Coordinador) persona;
        // en tu factory usas dto.programa() como programa coordinado
        assertEquals(Programa.AUTOMATICA_INDUSTRIAL, coord.getProgramaCoordinado());
    }

    @Test
    void debeCrearDocenteCuandoTieneEseRol() {
        var dto = new RegistroPersonaDto(
                "Andrés",
                "García",
                "3160000002",
                "doc@unicauca.edu.co",
                "Uni123456*",
                Programa.INGENIERIA_DE_SISTEMAS,
                List.of(Rol.DOCENTE),
                Departamento.SISTEMAS
        );

        var usuario = buildUsuarioDemo(dto.roles());

        Persona persona = factory.crearDesdeDto(dto, usuario);

        assertTrue(persona instanceof Docente);
        var doc = (Docente) persona;
        assertEquals(Departamento.SISTEMAS, doc.getDepartamento());
        assertEquals(Programa.INGENIERIA_DE_SISTEMAS, doc.getPrograma());
    }

    @Test
    void debeCrearEstudianteCuandoSoloTieneEseRol() {
        var dto = new RegistroPersonaDto(
                "Camila",
                "López",
                "3150000001",
                "estu@unicauca.edu.co",
                "Uni123456*",
                Programa.INGENIERIA_DE_SISTEMAS,
                List.of(Rol.ESTUDIANTE),
                null
        );

        var usuario = buildUsuarioDemo(dto.roles());

        Persona persona = factory.crearDesdeDto(dto, usuario);

        assertTrue(persona instanceof Estudiante);
        assertEquals("Camila", persona.getNombres());
        assertEquals("López", persona.getApellidos());
        assertEquals(Programa.INGENIERIA_DE_SISTEMAS, persona.getPrograma());
    }

    @Test
    void debeFallarCuandoNoHayRoles() {
        var dto = new RegistroPersonaDto(
                "Sin",
                "Rol",
                "3000000000",
                "sinrol@unicauca.edu.co",
                "Uni123456*",
                Programa.INGENIERIA_DE_SISTEMAS,
                List.of(),     // ← aquí va la lista de roles VACÍA
                null           // ← y luego el depto
        );

        var usuario = buildUsuarioDemo(List.of());

        assertThrows(IllegalArgumentException.class,
                () -> factory.crearDesdeDto(dto, usuario));
    }
}

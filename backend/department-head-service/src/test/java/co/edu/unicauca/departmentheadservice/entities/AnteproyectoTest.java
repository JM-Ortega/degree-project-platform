package co.edu.unicauca.departmentheadservice.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class AnteproyectoTest {

    private Anteproyecto anteproyecto;
    private Docente docenteMock;

    @BeforeEach
    void setUp() {
        // Creamos un Docente mockeado para usar en los tests
        docenteMock = mock(Docente.class);

        // Instanciamos la clase Anteproyecto con datos de prueba
        anteproyecto = new Anteproyecto(
            "Proyecto de Investigación",
            "Descripción del proyecto de investigación",
            LocalDate.of(2025, 11, 2),
            List.of(docenteMock)
        );
    }

    @Test
    void testConstructorAndGetters() {
        assertNotNull(anteproyecto);
        assertEquals("Proyecto de Investigación", anteproyecto.getTitulo());
        assertEquals("Descripción del proyecto de investigación", anteproyecto.getDescripcion());
        assertEquals(LocalDate.of(2025, 11, 2), anteproyecto.getFechaCreacion());
        assertEquals(1, anteproyecto.getEvaluadores().size());
        assertSame(docenteMock, anteproyecto.getEvaluadores().get(0));
    }

    @Test
    void testSetters() {
        // Cambiar valores a través de los setters
        anteproyecto.setTitulo("Nuevo Titulo");
        anteproyecto.setDescripcion("Nueva descripción");
        anteproyecto.setFechaCreacion(LocalDate.of(2025, 12, 15));

        // Verificar que los valores se actualizan correctamente
        assertEquals("Nuevo Titulo", anteproyecto.getTitulo());
        assertEquals("Nueva descripción", anteproyecto.getDescripcion());
        assertEquals(LocalDate.of(2025, 12, 15), anteproyecto.getFechaCreacion());
    }

    @Test
    void testSetEvaluadores() {
        // Mock para un segundo docente
        Docente docenteMock2 = mock(Docente.class);

        anteproyecto.setEvaluadores(List.of(docenteMock, docenteMock2));

        // Verificar que los evaluadores se han actualizado correctamente
        assertEquals(2, anteproyecto.getEvaluadores().size());
        assertSame(docenteMock, anteproyecto.getEvaluadores().get(0));
        assertSame(docenteMock2, anteproyecto.getEvaluadores().get(1));
    }

    @Test
    void testConstructorSinArgumentos() {
        // Crear un Anteproyecto sin argumentos (que debería ser usado por JPA)
        Anteproyecto anteproyectoSinArgumentos = new Anteproyecto();
        assertNotNull(anteproyectoSinArgumentos);
    }

}

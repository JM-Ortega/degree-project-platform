package co.edu.unicauca.departmentheadservice.services;

import co.edu.unicauca.departmentheadservice.access.AnteproyectoRepository;
import co.edu.unicauca.departmentheadservice.entities.Anteproyecto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para la clase AnteproyectoService.
 */
@ExtendWith(MockitoExtension.class)
class AnteproyectoServiceTest {

    @Mock
    private AnteproyectoRepository anteproyectoRepository;

    @InjectMocks
    private AnteproyectoService anteproyectoService;

    private Anteproyecto anteproyecto1;
    private Anteproyecto anteproyecto2;
    private Anteproyecto anteproyecto3;

    @BeforeEach
    void setUp() {
        // Configurar datos de prueba usando el constructor público
        anteproyecto1 = new Anteproyecto(
                1L, // anteproyectoId
                101L, // proyectoId
                "Sistema de Gestión Académica",
                "Descripción del sistema académico",
                LocalDate.of(2024, 1, 15),
                Collections.emptyList(), // sin evaluadores
                "est1@unicauca.edu.co", // estudianteCorreo
                "dir1@unicauca.edu.co", // directorCorreo
                "SISTEMAS" // departamento
        );

        anteproyecto2 = new Anteproyecto(
                2L, // anteproyectoId
                102L, // proyectoId
                "Plataforma de E-learning",
                "Descripción de plataforma e-learning",
                LocalDate.of(2024, 2, 20),
                Collections.emptyList(), // sin evaluadores
                "est2@unicauca.edu.co", // estudianteCorreo
                "dir2@unicauca.edu.co", // directorCorreo
                "SISTEMAS" // departamento
        );

        anteproyecto3 = new Anteproyecto(
                3L, // anteproyectoId
                103L, // proyectoId
                "Aplicación Móvil para Salud",
                "Descripción de app móvil salud",
                LocalDate.of(2024, 3, 10),
                Collections.emptyList(), // sin evaluadores
                "est3@unicauca.edu.co", // estudianteCorreo
                "dir3@unicauca.edu.co", // directorCorreo
                "SISTEMAS" // departamento
        );


        // Usar reflection para establecer los IDs ya que no hay setter público
        setId(anteproyecto1, 1L);
        setId(anteproyecto2, 2L);
        setId(anteproyecto3, 3L);
    }

    /**
     * Método helper para establecer el ID usando reflection
     */
    private void setId(Anteproyecto anteproyecto, Long id) {
        try {
            var idField = Anteproyecto.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(anteproyecto, id);
        } catch (Exception e) {
            throw new RuntimeException("Error al establecer ID mediante reflection", e);
        }
    }

    @Test
    void testObtenerAnteproyectosSinEvaluadores_ShouldReturnEmptyList() {
        // Preparar
        when(anteproyectoRepository.findByEvaluadoresIsEmpty()).thenReturn(Collections.emptyList());

        // Ejecutar
        List<Anteproyecto> resultado = anteproyectoService.obtenerAnteproyectosSinEvaluadores();

        // Verificar
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(anteproyectoRepository).findByEvaluadoresIsEmpty();
    }

    @Test
    void testObtenerAnteproyectosSinEvaluadores_ShouldReturnListWithItems() {
        // Preparar
        List<Anteproyecto> anteproyectosEsperados = Arrays.asList(anteproyecto1, anteproyecto2);
        when(anteproyectoRepository.findByEvaluadoresIsEmpty()).thenReturn(anteproyectosEsperados);

        // Ejecutar
        List<Anteproyecto> resultado = anteproyectoService.obtenerAnteproyectosSinEvaluadores();

        // Verificar
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(anteproyectosEsperados, resultado);
        verify(anteproyectoRepository).findByEvaluadoresIsEmpty();
    }

    @Test
    void testBuscarPorNombreOIdSinEvaluadores_WithNullParameters_ShouldReturnAll() {
        // Preparar
        List<Anteproyecto> anteproyectosEsperados = Arrays.asList(anteproyecto1, anteproyecto2, anteproyecto3);
        when(anteproyectoRepository.findByEvaluadoresIsEmpty()).thenReturn(anteproyectosEsperados);

        // Ejecutar
        List<Anteproyecto> resultado = anteproyectoService.buscarPorNombreOIdSinEvaluadores(null, null);

        // Verificar
        assertNotNull(resultado);
        assertEquals(3, resultado.size());
        verify(anteproyectoRepository).findByEvaluadoresIsEmpty();
        verify(anteproyectoRepository, never()).findByEvaluadoresIsEmptyAndTituloContainingIgnoreCase(anyString());
        verify(anteproyectoRepository, never()).findByEvaluadoresIsEmptyAndId(anyLong());
    }

    @Test
    void testBuscarPorNombreOIdSinEvaluadores_WithEmptyParameters_ShouldReturnAll() {
        // Preparar
        List<Anteproyecto> anteproyectosEsperados = Arrays.asList(anteproyecto1, anteproyecto2);
        when(anteproyectoRepository.findByEvaluadoresIsEmpty()).thenReturn(anteproyectosEsperados);

        // Ejecutar
        List<Anteproyecto> resultado = anteproyectoService.buscarPorNombreOIdSinEvaluadores("", "");

        // Verificar
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(anteproyectoRepository).findByEvaluadoresIsEmpty();
        verify(anteproyectoRepository, never()).findByEvaluadoresIsEmptyAndTituloContainingIgnoreCase(anyString());
        verify(anteproyectoRepository, never()).findByEvaluadoresIsEmptyAndId(anyLong());
    }

    @Test
    void testBuscarPorNombreOIdSinEvaluadores_WithOnlyName_ShouldReturnFilteredResults() {
        // Preparar
        String nombreBusqueda = "Sistema";
        List<Anteproyecto> anteproyectosEsperados = Arrays.asList(anteproyecto1);
        when(anteproyectoRepository.findByEvaluadoresIsEmptyAndTituloContainingIgnoreCase(nombreBusqueda))
                .thenReturn(anteproyectosEsperados);

        // Ejecutar
        List<Anteproyecto> resultado = anteproyectoService.buscarPorNombreOIdSinEvaluadores(nombreBusqueda, null);

        // Verificar
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(anteproyecto1, resultado.get(0));
        verify(anteproyectoRepository).findByEvaluadoresIsEmptyAndTituloContainingIgnoreCase(nombreBusqueda);
        verify(anteproyectoRepository, never()).findByEvaluadoresIsEmptyAndId(anyLong());
    }

    @Test
    void testBuscarPorNombreOIdSinEvaluadores_WithOnlyId_ShouldReturnFilteredResults() {
        // Preparar
        String idBusqueda = "2";
        List<Anteproyecto> anteproyectosEsperados = Arrays.asList(anteproyecto2);
        when(anteproyectoRepository.findByEvaluadoresIsEmptyAndId(2L))
                .thenReturn(anteproyectosEsperados);

        // Ejecutar
        List<Anteproyecto> resultado = anteproyectoService.buscarPorNombreOIdSinEvaluadores(null, idBusqueda);

        // Verificar
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(anteproyecto2, resultado.get(0));
        verify(anteproyectoRepository).findByEvaluadoresIsEmptyAndId(2L);
        verify(anteproyectoRepository, never()).findByEvaluadoresIsEmptyAndTituloContainingIgnoreCase(anyString());
    }

    @Test
    void testBuscarPorNombreOIdSinEvaluadores_WithNameAndId_ShouldReturnUnionOfResults() {
        // Preparar
        String nombreBusqueda = "Sistema";
        String idBusqueda = "1";

        List<Anteproyecto> porNombre = Arrays.asList(anteproyecto1);
        List<Anteproyecto> porId = Arrays.asList(anteproyecto1); // Mismo anteproyecto

        when(anteproyectoRepository.findByEvaluadoresIsEmptyAndTituloContainingIgnoreCase(nombreBusqueda))
                .thenReturn(porNombre);
        when(anteproyectoRepository.findByEvaluadoresIsEmptyAndId(1L))
                .thenReturn(porId);

        // Ejecutar
        List<Anteproyecto> resultado = anteproyectoService.buscarPorNombreOIdSinEvaluadores(nombreBusqueda, idBusqueda);

        // Verificar
        assertNotNull(resultado);
        assertEquals(1, resultado.size()); // Debe eliminar duplicados
        assertEquals(anteproyecto1, resultado.get(0));
        verify(anteproyectoRepository).findByEvaluadoresIsEmptyAndTituloContainingIgnoreCase(nombreBusqueda);
        verify(anteproyectoRepository).findByEvaluadoresIsEmptyAndId(1L);
    }

    @Test
    void testBuscarPorNombreOIdSinEvaluadores_WithNameAndIdDifferentResults_ShouldReturnCombinedResults() {
        // Preparar
        String nombreBusqueda = "Sistema";
        String idBusqueda = "2";

        List<Anteproyecto> porNombre = Arrays.asList(anteproyecto1);
        List<Anteproyecto> porId = Arrays.asList(anteproyecto2);

        when(anteproyectoRepository.findByEvaluadoresIsEmptyAndTituloContainingIgnoreCase(nombreBusqueda))
                .thenReturn(porNombre);
        when(anteproyectoRepository.findByEvaluadoresIsEmptyAndId(2L))
                .thenReturn(porId);

        // Ejecutar
        List<Anteproyecto> resultado = anteproyectoService.buscarPorNombreOIdSinEvaluadores(nombreBusqueda, idBusqueda);

        // Verificar
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.contains(anteproyecto1));
        assertTrue(resultado.contains(anteproyecto2));
    }

    @Test
    void testBuscarPorNombreOIdSinEvaluadores_WithInvalidId_ShouldHandleNumberFormatException() {
        // Preparar
        String nombreBusqueda = "Sistema";
        String idInvalido = "abc";

        List<Anteproyecto> porNombre = Arrays.asList(anteproyecto1);
        when(anteproyectoRepository.findByEvaluadoresIsEmptyAndTituloContainingIgnoreCase(nombreBusqueda))
                .thenReturn(porNombre);

        // Ejecutar
        List<Anteproyecto> resultado = anteproyectoService.buscarPorNombreOIdSinEvaluadores(nombreBusqueda, idInvalido);

        // Verificar
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(anteproyecto1, resultado.get(0));
        verify(anteproyectoRepository).findByEvaluadoresIsEmptyAndTituloContainingIgnoreCase(nombreBusqueda);
        // No debería llamar al repositorio para búsqueda por ID ya que lanza NumberFormatException
    }

    @Test
    void testBuscarPorNombreOIdSinEvaluadores_WithNameContainingSpaces_ShouldTrimSpaces() {
        // Preparar
        String nombreConEspacios = "  Sistema  ";
        String nombreTrimmed = "Sistema";
        List<Anteproyecto> anteproyectosEsperados = Arrays.asList(anteproyecto1);

        when(anteproyectoRepository.findByEvaluadoresIsEmptyAndTituloContainingIgnoreCase(nombreTrimmed))
                .thenReturn(anteproyectosEsperados);

        // Ejecutar
        List<Anteproyecto> resultado = anteproyectoService.buscarPorNombreOIdSinEvaluadores(nombreConEspacios, null);

        // Verificar
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(anteproyectoRepository).findByEvaluadoresIsEmptyAndTituloContainingIgnoreCase(nombreTrimmed);
    }

    @Test
    void testBuscarPorNombreOIdSinEvaluadores_WithIdContainingSpaces_ShouldTrimSpaces() {
        // Preparar
        String idConEspacios = "  1  ";
        List<Anteproyecto> anteproyectosEsperados = Arrays.asList(anteproyecto1);

        when(anteproyectoRepository.findByEvaluadoresIsEmptyAndId(1L))
                .thenReturn(anteproyectosEsperados);

        // Ejecutar
        List<Anteproyecto> resultado = anteproyectoService.buscarPorNombreOIdSinEvaluadores(null, idConEspacios);

        // Verificar
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(anteproyectoRepository).findByEvaluadoresIsEmptyAndId(1L);
    }

    @Test
    void testBuscarPorNombreOIdSinEvaluadores_WithCaseInsensitiveName_ShouldWork() {
        // Preparar
        String nombreEnMayusculas = "SISTEMA";
        List<Anteproyecto> anteproyectosEsperados = Arrays.asList(anteproyecto1);

        when(anteproyectoRepository.findByEvaluadoresIsEmptyAndTituloContainingIgnoreCase(nombreEnMayusculas))
                .thenReturn(anteproyectosEsperados);

        // Ejecutar
        List<Anteproyecto> resultado = anteproyectoService.buscarPorNombreOIdSinEvaluadores(nombreEnMayusculas, null);

        // Verificar
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(anteproyectoRepository).findByEvaluadoresIsEmptyAndTituloContainingIgnoreCase(nombreEnMayusculas);
    }

    @Test
    void testBuscarPorNombreOIdSinEvaluadores_WithNoResults_ShouldReturnEmptyList() {
        // Preparar
        String nombreBusqueda = "Inexistente";
        when(anteproyectoRepository.findByEvaluadoresIsEmptyAndTituloContainingIgnoreCase(nombreBusqueda))
                .thenReturn(Collections.emptyList());

        // Ejecutar
        List<Anteproyecto> resultado = anteproyectoService.buscarPorNombreOIdSinEvaluadores(nombreBusqueda, null);

        // Verificar
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(anteproyectoRepository).findByEvaluadoresIsEmptyAndTituloContainingIgnoreCase(nombreBusqueda);
    }
}
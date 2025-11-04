package co.edu.unicauca.frontend.services.departmenthead;

import co.edu.unicauca.frontend.dto.AnteproyectoDto;
import co.edu.unicauca.frontend.infra.http.HttpClientException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DepartmentHeadServiceFrontTest {

    private DepartmentHeadServiceFront departmentHeadServiceFront;
    private DepartmentHeadApi mockDepartmentHeadApi;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockDepartmentHeadApi = mock(DepartmentHeadApi.class);
        departmentHeadServiceFront = new DepartmentHeadServiceFront(mockDepartmentHeadApi);
    }

    @Test
    void testObtenerAnteproyectosSinEvaluadores_Success() throws Exception {
        // Configurar
        AnteproyectoDto anteproyecto1 = new AnteproyectoDto();
        anteproyecto1.setId(1L);
        anteproyecto1.setTitulo("Anteproyecto 1");
        anteproyecto1.setDescripcion("Descripción 1");
        anteproyecto1.setFechaCreacion("2024-01-01");

        AnteproyectoDto anteproyecto2 = new AnteproyectoDto();
        anteproyecto2.setId(2L);
        anteproyecto2.setTitulo("Anteproyecto 2");
        anteproyecto2.setDescripcion("Descripción 2");
        anteproyecto2.setFechaCreacion("2024-01-02");

        List<AnteproyectoDto> expectedAnteproyectos = Arrays.asList(anteproyecto1, anteproyecto2);

        when(mockDepartmentHeadApi.obtenerAnteproyectosSinEvaluadores()).thenReturn(expectedAnteproyectos);

        // Ejecutar
        List<AnteproyectoDto> result = departmentHeadServiceFront.obtenerAnteproyectosSinEvaluadores();

        // Verificar
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Anteproyecto 1", result.get(0).getTitulo());
        assertEquals("Anteproyecto 2", result.get(1).getTitulo());
        verify(mockDepartmentHeadApi).obtenerAnteproyectosSinEvaluadores();
    }

    @Test
    void testObtenerAnteproyectosSinEvaluadores_WithHttpClientException() throws Exception {
        // Configurar
        HttpClientException httpException = new HttpClientException(500, "Internal Server Error");
        when(mockDepartmentHeadApi.obtenerAnteproyectosSinEvaluadores()).thenThrow(httpException);

        // Ejecutar y verificar
        Exception exception = assertThrows(Exception.class, () ->
            departmentHeadServiceFront.obtenerAnteproyectosSinEvaluadores()
        );

        // Verificar
        assertTrue(exception.getMessage().contains("Error al obtener Anteproyectos sin evaluadores"));
        assertTrue(exception.getMessage().contains("Internal Server Error"));
        assertNotNull(exception.getCause());
        assertEquals(httpException, exception.getCause());
        verify(mockDepartmentHeadApi).obtenerAnteproyectosSinEvaluadores();
    }

    @Test
    void testObtenerAnteproyectosSinEvaluadores_WithGenericException() throws Exception {
        // Configurar
        RuntimeException genericException = new RuntimeException("Connection timeout");
        when(mockDepartmentHeadApi.obtenerAnteproyectosSinEvaluadores()).thenThrow(genericException);

        // Ejecutar y verificar
        Exception exception = assertThrows(Exception.class, () ->
            departmentHeadServiceFront.obtenerAnteproyectosSinEvaluadores()
        );

        // Verificar
        assertEquals(genericException, exception);
        verify(mockDepartmentHeadApi).obtenerAnteproyectosSinEvaluadores();
    }

    @Test
    void testBuscarAnteproyectos_ByNombre_Success() throws Exception {
        // Configurar
        String nombre = "Sistema";
        String id = null;

        AnteproyectoDto anteproyecto1 = new AnteproyectoDto();
        anteproyecto1.setId(1L);
        anteproyecto1.setTitulo("Sistema de Gestión");
        anteproyecto1.setDescripcion("Descripción 1");
        anteproyecto1.setFechaCreacion("2024-01-01");

        AnteproyectoDto anteproyecto2 = new AnteproyectoDto();
        anteproyecto2.setId(3L);
        anteproyecto2.setTitulo("Sistema Web");
        anteproyecto2.setDescripcion("Descripción 3");
        anteproyecto2.setFechaCreacion("2024-01-03");

        List<AnteproyectoDto> expectedAnteproyectos = Arrays.asList(anteproyecto1, anteproyecto2);

        when(mockDepartmentHeadApi.buscarPorNombreOIdSinEvaluadores(nombre, id)).thenReturn(expectedAnteproyectos);

        // Ejecutar
        List<AnteproyectoDto> result = departmentHeadServiceFront.buscarAnteproyectos(nombre, id);

        // Verificar
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).getTitulo().contains("Sistema"));
        assertTrue(result.get(1).getTitulo().contains("Sistema"));
        verify(mockDepartmentHeadApi).buscarPorNombreOIdSinEvaluadores(nombre, id);
    }

    @Test
    void testBuscarAnteproyectos_ById_Success() throws Exception {
        // Configurar
        String nombre = null;
        String id = "5";

        AnteproyectoDto anteproyecto = new AnteproyectoDto();
        anteproyecto.setId(5L);
        anteproyecto.setTitulo("Anteproyecto 5");
        anteproyecto.setDescripcion("Descripción 5");
        anteproyecto.setFechaCreacion("2024-01-05");

        List<AnteproyectoDto> expectedAnteproyectos = Arrays.asList(anteproyecto);

        when(mockDepartmentHeadApi.buscarPorNombreOIdSinEvaluadores(nombre, id)).thenReturn(expectedAnteproyectos);

        // Ejecutar
        List<AnteproyectoDto> result = departmentHeadServiceFront.buscarAnteproyectos(nombre, id);

        // Verificar
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(5L, result.get(0).getId());
        assertEquals("Anteproyecto 5", result.get(0).getTitulo());
        verify(mockDepartmentHeadApi).buscarPorNombreOIdSinEvaluadores(nombre, id);
    }

    @Test
    void testBuscarAnteproyectos_ByNombreAndId_Success() throws Exception {
        // Configurar
        String nombre = "Sistema";
        String id = "10";

        AnteproyectoDto anteproyecto = new AnteproyectoDto();
        anteproyecto.setId(10L);
        anteproyecto.setTitulo("Sistema Integral");
        anteproyecto.setDescripcion("Descripción 10");
        anteproyecto.setFechaCreacion("2024-01-10");

        List<AnteproyectoDto> expectedAnteproyectos = Arrays.asList(anteproyecto);

        when(mockDepartmentHeadApi.buscarPorNombreOIdSinEvaluadores(nombre, id)).thenReturn(expectedAnteproyectos);

        // Ejecutar
        List<AnteproyectoDto> result = departmentHeadServiceFront.buscarAnteproyectos(nombre, id);

        // Verificar
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getId());
        assertTrue(result.get(0).getTitulo().contains("Sistema"));
        verify(mockDepartmentHeadApi).buscarPorNombreOIdSinEvaluadores(nombre, id);
    }

    @Test
    void testBuscarAnteproyectos_EmptyResult() throws Exception {
        // Configurar
        String nombre = "Inexistente";
        String id = null;

        List<AnteproyectoDto> expectedAnteproyectos = Arrays.asList();

        when(mockDepartmentHeadApi.buscarPorNombreOIdSinEvaluadores(nombre, id)).thenReturn(expectedAnteproyectos);

        // Ejecutar
        List<AnteproyectoDto> result = departmentHeadServiceFront.buscarAnteproyectos(nombre, id);

        // Verificar
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(mockDepartmentHeadApi).buscarPorNombreOIdSinEvaluadores(nombre, id);
    }

    @Test
    void testBuscarAnteproyectos_WithHttpClientException() throws Exception {
        // Configurar
        String nombre = "Test";
        String id = null;

        HttpClientException httpException = new HttpClientException(404, "Not Found");
        when(mockDepartmentHeadApi.buscarPorNombreOIdSinEvaluadores(nombre, id)).thenThrow(httpException);

        // Ejecutar y verificar
        Exception exception = assertThrows(Exception.class, () ->
            departmentHeadServiceFront.buscarAnteproyectos(nombre, id)
        );

        // Verificar
        assertTrue(exception.getMessage().contains("Error al buscar Anteproyectos"));
        assertTrue(exception.getMessage().contains("Not Found"));
        assertNotNull(exception.getCause());
        assertEquals(httpException, exception.getCause());
        verify(mockDepartmentHeadApi).buscarPorNombreOIdSinEvaluadores(nombre, id);
    }

    @Test
    void testBuscarAnteproyectos_WithGenericException() throws Exception {
        // Configurar
        String nombre = "Test";
        String id = null;

        RuntimeException genericException = new RuntimeException("Network error");
        when(mockDepartmentHeadApi.buscarPorNombreOIdSinEvaluadores(nombre, id)).thenThrow(genericException);

        // Ejecutar y verificar
        Exception exception = assertThrows(Exception.class, () ->
            departmentHeadServiceFront.buscarAnteproyectos(nombre, id)
        );

        // Verificar
        assertEquals(genericException, exception);
        verify(mockDepartmentHeadApi).buscarPorNombreOIdSinEvaluadores(nombre, id);
    }

    @Test
    void testBuscarAnteproyectos_WithNullParameters() throws Exception {
        // Configurar
        String nombre = null;
        String id = null;

        AnteproyectoDto anteproyecto1 = new AnteproyectoDto();
        anteproyecto1.setId(1L);
        anteproyecto1.setTitulo("Anteproyecto 1");
        anteproyecto1.setDescripcion("Descripción 1");
        anteproyecto1.setFechaCreacion("2024-01-01");

        AnteproyectoDto anteproyecto2 = new AnteproyectoDto();
        anteproyecto2.setId(2L);
        anteproyecto2.setTitulo("Anteproyecto 2");
        anteproyecto2.setDescripcion("Descripción 2");
        anteproyecto2.setFechaCreacion("2024-01-02");

        List<AnteproyectoDto> expectedAnteproyectos = Arrays.asList(anteproyecto1, anteproyecto2);

        when(mockDepartmentHeadApi.buscarPorNombreOIdSinEvaluadores(nombre, id)).thenReturn(expectedAnteproyectos);

        // Ejecutar
        List<AnteproyectoDto> result = departmentHeadServiceFront.buscarAnteproyectos(nombre, id);

        // Verificar
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(mockDepartmentHeadApi).buscarPorNombreOIdSinEvaluadores(nombre, id);
    }

    @Test
    void testBuscarAnteproyectos_WithEvaluadoresList() throws Exception {
        // Configurar - probar que la lista de evaluadores se maneja correctamente
        String nombre = "Test";
        String id = null;

        AnteproyectoDto anteproyecto = new AnteproyectoDto();
        anteproyecto.setId(1L);
        anteproyecto.setTitulo("Anteproyecto con Evaluadores");
        anteproyecto.setDescripcion("Descripción");
        anteproyecto.setFechaCreacion("2024-01-01");
        anteproyecto.setEvaluadores(Arrays.asList("Evaluador1", "Evaluador2"));

        List<AnteproyectoDto> expectedAnteproyectos = Arrays.asList(anteproyecto);

        when(mockDepartmentHeadApi.buscarPorNombreOIdSinEvaluadores(nombre, id)).thenReturn(expectedAnteproyectos);

        // Ejecutar
        List<AnteproyectoDto> result = departmentHeadServiceFront.buscarAnteproyectos(nombre, id);

        // Verificar
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(2, result.get(0).getEvaluadores().size());
        assertTrue(result.get(0).getEvaluadores().contains("Evaluador1"));
        assertTrue(result.get(0).getEvaluadores().contains("Evaluador2"));
        verify(mockDepartmentHeadApi).buscarPorNombreOIdSinEvaluadores(nombre, id);
    }

    @Test
    void testConstructor() {
        // Verificar que el constructor asigna correctamente la dependencia
        assertNotNull(departmentHeadServiceFront);
    }
}
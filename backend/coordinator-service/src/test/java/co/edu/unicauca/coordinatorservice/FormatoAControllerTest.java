package co.edu.unicauca.coordinatorservice;


import co.edu.unicauca.coordinatorservice.controller.FormatoAController;
import co.edu.unicauca.coordinatorservice.entity.DocenteEmbeddable;
import co.edu.unicauca.coordinatorservice.entity.FormatoA;
import co.edu.unicauca.coordinatorservice.entity.TipoProyecto;
import co.edu.unicauca.coordinatorservice.infra.DTOS.EstadoFormatoA;
import co.edu.unicauca.coordinatorservice.repository.FormatoARepository;
import co.edu.unicauca.coordinatorservice.service.FormatoAService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


/**
 * Pruebas para los endpoints del FormatoAController
 */
@WebMvcTest(FormatoAController.class)
class FormatoAControllerTest {

    @MockitoBean
    private FormatoARepository formatoARepository;

    @Autowired
    private FormatoAService formatoAService;

    @Test
    void debeActualizarFormato() throws IOException {
        // Arrange: crear un formato de prueba
        FormatoA formatoExistente = new FormatoA();
        formatoExistente.setId(1L);
        formatoExistente.setNombreFormatoA("Formato_Prueba.pdf");
        formatoExistente.setEstadoFormatoA(EstadoFormatoA.valueOf("PENDIENTE"));
        formatoExistente.setFechaSubida(LocalDate.now());

        // Simular que el repositorio lo devuelve
        when(formatoARepository.findById(1L)).thenReturn(Optional.of(formatoExistente));

        // Act: ejecutar el método a probar
        FormatoA actualizado = formatoAService.actualizarFormato(
                1L,
                null,                         // archivo (puede ser null si no lo pruebas aquí)
                "Aprobado",                   // nuevo estado
                "Formato_Prueba.pdf",         // nombre del archivo
                "2025-11-02 14:33:00"         // hora actual como String
        );

        // Assert: verificar comportamiento y valores
        assertEquals("Aprobado", actualizado.getEstadoFormatoA());
        assertEquals("Formato_Prueba.pdf", actualizado.getNombreFormatoA());

        // Verificar que el repositorio guardó los cambios
        verify(formatoARepository, times(1)).save(formatoExistente);
    }

    @Test
    void debeLanzarErrorSiNoExisteFormato() {
        // Simular que el formato no existe
        when(formatoARepository.findById(99L)).thenReturn(Optional.empty());

        try {
            formatoAService.actualizarFormato(99L, null, "Rechazado", "Inexistente.pdf", "2025-11-02");
        } catch (IOException e) {
            fail("No debería lanzarse IOException en esta prueba: " + e.getMessage());
        } catch (RuntimeException e) {
            assertEquals("Formato no encontrado con ID: 99", e.getMessage());
        }

        verify(formatoARepository, never()).save(any());
    }
}

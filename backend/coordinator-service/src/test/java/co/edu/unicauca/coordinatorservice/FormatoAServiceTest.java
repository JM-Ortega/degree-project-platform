package co.edu.unicauca.coordinatorservice;

import co.edu.unicauca.coordinatorservice.entity.FormatoA;
import co.edu.unicauca.coordinatorservice.infra.DTOS.EstadoFormatoA;
import co.edu.unicauca.coordinatorservice.infra.DTOS.FormatoADTO;
import co.edu.unicauca.coordinatorservice.repository.FormatoARepository;
import co.edu.unicauca.coordinatorservice.service.FormatoAService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.mock.web.MockMultipartFile;
import java.io.IOException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FormatoAServiceTest {
    @Mock
    private FormatoARepository formatoARepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private FormatoAService formatoAService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        formatoAService = new FormatoAService(formatoARepository, rabbitTemplate);

        // Inyección directa (reflexión eliminada)
        try {
            var mainExchangeField = FormatoAService.class.getDeclaredField("mainExchange");
            mainExchangeField.setAccessible(true);
            mainExchangeField.set(formatoAService, "main.exchange");

            var routingField = FormatoAService.class.getDeclaredField("routingKeyFormatAApproved");
            routingField.setAccessible(true);
            routingField.set(formatoAService, "routing.key.formatAApproved");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    void actualizarFormato_deberiaActualizarYEnviarMensajeCuandoEsAceptado() throws IOException {
        // Arrange
        Long id = 1L;
        MockMultipartFile archivo = new MockMultipartFile(
                "file", "formatoA.pdf", "application/pdf", "contenido".getBytes()
        );

        FormatoA formatoExistente = new FormatoA();
        formatoExistente.setId(id);
        formatoExistente.setNroVersion(2);
        formatoExistente.setProyectoId(10L);
        formatoExistente.setEstadoFormatoA(EstadoFormatoA.PENDIENTE);

        when(formatoARepository.findById(id)).thenReturn(Optional.of(formatoExistente));
        when(formatoARepository.save(any(FormatoA.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        FormatoA resultado = formatoAService.actualizarFormato(
                id, archivo, "aceptado", "formatoA.pdf", "2024-05-15 12:00:00"
        );

        // Assert
        assertEquals("formatoA.pdf", resultado.getNombreFormatoA());
        assertEquals(EstadoFormatoA.APROBADO, resultado.getEstadoFormatoA());
        assertNotNull(resultado.getBlob());
        verify(formatoARepository).save(any(FormatoA.class));
        verify(rabbitTemplate).convertAndSend(eq("main.exchange"), eq("routing.key.formatAApproved"), any(FormatoADTO.class));
    }

    @Test
    void actualizarFormato_deberiaMarcarComoObservadoCuandoEsRechazado() throws IOException {
        // Arrange
        Long id = 1L;
        MockMultipartFile archivo = new MockMultipartFile(
                "file", "formatoA.pdf", "application/pdf", "contenido".getBytes()
        );

        FormatoA formatoExistente = new FormatoA();
        formatoExistente.setId(id);
        formatoExistente.setNroVersion(1);
        formatoExistente.setProyectoId(20L);
        formatoExistente.setEstadoFormatoA(EstadoFormatoA.PENDIENTE);

        when(formatoARepository.findById(id)).thenReturn(Optional.of(formatoExistente));
        when(formatoARepository.save(any(FormatoA.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        FormatoA resultado = formatoAService.actualizarFormato(
                id, archivo, "rechazado", "formatoA.pdf", "2024-05-15 12:00:00"
        );

        // Assert
        assertEquals(EstadoFormatoA.OBSERVADO, resultado.getEstadoFormatoA());
        verify(rabbitTemplate).convertAndSend(eq("main.exchange"), eq("routing.key.formatAApproved"), any(FormatoADTO.class));
    }

    @Test
    void actualizarFormato_deberiaLanzarExcepcionSiNoExisteFormato() {
        // Arrange
        when(formatoARepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                formatoAService.actualizarFormato(99L, null, "aceptado", "archivo.pdf", "2024-05-15 12:00:00")
        );
        assertTrue(ex.getMessage().contains("no encontrado"));
    }

    @Test
    void actualizarFormato_deberiaLanzarExcepcionSiSuperaTresVersiones() {
        // Arrange
        FormatoA formato = new FormatoA();
        formato.setNroVersion(4);
        when(formatoARepository.findById(anyLong())).thenReturn(Optional.of(formato));

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
                formatoAService.actualizarFormato(1L, null, "aceptado", "archivo.pdf", "2024-05-15 12:00:00")
        );
    }
}

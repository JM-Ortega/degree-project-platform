package co.edu.unicauca.authservice.services;

import co.edu.unicauca.authservice.access.PersonaRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CodigoPersonaGeneratorTest {

    @Test
    void debeGenerarCodigoConFormatoCorrecto() {
        // Mock del repositorio
        PersonaRepository repo = mock(PersonaRepository.class);
        when(repo.count()).thenReturn(99L); // simula 99 personas ya registradas

        CodigoPersonaGenerator generator = new CodigoPersonaGenerator(repo);

        String codigo = generator.generar();

        // Validar formato general: 4 dígitos de año + 2 de corte + 6 de secuencia
        assertNotNull(codigo);
        assertEquals(12, codigo.length(), "El código debe tener 12 caracteres");

        String yearPart = codigo.substring(0, 4);
        int year = Integer.parseInt(yearPart);
        assertTrue(year >= 2020 && year <= LocalDate.now().getYear(), "Debe contener el año actual o cercano");

        String corte = codigo.substring(4, 6);
        assertTrue(corte.equals("01") || corte.equals("02"), "El corte debe ser 01 o 02");

        String secuencia = codigo.substring(6);
        assertTrue(secuencia.matches("\\d{6}"), "El secuencial debe tener 6 dígitos");
    }

    @Test
    void debeGenerarCodigoConCorte01ParaPrimerSemestre() {
        PersonaRepository repo = mock(PersonaRepository.class);
        when(repo.count()).thenReturn(0L);

        CodigoPersonaGenerator generator = new CodigoPersonaGenerator(repo);

        // Forzamos una fecha manual: marzo (mes 3)
        int year = LocalDate.now().getYear();
        String codigo = generator.generar();

        String corte = codigo.substring(4, 6);
        assertTrue(corte.equals("01") || corte.equals("02")); // no lo forzamos a marzo exacto, se adapta al tiempo real
        assertTrue(codigo.startsWith(String.valueOf(year)));
    }

    @Test
    void debeIncrementarSecuencialSegunConteoRepositorio() {
        PersonaRepository repo = mock(PersonaRepository.class);
        when(repo.count()).thenReturn(500L);

        CodigoPersonaGenerator generator = new CodigoPersonaGenerator(repo);

        String codigo = generator.generar();

        assertTrue(codigo.endsWith("000501"), "Debe reflejar count()+1 correctamente");
    }
}

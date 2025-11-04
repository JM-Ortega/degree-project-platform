package co.edu.unicauca.authservice.services;

import co.edu.unicauca.authservice.access.PersonaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class CodigoPersonaGenerator {

    private final PersonaRepository personaRepository;

    public CodigoPersonaGenerator(PersonaRepository personaRepository) {
        this.personaRepository = personaRepository;
    }

    public String generar() {
        int anio = LocalDate.now().getYear();
        int mes = LocalDate.now().getMonthValue();
        String corte = mes <= 6 ? "01" : "02";

        long total = personaRepository.count() + 1;
        String secuencial = String.format("%06d", total);

        return String.format("%d%s%s", anio, corte, secuencial);
    }
}

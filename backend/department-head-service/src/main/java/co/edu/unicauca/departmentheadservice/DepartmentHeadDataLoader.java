package co.edu.unicauca.departmentheadservice;

import co.edu.unicauca.departmentheadservice.access.AnteproyectoRepository;
import co.edu.unicauca.departmentheadservice.access.DocenteRepository;
import co.edu.unicauca.departmentheadservice.entities.Anteproyecto;
import co.edu.unicauca.departmentheadservice.entities.Docente;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class DepartmentHeadDataLoader implements CommandLineRunner {

    private final AnteproyectoRepository anteproyectoRepository;
    private final DocenteRepository docenteRepository;

    public DepartmentHeadDataLoader(AnteproyectoRepository anteproyectoRepository,
                                    DocenteRepository docenteRepository) {
        this.anteproyectoRepository = anteproyectoRepository;
        this.docenteRepository = docenteRepository;
    }

    @Override
    public void run(String... args) {
        loadData();
    }

    private void loadData() {
        // --- Crear docentes de prueba ---
        Docente docente1 = new Docente("1", "Juan Pérez", "juan.perez@unicauca.edu.co");
        Docente docente2 = new Docente("2", "Ana Gómez", "ana.gomez@unicauca.edu.co");
        Docente docente3 = new Docente("3", "Carlos Ruiz", "carlos.ruiz@unicauca.edu.co");

        docenteRepository.saveAll(List.of(docente1, docente2, docente3));

        // --- Crear lista de anteproyectos ---
        List<Anteproyecto> anteproyectos = new ArrayList<>();

        // 15 sin evaluadores
        for (int i = 1; i <= 15; i++) {
            Anteproyecto ante = new Anteproyecto(
                    100L + i,                      // anteproyectoId
                    200L + i,                      // proyectoId
                    "Anteproyecto sin evaluadores " + i,
                    "Descripción del anteproyecto sin evaluadores " + i,
                    LocalDate.now(),
                    List.of(),                     // sin evaluadores
                    "estudiante" + i + "@unicauca.edu.co",
                    "director" + i + "@unicauca.edu.co",
                    "SISTEMAS"
            );
            anteproyectos.add(ante);
        }

        // 3 con dos evaluadores
        for (int i = 16; i <= 18; i++) {
            Anteproyecto ante = new Anteproyecto(
                    100L + i,
                    200L + i,
                    "Anteproyecto con 2 evaluadores " + i,
                    "Descripción del anteproyecto con 2 evaluadores " + i,
                    LocalDate.now(),
                    List.of(docente1, docente2),
                    "estudiante" + i + "@unicauca.edu.co",
                    "director" + i + "@unicauca.edu.co",
                    "SISTEMAS"
            );
            anteproyectos.add(ante);
        }

        // 2 con un evaluador
        for (int i = 19; i <= 20; i++) {
            Anteproyecto ante = new Anteproyecto(
                    100L + i,
                    200L + i,
                    "Anteproyecto con 1 evaluador " + i,
                    "Descripción del anteproyecto con 1 evaluador " + i,
                    LocalDate.now(),
                    List.of(docente3),
                    "estudiante" + i + "@unicauca.edu.co",
                    "director" + i + "@unicauca.edu.co",
                    "SISTEMAS"
            );
            anteproyectos.add(ante);
        }

        // --- Guardar en la base de datos ---
        anteproyectoRepository.saveAll(anteproyectos);

        System.out.println("✅ Datos iniciales cargados correctamente en DepartmentHeadService");
    }
}

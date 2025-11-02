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

    public DepartmentHeadDataLoader(AnteproyectoRepository anteproyectoRepository, DocenteRepository docenteRepository) {
        this.anteproyectoRepository = anteproyectoRepository;
        this.docenteRepository = docenteRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        loadData();
    }

    public void loadData() {
        // Crear algunos docentes (estos podrían ser obtenidos desde la base de datos o creados directamente)
        Docente docente1 = new Docente("1", "Juan Pérez","juan.perez@unicauca.edu.co");
        Docente docente2 = new Docente("2", "Ana Gómez","ana.gomez@unicauca.edu.co");
        Docente docente3 = new Docente("3","Carlos Ruiz" ,"carlos.ruiz@unicauca.edu.co");

        docenteRepository.saveAll(List.of(docente1, docente2, docente3));

        // Lista para almacenar los Anteproyectos
        List<Anteproyecto> anteproyectos = new ArrayList<>();

        // Crear 15 Anteproyectos sin evaluadores
        for (int i = 1; i <= 15; i++) {
            Anteproyecto anteproyecto = new Anteproyecto(
                    "Anteproyecto sin evaluadores " + i,
                    "Descripción del anteproyecto sin evaluadores " + i,
                    LocalDate.now(),
                    List.of() // Sin evaluadores
            );
            anteproyectos.add(anteproyecto);
        }

        // Crear 3 Anteproyectos con 2 evaluadores
        for (int i = 16; i <= 18; i++) {
            Anteproyecto anteproyecto = new Anteproyecto(
                    "Anteproyecto con 2 evaluadores " + i,
                    "Descripción del anteproyecto con 2 evaluadores " + i,
                    LocalDate.now(),
                    List.of(docente1, docente2) // 2 evaluadores
            );
            anteproyectos.add(anteproyecto);
        }

        // Crear 2 Anteproyectos con 1 evaluador
        for (int i = 19; i <= 20; i++) {
            Anteproyecto anteproyecto = new Anteproyecto(
                    "Anteproyecto con 1 evaluador " + i,
                    "Descripción del anteproyecto con 1 evaluador " + i,
                    LocalDate.now(),
                    List.of(docente1) // 1 evaluador
            );
            anteproyectos.add(anteproyecto);
        }

        // Guardar los Anteproyectos en la base de datos
        anteproyectoRepository.saveAll(anteproyectos);

        System.out.println("Datos de anteproyectos cargados correctamente");
    }
}

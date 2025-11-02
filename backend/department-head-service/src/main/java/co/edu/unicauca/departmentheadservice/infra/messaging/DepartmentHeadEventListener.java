package co.edu.unicauca.departmentheadservice.infra.messaging;

import co.edu.unicauca.shared.contracts.events.academic.AnteproyectoSinEvaluadoresEvent;
import co.edu.unicauca.shared.contracts.events.auth.UserCreatedEvent;
import co.edu.unicauca.departmentheadservice.entities.Anteproyecto;
import co.edu.unicauca.departmentheadservice.entities.Docente;
import co.edu.unicauca.departmentheadservice.access.AnteproyectoRepository;
import co.edu.unicauca.departmentheadservice.access.DocenteRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DepartmentHeadEventListener {

    private final AnteproyectoRepository anteproyectoRepository;
    private final DocenteRepository docenteRepository; // Repositorio para buscar los docentes

    // Inyección de repositorios
    public DepartmentHeadEventListener(AnteproyectoRepository anteproyectoRepository, DocenteRepository docenteRepository) {
        this.anteproyectoRepository = anteproyectoRepository;
        this.docenteRepository = docenteRepository;
    }

    /**
     * Escuchar el evento de creación de usuario (docentes) y transformarlo en una entidad Docente.
     *
     * @param event Evento recibido con los datos del usuario (docente) creado.
     */
    @RabbitListener(queues = "${messaging.queues.department}")
    public void handleUserCreatedEvent(UserCreatedEvent event) {
        // Log para ver el evento recibido
        System.out.println("Evento recibido: Docente creado: " + event.nombre());

        try {
            // Transformar el evento en una entidad Docente
            Docente docente = new Docente(event.personaId(), event.nombre(), event.email());

            // Guardar el docente en la base de datos
            docenteRepository.save(docente);

            System.out.println("Docente guardado en la base de datos: " + docente.getNombre());
        } catch (Exception e) {
            System.err.println("Error al guardar el docente: " + e.getMessage());
        }
    }

    /**
     * Escuchar el evento de creación de anteproyecto SIN evaluadores asignados
     * y guardarlo en la base de datos con evaluadores vacíos.
     *
     * @param event Evento recibido con los datos del anteproyecto.
     */
    @RabbitListener(queues = "${messaging.queues.department}")
    public void handleAnteproyectoSinEvaluadoresEvent(AnteproyectoSinEvaluadoresEvent event) {
        // Log para ver el evento recibido
        System.out.println("Evento recibido: Anteproyecto creado (sin evaluadores): " + event.titulo());

        try {
            // Aquí no tenemos evaluadores al principio, asignamos una lista vacía
            List<Docente> evaluadores = List.of(); // No hay evaluadores al principio

            // Crear una entidad Anteproyecto con evaluadores vacíos
            Anteproyecto anteproyecto = new Anteproyecto(
                    event.titulo(),          // Título del anteproyecto
                    event.descripcion(),     // Descripción del anteproyecto
                    event.fechaCreacion(),   // Fecha de creación
                    evaluadores              // Lista de evaluadores (vacía por ahora)
            );

            // Guardar el anteproyecto en la base de datos
            anteproyectoRepository.save(anteproyecto);

            System.out.println("Anteproyecto guardado en la base de datos: " + anteproyecto.getTitulo());
        } catch (Exception e) {
            System.err.println("Error al guardar el anteproyecto: " + e.getMessage());
        }
    }
}

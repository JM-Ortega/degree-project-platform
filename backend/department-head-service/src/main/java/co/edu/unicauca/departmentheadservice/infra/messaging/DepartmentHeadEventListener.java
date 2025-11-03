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
    private final DocenteRepository docenteRepository;

    public DepartmentHeadEventListener(AnteproyectoRepository anteproyectoRepository, DocenteRepository docenteRepository) {
        this.anteproyectoRepository = anteproyectoRepository;
        this.docenteRepository = docenteRepository;
    }

    /**
     * Procesa eventos de creación de usuarios y almacena únicamente los registros de docentes.
     * Evalúa la lista de roles para identificar usuarios con rol de docente.
     *
     * @param event Evento recibido con los datos del usuario creado
     */
    @RabbitListener(queues = "${messaging.queues.department}")
    public void handleUserCreatedEvent(UserCreatedEvent event) {
        System.out.println("Evento recibido: Usuario creado - Nombre: " + event.nombre() + ", Roles: " +
                          event.roles().stream().map(Enum::name).reduce((a, b) -> a + ", " + b).orElse("Ninguno"));

        // Validar si el usuario tiene rol de docente
        boolean esDocente = event.roles().stream()
                .anyMatch(rol -> rol.name().equalsIgnoreCase("DOCENTE"));

        if (esDocente) {
            try {
                Docente docente = new Docente(event.personaId(), event.nombre(), event.email());
                docenteRepository.save(docente);
                System.out.println("Docente almacenado en base de datos: " + docente.getNombre());
            } catch (Exception e) {
                System.err.println("Error durante el almacenamiento del docente: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Usuario descartado - No contiene rol docente. Roles asignados: " +
                             event.roles().stream().map(Enum::name).reduce((a, b) -> a + ", " + b).orElse("Ninguno"));
        }
    }

    /**
     * Procesa eventos de creación de anteproyectos sin evaluadores asignados.
     * Almacena el anteproyecto con una lista vacía de evaluadores para su posterior asignación.
     *
     * @param event Evento recibido con los datos del anteproyecto creado
     */
    @RabbitListener(queues = "${messaging.queues.department}")
    public void handleAnteproyectoSinEvaluadoresEvent(AnteproyectoSinEvaluadoresEvent event) {
        System.out.println("Evento recibido: Anteproyecto creado (sin evaluadores): " + event.titulo());

        try {
            // Inicializar lista vacía de evaluadores para asignación posterior
            List<Docente> evaluadores = List.of();

            Anteproyecto anteproyecto = new Anteproyecto(
                    event.titulo(),
                    event.descripcion(),
                    event.fechaCreacion(),
                    evaluadores
            );

            anteproyectoRepository.save(anteproyecto);
            System.out.println("Anteproyecto almacenado en base de datos: " + anteproyecto.getTitulo());
        } catch (Exception e) {
            System.err.println("Error durante el almacenamiento del anteproyecto: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
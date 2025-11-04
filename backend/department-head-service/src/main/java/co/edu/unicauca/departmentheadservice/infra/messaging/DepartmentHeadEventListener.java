package co.edu.unicauca.departmentheadservice.infra.messaging;

import co.edu.unicauca.departmentheadservice.access.AnteproyectoRepository;
import co.edu.unicauca.departmentheadservice.access.DocenteRepository;
import co.edu.unicauca.departmentheadservice.entities.Anteproyecto;
import co.edu.unicauca.departmentheadservice.entities.Docente;
import co.edu.unicauca.shared.contracts.events.academic.AnteproyectoSinEvaluadoresEvent;
import co.edu.unicauca.shared.contracts.events.auth.UserCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class DepartmentHeadEventListener {

    private final AnteproyectoRepository anteproyectoRepository;
    private final DocenteRepository docenteRepository;

    public DepartmentHeadEventListener(AnteproyectoRepository anteproyectoRepository,
                                       DocenteRepository docenteRepository) {
        this.anteproyectoRepository = anteproyectoRepository;
        this.docenteRepository = docenteRepository;
    }

    /**
     * Procesa eventos de creación de usuarios y almacena únicamente los registros de docentes.
     */
    @RabbitListener(queues = "${messaging.queues.department}")
    public void handleUserCreatedEvent(UserCreatedEvent event) {
        log.info("Evento recibido: Usuario creado - Nombre: {}, Roles: {}",
                event.nombre(),
                event.roles().stream().map(Enum::name).reduce((a, b) -> a + ", " + b).orElse("Ninguno"));

        boolean esDocente = event.roles().stream().anyMatch(rol -> rol.name().equalsIgnoreCase("DOCENTE"));
        if (!esDocente) {
            log.debug("Usuario descartado (no docente): {}", event.email());
            return;
        }

        try {
            Docente docente = new Docente(event.personaId(), event.nombre(), event.email());
            docenteRepository.save(docente);
            log.info("Docente almacenado: {}", docente.getNombre());
        } catch (Exception e) {
            log.error("Error durante el almacenamiento del docente {}: {}", event.email(), e.getMessage(), e);
            throw e; // permite reintento/DLQ
        }
    }



    /**
     * Procesa eventos de creación de anteproyectos sin evaluadores asignados.
     * Idempotente por anteproyectoId.
     */
    @RabbitListener(queues = "${messaging.queues.department}")
    public void handleAnteproyectoSinEvaluadoresEvent(AnteproyectoSinEvaluadoresEvent event) {
        log.info("[DeptHead] Anteproyecto creado (sin evaluadores). projId={}, anteId={}, titulo={}",
                event.proyectoId(), event.anteproyectoId(), event.titulo());

        try {
            // 1) Idempotencia: si ya existe, salimos
            if (anteproyectoRepository.existsByAnteproyectoId(event.anteproyectoId())) {
                log.debug("[DeptHead] Ignorado: anteproyectoId={} ya registrado", event.anteproyectoId());
                return;
            }

            // 2) (opcional) filtrar por departamento si aplica
            // if (!"SISTEMAS".equalsIgnoreCase(event.departamento())) return;

            // 3) crear con evaluadores vacíos
            List<Docente> evaluadores = List.of();

            // Constructor recomendado en la entidad adaptada:
            Anteproyecto ante = new Anteproyecto(
                    event.anteproyectoId(),       // id externo único
                    event.proyectoId(),           // trazabilidad
                    event.titulo(),
                    event.descripcion(),
                    event.fechaCreacion(),
                    evaluadores,
                    event.estudianteCorreo(),     // opcional según tu entidad
                    event.directorCorreo(),       // opcional según tu entidad
                    event.departamento()          // opcional
            );

            anteproyectoRepository.save(ante);
            log.info("[DeptHead] Anteproyecto almacenado. anteId={}, titulo={}",
                    event.anteproyectoId(), event.titulo());

        } catch (Exception e) {
            log.error("[DeptHead] Error guardando anteproyecto projId={} anteId={}: {}",
                    event.proyectoId(), event.anteproyectoId(), e.getMessage(), e);
            throw e; // deja que falle para reintentos/DLQ
        }
    }
}

package co.edu.unicauca.academicprojectservice.Controller;

import co.edu.unicauca.academicprojectservice.Entity.Coordinador;
import co.edu.unicauca.academicprojectservice.Entity.Docente;
import co.edu.unicauca.academicprojectservice.Entity.Estudiante;
import co.edu.unicauca.academicprojectservice.Entity.JefeDeDepartamento;
import co.edu.unicauca.academicprojectservice.Repository.CoordinadorRepository;
import co.edu.unicauca.academicprojectservice.Repository.DocenteRepository;
import co.edu.unicauca.academicprojectservice.Repository.EstudianteRepository;
import co.edu.unicauca.academicprojectservice.Repository.JefeDeDepartamentoRepository;
import co.edu.unicauca.academicprojectservice.infra.dto.UserDto;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public class UsuarioListener {
    private final DocenteRepository docenteRepository;
    private final EstudianteRepository estudianteRepository;
    private final CoordinadorRepository coordinadorRepository;
    private final JefeDeDepartamentoRepository jefeDeDepartamentoRepository;

    public UsuarioListener(DocenteRepository docenteRepository, EstudianteRepository estudianteRepository, CoordinadorRepository coordinadorRepository, JefeDeDepartamentoRepository jefeDeDepartamentoRepository) {
        this.docenteRepository = docenteRepository;
        this.estudianteRepository = estudianteRepository;
        this.coordinadorRepository = coordinadorRepository;
        this.jefeDeDepartamentoRepository = jefeDeDepartamentoRepository;
    }

    /**
     * Escucha los mensajes enviados cuando se crea o actualiza un usuario en otro microservicio.
     * Se determina si es Docente o Estudiante según el campo "rol" del DTO.
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${messaging.queues.project}", durable = "true"),
            exchange = @Exchange(value = "${messaging.exchange.main}", type = "topic"),
            key = "${messaging.routing.userCreated}"
    ))
    @Transactional
    public void recibirUsuario(UserDto dto) {
        System.out.println("[RabbitMQ] Mensaje recibido en UserListener: " + dto.getCorreo());

        if (dto.getRol() == null) {
            System.err.println("[RabbitMQ] Rol no especificado en el mensaje recibido. No se puede procesar.");
            return;
        }

        String rol = dto.getRol().trim().toUpperCase();

        switch (rol) {
            case "DOCENTE":
                procesarDocente(dto);
                break;

            case "ESTUDIANTE":
                procesarEstudiante(dto);
                break;

            case "COORDINADOR":
                procesarCoordinador(dto);
                break;

            case "JEFE_DE_DEPARTAMENTO":
                procesarJefeDepartamento(dto);
                break;

            default:
                System.err.println("[RabbitMQ] Rol no reconocido: " + rol);
                break;
        }
    }

    private void procesarJefeDepartamento(UserDto dto) {
        Optional<JefeDeDepartamento> existente = jefeDeDepartamentoRepository.findByCorreo(dto.getCorreo());
        JefeDeDepartamento jefe = existente.orElse(new JefeDeDepartamento());

        jefe.setCorreo(dto.getCorreo());
        jefe.setCelular(dto.getCelular());

        jefeDeDepartamentoRepository.save(jefe);
        System.out.println("[UserListener] Jefe de Departamento guardado/actualizado: " + dto.getCorreo());
    }

    private void procesarCoordinador(UserDto dto) {
        Optional<Coordinador> existente = coordinadorRepository.findByCorreo(dto.getCorreo());
        Coordinador coordinador = existente.orElse(new Coordinador());

        coordinador.setCorreo(dto.getCorreo());
        coordinador.setCelular(dto.getCelular());

        coordinadorRepository.save(coordinador);
        System.out.println("[UserListener] Coordinador guardado/actualizado: " + dto.getCorreo());
    }

    private void procesarDocente(UserDto dto) {
        Optional<Docente> existente = docenteRepository.findByCorreo(dto.getCorreo());
        Docente docente = existente.orElse(new Docente());

        docente.setNombres(dto.getNombres());
        docente.setApellidos(dto.getApellidos());
        docente.setCorreo(dto.getCorreo());
        docente.setCelular(dto.getCelular());
        docente.setDepartamento(dto.getDepartamento());

        docenteRepository.save(docente);
        System.out.println("[UserListener] Docente guardado/actualizado: " + docente.getNombres() + " " + docente.getApellidos());
    }

    private void procesarEstudiante(UserDto dto) {
        Optional<Estudiante> existente = estudianteRepository.findByCorreoIgnoreCase(dto.getCorreo());
        Estudiante estudiante = existente.orElse(new Estudiante());

        estudiante.setCodigoEstudiante(dto.getCodigo());
        estudiante.setNombres(dto.getNombres());
        estudiante.setApellidos(dto.getApellidos());
        estudiante.setCorreo(dto.getCorreo());
        estudiante.setCelular(dto.getCelular());
        estudiante.setPrograma(dto.getPrograma());

        estudianteRepository.save(estudiante);
        System.out.println("[UserListener] Estudiante guardado/actualizado: " + estudiante.getNombres() + " " + estudiante.getApellidos());
    }
}

package co.edu.unicauca.academicprojectservice.Controller;

import co.edu.unicauca.academicprojectservice.Entity.EstadoFormatoA;
import co.edu.unicauca.academicprojectservice.Entity.FormatoA;
import co.edu.unicauca.academicprojectservice.Repository.DocenteRepository;
import co.edu.unicauca.academicprojectservice.Repository.EstudianteRepository;
import co.edu.unicauca.academicprojectservice.Repository.FormatoARepository;
import co.edu.unicauca.academicprojectservice.infra.DTOs.FormatoADTOSend;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class FormatoAListener {

    private final FormatoARepository formatoARepository;
    private final DocenteRepository docenteRepository;
    private final EstudianteRepository estudianteRepository;

    public FormatoAListener(FormatoARepository formatoARepository,
                            DocenteRepository docenteRepository,
                            EstudianteRepository estudianteRepository) {
        this.formatoARepository = formatoARepository;
        this.docenteRepository = docenteRepository;
        this.estudianteRepository = estudianteRepository;
    }

    /**
     * Escucha mensajes relacionados con FormatoA o Proyectos provenientes del exchange principal.
     * Los eventos llegan desde otros microservicios (p. ej., coordinator-service o project-service)
     * a través de la cola del servicio académico.
     */
    //@RabbitListener(queues = "${messaging.queues.project}")
    @Transactional
    public void handleFormatoAEvent(FormatoADTOSend dto) {
        System.out.println("📩 [RabbitMQ] Mensaje recibido (FormatoA): " + dto.getNombreFormatoA());

        Optional<FormatoA> existingFormato = formatoARepository.findByProyectoId(dto.getProyectoId());
        FormatoA formato = existingFormato.orElse(new FormatoA());

        formato.setNroVersion(dto.getNroVersion());
        formato.setNombreFormato(dto.getNombreFormatoA());
        formato.setFechaCreacion(dto.getFechaSubida());
        formato.setBlob(dto.getBlob());
        formato.setEstado(EstadoFormatoA.valueOf(dto.getEstado().toString()));

        formatoARepository.save(formato);

        System.out.println("[AcademicProjectService] FormatoA actualizado/creado: "
                + formato.getNombreFormato() + " (versión " + formato.getNroVersion() + ")");
    }

    //No se tiene un metodo para actualizar el proyecto con evaluadores, porque no se implemento esa asignación
}
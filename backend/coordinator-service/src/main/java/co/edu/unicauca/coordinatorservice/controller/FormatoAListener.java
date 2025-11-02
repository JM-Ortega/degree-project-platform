package co.edu.unicauca.coordinatorservice.controller;

import co.edu.unicauca.coordinatorservice.entity.DocenteEmbeddable;
import co.edu.unicauca.coordinatorservice.infra.DTOS.EstadoFormatoA;
import co.edu.unicauca.coordinatorservice.entity.EstadoProyecto;
import co.edu.unicauca.coordinatorservice.entity.FormatoA;
import co.edu.unicauca.coordinatorservice.infra.DTOS.EstudianteDTO;
import co.edu.unicauca.coordinatorservice.infra.DTOS.FormatoADTO;
import co.edu.unicauca.coordinatorservice.infra.DTOS.ProyectoDTO;
import co.edu.unicauca.coordinatorservice.repository.FormatoARepository;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class FormatoAListener {
    private final FormatoARepository formatoARepository;

    public FormatoAListener(FormatoARepository formatoARepository) {
        this.formatoARepository = formatoARepository;
    }

    /**
     * Escucha mensajes provenientes de la cola del coordinador.
     * Estos mensajes llegan desde el microservicio de la fuente de la verdad
     * cuando se crea o actualiza un FormatoA o un Proyecto.
     */
    //@RabbitListener(queues = "${messaging.queues.project}")
    @Transactional
    public void handleFormatoAEvent(FormatoADTO dto) {
        System.out.println("📩 [RabbitMQ] Mensaje recibido en CoordinatorService: " + dto.getNombreFormatoA());

        // Buscar si ya existe el FormatoA en la base de datos local
        Optional<FormatoA> existingFormato = formatoARepository.findByProyectoId(dto.getProyectoId());

        FormatoA formato = existingFormato.orElse(new FormatoA());

        // Actualizar campos con la información nueva
        formato.setProyectoId(dto.getProyectoId());
        formato.setNroVersion(dto.getNroVersion());
        formato.setFechaSubida(dto.getFechaSubida());
        formato.setBlob(dto.getBlob());
        formato.setEstadoFormatoA(EstadoFormatoA.valueOf(dto.getEstado().toString()));

        formatoARepository.save(formato);

        System.out.println("[CoordinatorService] FormatoA guardado/actualizado correctamente: " + formato.getNombreProyecto()+ " Version: "+ formato.getNroVersion());
    }

    @Transactional
    public void recibirProyecto(ProyectoDTO dto) {
        System.out.println("📩 [RabbitMQ] Mensaje recibido en CoordinatorService: " + dto.getTitulo());

        // Buscar si ya existe el FormatoA en la base de datos local
        Optional<FormatoA> existingFormato = formatoARepository.findByProyectoId(dto.getId());

        FormatoA formato = existingFormato.orElse(new FormatoA());

        // Actualizar campos con la información nueva
        List<String> emailEstudiantes = new ArrayList<>();
        for(EstudianteDTO e : dto.getEstudiantes()){
            if (e.getUsuarioDTO() != null && e.getUsuarioDTO().getEmail() != null) {
                emailEstudiantes.add(e.getUsuarioDTO().getEmail());
            }
        }
        formato.setEstudiantesEmail(emailEstudiantes);

        DocenteEmbeddable director = new DocenteEmbeddable();
        director.setNombres(dto.getDirector().getNombres());
        director.setApellidos(dto.getDirector().getApellidos());
        director.setEmail(dto.getDirector().getUsuarioDTO().getEmail());
        formato.setDirector(director);

        if(dto.getCodirector() != null){
            DocenteEmbeddable coodirector = new DocenteEmbeddable();
            coodirector.setNombres(dto.getDirector().getNombres());
            coodirector.setApellidos(dto.getDirector().getApellidos());
            coodirector.setEmail(dto.getDirector().getUsuarioDTO().getEmail());
            formato.setDirector(coodirector);
        }

        formato.setEstadoProyecto(EstadoProyecto.valueOf(dto.getEstado().toString()));

        formato.setNombreProyecto(dto.getTitulo());

        formatoARepository.save(formato);

        System.out.println("[CoordinatorService] FormatoA guardado/actualizado correctamente: " + formato.getNombreProyecto()+ " Version: "+ formato.getNroVersion());
    }
}

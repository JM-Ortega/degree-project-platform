package co.edu.unicauca.academicprojectservice.Service;

import co.edu.unicauca.academicprojectservice.Entity.*;
import co.edu.unicauca.academicprojectservice.Repository.*;
import co.edu.unicauca.academicprojectservice.infra.DTOs.DocenteDTOSend;
import co.edu.unicauca.academicprojectservice.infra.DTOs.EstudianteDTOSend;
import co.edu.unicauca.academicprojectservice.infra.DTOs.FormatoADTOSend;
import co.edu.unicauca.academicprojectservice.infra.DTOs.ProyectoDTOSend;
import co.edu.unicauca.academicprojectservice.infra.dto.AnteproyectoDTO;
import co.edu.unicauca.academicprojectservice.infra.dto.FormatoADTO;
import co.edu.unicauca.academicprojectservice.infra.dto.ProyectoDTO;
import co.edu.unicauca.academicprojectservice.infra.dto.ProyectoInfoDTO;
import co.edu.unicauca.shared.contracts.events.academic.AnteproyectoSinEvaluadoresEvent;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProyectoService {
    @Autowired
    private ProyectoRepository proyectoRepository;
    @Autowired
    private EstudianteRepository estudianteRepository;
    @Autowired
    private DocenteRepository docenteRepository;
    @Autowired
    private FormatoARepository formatoARepository;
    @Autowired
    private AnteproyectoRepository anteproyectoRepository;
    @Autowired
    private RabbitTemplate  rabbitTemplate;

    @Value("${messaging.exchange.main}")
    private String mainExchange;

    @Value("${messaging.routing.projectCreated}")
    private String routingKeyProjectCreated;

    @Value("${messaging.routing.projectUpdated}")
    private String routingKeyProjectUpdated;

    public List<ProyectoInfoDTO> listarInfoPorCorreoDocente(String correo, String filtro) {
        Docente docente = docenteRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Docente no encontrado con correo: " + correo));

        return proyectoRepository.listarInfoPorDocente(docente.getId(), filtro);
    }

    @Transactional
    public void crearProyectoConArchivos(ProyectoDTO dto) {

        // ===== Crear y poblar la entidad Proyecto =====
        Proyecto proyecto = new Proyecto();
        proyecto.setTitulo(dto.getTitulo());
        proyecto.setTipoProyecto(dto.getTipoProyecto());
        proyecto.setEstadoProyecto(EstadoProyecto.EN_TRAMITE);

        // ===== Asociar estudiante =====
        Estudiante estudiante = estudianteRepository.findByCorreoIgnoreCase(dto.getEstudiante())
                .orElseThrow(() -> new IllegalArgumentException("No existe un estudiante con ese correo"));
        proyecto.setEstudiantes(List.of(estudiante));

        // ===== Asociar director =====
        Docente docente = docenteRepository.findByCorreo(dto.getDirector())
                .orElseThrow(() -> new IllegalArgumentException("No existe un docente con ese correo"));
        proyecto.setDirector(docente);

        // ===== Asociar Formato A (si lo hay) =====
        FormatoA formatoA = dto.getFormatoA();
        if (formatoA != null) {
            formatoA.setProyecto(proyecto);
            formatoA.setEstado(dto.getFormatoA().getEstado());
            formatoA.setNombreFormato(dto.getFormatoA().getNombreFormato());
            formatoA.setFechaCreacion(dto.getFormatoA().getFechaCreacion());
            formatoA.setNroVersion(dto.getFormatoA().getNroVersion());
            proyecto.addFormato(formatoA);
        }

        // ===== Asociar Carta Laboral (si la hay) =====
        CartaLaboral cartaLaboral = dto.getCartaLaboral();
        if (cartaLaboral != null) {
            cartaLaboral.setProyecto(proyecto);
            cartaLaboral.setNombreCartaLaboral(dto.getCartaLaboral().getNombreCartaLaboral());
            cartaLaboral.setFechaCreacion(dto.getCartaLaboral().getFechaCreacion());
            proyecto.setCartaLaboral(cartaLaboral);
        }

        // ===== Guardar el proyecto y obtener su ID =====
        Proyecto proyectoGuardado = proyectoRepository.save(proyecto);
        Long proyectoId = proyectoGuardado.getId();

        // =====================================================
        // Construcción del DTO que se enviará por RabbitMQ
        // =====================================================
        ProyectoDTOSend pDtoSend = new ProyectoDTOSend();
        pDtoSend.setId(proyectoId);
        pDtoSend.setTitulo(proyectoGuardado.getTitulo());
        pDtoSend.setTipoProyecto(proyectoGuardado.getTipoProyecto());
        pDtoSend.setEstado(proyectoGuardado.getEstadoProyecto());

        // ======= Estudiantes DTO =======
        List<EstudianteDTOSend> estudiantes = new ArrayList<>();
        EstudianteDTOSend estDto = new EstudianteDTOSend();
        estDto.setId(proyectoGuardado.getEstudiantes().get(0).getId());
        estDto.setPrograma(estudiante.getPrograma());
        estDto.setEmail(estudiante.getCorreo());
        estDto.setNombres(estudiante.getNombres());
        estDto.setApellidos(estudiante.getApellidos());
        estDto.setCelular(estudiante.getCelular());

        // Referencia inversa de trabajos (puede ser omitida si genera bucle en serialización)
        estDto.setTrabajos(List.of(pDtoSend));
        estudiantes.add(estDto);
        pDtoSend.setEstudiantes(estudiantes);

        // ======= Director DTO =======
        DocenteDTOSend docDto = new DocenteDTOSend();
        docDto.setId(proyectoGuardado.getDirector().getId());
        docDto.setDepartamento(docente.getDepartamento());
        docDto.setEmail(docente.getCorreo());
        docDto.setNombres(docente.getNombres());
        docDto.setApellidos(docente.getApellidos());
        docDto.setCelular(docente.getCelular());

        // Referencia inversa
        docDto.setTrabajosComoDirector(List.of(pDtoSend));
        docDto.setTrabajosComoCodirector(null);

        pDtoSend.setDirector(docDto);
        pDtoSend.setCodirector(null); // En caso de no tener

        // ======= Formato A DTO =======
        if (formatoA != null) {
            FormatoADTOSend formatoSend = new FormatoADTOSend();
            formatoSend.setId(formatoA.getId());
            formatoSend.setProyectoId(proyectoId);
            formatoSend.setNroVersion(formatoA.getNroVersion());
            formatoSend.setNombreFormatoA(formatoA.getNombreFormato());
            formatoSend.setFechaSubida(formatoA.getFechaCreacion());
            formatoSend.setBlob(formatoA.getBlob());
            formatoSend.setEstado(formatoA.getEstado());
            pDtoSend.setFormatoA(formatoSend);
        } else {
            pDtoSend.setFormatoA(null);
        }

        // No hay anteproyecto al crear
        pDtoSend.setAnteproyecto(null);

        // ======= Envío del mensaje =======
        rabbitTemplate.convertAndSend(mainExchange, routingKeyProjectCreated, pDtoSend);

        ProyectoService.log.info("[RabbitMQ] Proyecto creado enviado a la cola: " + routingKeyProjectCreated +
                " con ID: " + proyectoId);
    }


    public EstadoProyecto enforceAutoCancelIfNeeded(long proyectoId) {
        int observados = formatoARepository.countByProyectoIdAndEstado(proyectoId, EstadoFormatoA.OBSERVADO);
        if (observados >= 3) {
            proyectoRepository.actualizarEstadoProyecto(proyectoId, EstadoProyecto.RECHAZADO);
        }
        String est = proyectoRepository.getEstadoProyecto(proyectoId);
        return est == null ? EstadoProyecto.EN_TRAMITE : EstadoProyecto.valueOf(est);
    }

    public int getMaxVersionFormatoA(Long proyectoId) {
        Integer maxVersion = formatoARepository.findMaxVersionByProyectoId(proyectoId);
        return maxVersion != null ? maxVersion : 0;
    }

    private FormatoA getUltimoFormatoA(Long proyectoId) {
        List<FormatoA> resultados = formatoARepository.findUltimoFormatoA(proyectoId, PageRequest.of(0, 1));
        return resultados.isEmpty() ? null : resultados.get(0);
    }

    public boolean canResubmit(Long proyectoId) {
        String estado = proyectoRepository.getEstadoProyecto(proyectoId);
        if (estado == null || !estado.equalsIgnoreCase(EstadoProyecto.EN_TRAMITE.name())) {
            return false;
        }
        int maxVersion = getMaxVersionFormatoA(proyectoId);
        if (maxVersion == 0) return true;
        if (maxVersion >= 3) return false;

        FormatoA ultimo = getUltimoFormatoA(proyectoId);
        if (ultimo == null) return true;

        return ultimo.getEstado() == EstadoFormatoA.OBSERVADO;
    }

    public boolean tieneObservaciones(Long proyectoId) {
        FormatoA ultimo = getUltimoFormatoA(proyectoId);
        return ultimo != null && ultimo.getEstado() == EstadoFormatoA.OBSERVADO;
    }

    public boolean existeProyecto(Long proyectoId) {
        return proyectoRepository.existsById(proyectoId);
    }

    public String estadoProyecto(Long proyectoId) {
        return proyectoRepository.getEstadoProyecto(proyectoId);
    }

    public boolean insertarFormatoAEnProyecto(Long proyectoId, FormatoA formatoA) {
        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new IllegalArgumentException("No existe un proyecto con id " + proyectoId));

        int ultimaVersion = 0;
        if (proyecto.getFormatosA() != null && !proyecto.getFormatosA().isEmpty()) {
            ultimaVersion = proyecto.getFormatosA().stream()
                    .mapToInt(FormatoA::getNroVersion)
                    .max()
                    .orElse(0);
        }

        formatoA.setNroVersion(ultimaVersion + 1);
        formatoA.setFechaCreacion(LocalDate.now());
        formatoA.setProyecto(proyecto);

        proyecto.addFormato(formatoA);

        Proyecto proyectoGuardado = proyectoRepository.save(proyecto);

        FormatoA formatoGuardado = proyectoGuardado.getFormatosA().stream()
                .filter(f -> f.getNroVersion() == formatoA.getNroVersion())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No se encontró el formato recién guardado"));

        // =====================================================
        // Construcción del DTO que se enviará por RabbitMQ
        // =====================================================
        ProyectoDTOSend pDtoSend = new ProyectoDTOSend();
        pDtoSend.setId(proyectoGuardado.getId());
        pDtoSend.setTitulo(proyectoGuardado.getTitulo());
        pDtoSend.setTipoProyecto(proyectoGuardado.getTipoProyecto());
        pDtoSend.setEstado(proyectoGuardado.getEstadoProyecto());

        // ======= Estudiantes DTO =======
        List<EstudianteDTOSend> estudiantes = new ArrayList<>();
        for (Estudiante est : proyectoGuardado.getEstudiantes()) {
            EstudianteDTOSend estDto = new EstudianteDTOSend();
            estDto.setId(est.getId());
            estDto.setPrograma(est.getPrograma());
            estDto.setEmail(est.getCorreo());
            estDto.setNombres(est.getNombres());
            estDto.setApellidos(est.getApellidos());
            estDto.setCelular(est.getCelular());
            estudiantes.add(estDto);
        }
        pDtoSend.setEstudiantes(estudiantes);
        // ======= Director DTO =======
        Docente director = proyectoGuardado.getDirector();
        if (director != null) {
            DocenteDTOSend docDto = new DocenteDTOSend();
            docDto.setId(director.getId());
            docDto.setDepartamento(director.getDepartamento());
            docDto.setEmail(director.getCorreo());
            docDto.setNombres(director.getNombres());
            docDto.setApellidos(director.getApellidos());
            docDto.setCelular(director.getCelular());
            pDtoSend.setDirector(docDto);
        } else {
            pDtoSend.setDirector(null);
        }
        // ======= Formato A DTO (nuevo formato subido) =======
        FormatoADTOSend formatoSend = new FormatoADTOSend();
        formatoSend.setId(formatoGuardado.getId());
        formatoSend.setProyectoId(proyectoId);
        formatoSend.setNroVersion(formatoA.getNroVersion());
        formatoSend.setNombreFormatoA(formatoA.getNombreFormato());
        formatoSend.setFechaSubida(formatoA.getFechaCreacion());
        formatoSend.setBlob(formatoA.getBlob());
        formatoSend.setEstado(formatoA.getEstado());
        pDtoSend.setFormatoA(formatoSend);

        // ======= Envío del mensaje =======
        rabbitTemplate.convertAndSend(mainExchange, routingKeyProjectUpdated, pDtoSend);

        log.info("[RabbitMQ] Nueva versión de Formato A enviada a la cola: {} (Proyecto ID: {}, Versión: {})",
                routingKeyProjectUpdated, proyectoId, formatoA.getNroVersion());

        return true;
    }

    public FormatoA obtenerUltimoFormatoAConObservaciones(Long proyectoId) {
        List<FormatoA> resultados = formatoARepository.findUltimoFormatoAObservado(
                proyectoId, EstadoFormatoA.OBSERVADO, PageRequest.of(0, 1)
        );
        return resultados.isEmpty() ? null : resultados.get(0);
    }

    public void actualizarFormatoA(Long proyectoId, EstadoFormatoA estado) {
        formatoARepository.actualizarFormatoA(proyectoId, estado);
    }

    public int countProyectosByEstadoYTipo(TipoProyecto tipo, EstadoProyecto estado, String correoDocente) {
        return proyectoRepository.countProyectosByEstadoYTipo(tipo, estado, correoDocente);
    }

    public List<AnteproyectoDTO> listarAnteproyectosDocente(String correo, String filtro) {
        return anteproyectoRepository.listarAnteproyectosPorCorreoDocente(correo, filtro);
    }

    public void asociarAnteproyectoAProyecto(String correo, AnteproyectoDTO dto) {

        var proyecto = proyectoRepository.findByEstudiantesCorreoIgnoreCaseAndEstadoProyecto(correo, EstadoProyecto.EN_TRAMITE)
                .orElseThrow(() -> new EntityNotFoundException("El estudiante no tiene un proyecto asociado."));

        Anteproyecto anteproyecto = new Anteproyecto();
        anteproyecto.setDescripcion(dto.getDescripcion());
        anteproyecto.setTitulo(dto.getTitulo());
        anteproyecto.setNombreArchivo(dto.getNombreArchivo());
        anteproyecto.setBlob(dto.getBlob());
        anteproyecto.setFechaCreacion(LocalDate.now());

        anteproyecto.setProyecto(proyecto);

        anteproyectoRepository.save(anteproyecto);

        proyecto.setAnteproyecto(anteproyecto);
        proyectoRepository.save(proyecto);

        // --- Enviar evento de actualización del proyecto ---
        ProyectoDTOSend pDtoSend = new ProyectoDTOSend();
        pDtoSend.setId(proyecto.getId());
        pDtoSend.setTitulo(proyecto.getTitulo());
        pDtoSend.setTipoProyecto(proyecto.getTipoProyecto());
        pDtoSend.setEstado(proyecto.getEstadoProyecto());

        Docente director = proyecto.getDirector();
        if (director != null) {
            DocenteDTOSend docDto = new DocenteDTOSend();
            docDto.setId(director.getId());
            docDto.setCelular(director.getCelular());
            docDto.setDepartamento(director.getDepartamento());
            docDto.setNombres(director.getNombres());
            docDto.setApellidos(director.getApellidos());
            docDto.setEmail(director.getCorreo());
            pDtoSend.setDirector(docDto);
        }

        // Estudiantes
        List<EstudianteDTOSend> estudiantes = new ArrayList<>();
        for (Estudiante e : proyecto.getEstudiantes()) {
            EstudianteDTOSend estDto = new EstudianteDTOSend();
            estDto.setId(e.getId());
            estDto.setNombres(e.getNombres());
            estDto.setApellidos(e.getApellidos());
            estDto.setCelular(e.getCelular());
            estDto.setPrograma(e.getPrograma());
            estDto.setEmail(e.getCorreo());
            estudiantes.add(estDto);
        }
        pDtoSend.setEstudiantes(estudiantes);

        // Anteproyecto asociado
        AnteproyectoSinEvaluadoresEvent anteEvent = new AnteproyectoSinEvaluadoresEvent(
                proyecto.getId(),
                anteproyecto.getId(),
                anteproyecto.getTitulo(),
                anteproyecto.getDescripcion(),
                anteproyecto.getFechaCreacion(),
                proyecto.getEstudiantes().get(0).getCorreo(),
                proyecto.getDirector() != null ? proyecto.getDirector().getCorreo() : null,
                proyecto.getDirector() != null ? proyecto.getDirector().getDepartamento().name() : "DESCONOCIDO"
        );

        rabbitTemplate.convertAndSend(mainExchange, "academic.anteproyecto.created", anteEvent);

        log.info("[RabbitMQ] AnteproyectoSinEvaluadoresEvent publicado -> exchange={}, rk={}, payload={}",
                mainExchange, "academic.anteproyecto.created", anteEvent);

    }

    public AnteproyectoDTO obtenerAnteproyecto(long proyectoId) {
        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró el proyecto con ID: " + proyectoId));

        Anteproyecto anteproyecto = proyecto.getAnteproyecto();
        if (anteproyecto == null) {
            throw new EntityNotFoundException("El proyecto no tiene un anteproyecto asociado");
        }

        AnteproyectoDTO dto = new AnteproyectoDTO();
        dto.setId(anteproyecto.getId());
        dto.setNombreArchivo(anteproyecto.getNombreArchivo());
        dto.setDescripcion(anteproyecto.getDescripcion());
        dto.setTitulo(anteproyecto.getTitulo());
        dto.setBlob(anteproyecto.getBlob());
        dto.setFechaCreacion(anteproyecto.getFechaCreacion());

        if (proyecto.getEstudiantes() != null && !proyecto.getEstudiantes().isEmpty()) {
            dto.setEstudianteNombre(proyecto.getEstudiantes().get(0).getNombres());
            dto.setEstudianteCorreo(proyecto.getEstudiantes().get(0).getCorreo());
        }

        return dto;
    }

    public FormatoADTO obtenerUltimoFormatoAConObservaciones(long proyectoId) {
        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró el proyecto con ID: " + proyectoId));

        List<FormatoA> observados = proyecto.getFormatosA().stream()
                .filter(f -> f.getEstado() == EstadoFormatoA.OBSERVADO)
                .sorted((f1, f2) -> f2.getFechaCreacion().compareTo(f1.getFechaCreacion()))
                .collect(Collectors.toList());

        if (observados.isEmpty()) {
            throw new EntityNotFoundException("No hay formatos A con observaciones para este proyecto");
        }

        FormatoA ultimo = observados.get(0);

        FormatoADTO dto = new FormatoADTO();
        dto.setNombreFormato(ultimo.getNombreFormato());
        dto.setBlob(ultimo.getBlob());
        dto.setFechaCreacion(ultimo.getFechaCreacion());
        dto.setEstado(ultimo.getEstado());

        return dto;
    }
}

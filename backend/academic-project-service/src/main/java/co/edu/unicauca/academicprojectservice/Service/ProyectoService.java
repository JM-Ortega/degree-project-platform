package co.edu.unicauca.academicprojectservice.Service;

import co.edu.unicauca.academicprojectservice.Entity.*;
import co.edu.unicauca.academicprojectservice.Repository.*;
import co.edu.unicauca.academicprojectservice.infra.dto.AnteproyectoDTO;
import co.edu.unicauca.academicprojectservice.infra.dto.FormatoADTO;
import co.edu.unicauca.academicprojectservice.infra.dto.ProyectoDTO;
import co.edu.unicauca.academicprojectservice.infra.dto.ProyectoInfoDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<ProyectoInfoDTO> listarInfoPorCorreoDocente(String correo, String filtro) {
        Docente docente = docenteRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Docente no encontrado con correo: " + correo));

        return proyectoRepository.listarInfoPorDocente(docente.getId(), filtro);
    }

    public void crearProyectoConArchivos(ProyectoDTO dto){
        Proyecto proyecto = new Proyecto();
        proyecto.setTitulo(dto.getTitulo());
        proyecto.setTipoProyecto(dto.getTipoProyecto());
        proyecto.setEstadoProyecto(EstadoProyecto.EN_TRAMITE);

        Estudiante estudiante = estudianteRepository.findByCorreoIgnoreCase(dto.getEstudiante())
                .orElseThrow(() -> new IllegalArgumentException("No existe un estudiante con ese correo"));
        proyecto.setEstudiantes(List.of(estudiante));

        Docente docente = docenteRepository.findByCorreo(dto.getDirector())
                .orElseThrow(() -> new IllegalArgumentException("No existe un docente con ese correo"));
        proyecto.setDirector(docente);

        FormatoA formatoA = dto.getFormatoA();
        if (formatoA != null) {
            formatoA.setProyecto(proyecto);
            formatoA.setEstado(dto.getFormatoA().getEstado());
            formatoA.setNombreFormato(dto.getFormatoA().getNombreFormato());
            formatoA.setFechaCreacion(dto.getFormatoA().getFechaCreacion());
            formatoA.setNroVersion(dto.getFormatoA().getNroVersion());
            proyecto.addFormato(formatoA);
        }

        CartaLaboral cartaLaboral = dto.getCartaLaboral();
        if (cartaLaboral != null) {
            cartaLaboral.setProyecto(proyecto);
            cartaLaboral.setNombreCartaLaboral(dto.getCartaLaboral().getNombreCartaLaboral());
            cartaLaboral.setFechaCreacion(dto.getCartaLaboral().getFechaCreacion());
            proyecto.setCartaLaboral(cartaLaboral);
        }

        proyectoRepository.save(proyecto);
    }

    public EstadoProyecto enforceAutoCancelIfNeeded(long proyectoId) {
        int observados = formatoARepository.countByProyectoIdAndEstado(proyectoId, EstadoArchivo.OBSERVADO);
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

        return ultimo.getEstado() == EstadoArchivo.OBSERVADO;
    }

    public boolean tieneObservaciones(Long proyectoId) {
        FormatoA ultimo = getUltimoFormatoA(proyectoId);
        return ultimo != null && ultimo.getEstado() == EstadoArchivo.OBSERVADO;
    }

    public boolean existeProyecto(Long proyectoId) {
        return proyectoRepository.existsById(proyectoId);
    }

    public String estadoProyecto(Long proyectoId) {
        return proyectoRepository.getEstadoProyecto(proyectoId);
    }

    public boolean insertarFormatoAEnProyecto(Long proyectoId, FormatoA formatoA) {
        return false;
    }

    public FormatoA obtenerUltimoFormatoAConObservaciones(Long proyectoId) {
        List<FormatoA> resultados = formatoARepository.findUltimoFormatoAObservado(
                proyectoId, EstadoArchivo.OBSERVADO, PageRequest.of(0, 1)
        );
        return resultados.isEmpty() ? null : resultados.get(0);
    }

    public void actualizarFormatoA(Long proyectoId, EstadoArchivo estado) {
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
        anteproyecto.setFechaCreacion(new Date());

        anteproyecto.setProyecto(proyecto);

        anteproyectoRepository.save(anteproyecto);

        proyecto.setAnteproyecto(anteproyecto);
        proyectoRepository.save(proyecto);
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
                .filter(f -> f.getEstado() == EstadoArchivo.OBSERVADO)
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

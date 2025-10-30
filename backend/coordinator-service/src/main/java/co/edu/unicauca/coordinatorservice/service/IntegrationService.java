package co.edu.unicauca.coordinatorservice.service;

import co.edu.unicauca.coordinatorservice.entity.FormatoA;
import co.edu.unicauca.coordinatorservice.infra.FormatoADTO;
import co.edu.unicauca.coordinatorservice.infra.ProyectoDTO;
import co.edu.unicauca.coordinatorservice.repository.FormatoARepository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class IntegrationService {
    private final FormatoARepository formatoARepository;

    // Cache temporal para sincronizar mensajes
    private final Map<Long, ProyectoDTO> proyectosCache = new ConcurrentHashMap<>();
    private final Map<Long, FormatoADTO> formatosCache = new ConcurrentHashMap<>();

    public IntegrationService(FormatoARepository formatoARepository) {
        this.formatoARepository = formatoARepository;
    }

    public void handleProjectUpdate(ProyectoDTO proyectoDTO) {
        proyectosCache.put(proyectoDTO.getId(), proyectoDTO);

        // Si ya existe un formato con ese proyectoId, los fusionamos
        if (formatosCache.containsKey(proyectoDTO.getId())) {
            mergeAndSave(proyectoDTO, formatosCache.remove(proyectoDTO.getId()));
        }
    }

    public void handleFormatoAUpdate(FormatoADTO formatoADTO) {
        formatosCache.put(formatoADTO.getProyectoId(), formatoADTO);

        // Si ya existe un proyecto con ese id, los fusionamos
        if (proyectosCache.containsKey(formatoADTO.getProyectoId())) {
            mergeAndSave(proyectosCache.get(formatoADTO.getProyectoId()), formatoADTO);
            formatosCache.remove(formatoADTO.getProyectoId());
        }
    }

    private void mergeAndSave(ProyectoDTO proyecto, FormatoADTO formatoDTO) {
        FormatoA formato = new FormatoA();
        formato.setId(formatoDTO.getId());
        formato.setProyectoId(formatoDTO.getProyectoId());
        formato.setEstudiantes(proyecto.getEstudiantes());
        formato.setDirector(proyecto.getNombreDir());
        formato.setCoodirector(proyecto.getNombreCodir());
        formato.setNroVersion(formatoDTO.getNroVersion());
        formato.setNombre(formatoDTO.getNombre());
        formato.setFechaSubida(formatoDTO.getFechaSubida());
        formato.setEstado(formatoDTO.getEstado());
        formato.setTipoTrabajoGrado(proyecto.getTipoTrabajoGrado());

        // Aquí se maneja el archivo en Base64 (no como blob directo)
        if (formatoDTO.getArchivoBase64() != null) {
            formato.setArchivoBase64(formatoDTO.getArchivoBase64());
        }

        formatoARepository.save(formato);
        System.out.println("FormatoA fusionado y guardado correctamente: " + formato.getNombre());
    }
}

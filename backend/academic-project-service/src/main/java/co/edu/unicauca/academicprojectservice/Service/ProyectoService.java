package co.edu.unicauca.academicprojectservice.Service;

import co.edu.unicauca.academicprojectservice.Entity.Proyecto;
import co.edu.unicauca.academicprojectservice.Repository.ProyectoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProyectoService {
    private final ProyectoRepository proyectoRepository;

    public ProyectoService(ProyectoRepository proyectoRepository) {
        this.proyectoRepository = proyectoRepository;
    }

    public List<Proyecto> listarPorDocente(Long docenteId, String filtro) {
        return proyectoRepository.listarPorDocente(docenteId, filtro);
    }
}

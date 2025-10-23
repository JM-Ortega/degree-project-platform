package co.edu.unicauca.coordinatorservice.repository;

import co.edu.unicauca.coordinatorservice.entity.EstadoFormatoA;
import co.edu.unicauca.coordinatorservice.entity.FormatoA;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FormatoARepository extends JpaRepository<FormatoA, Long> {
    List<FormatoA> findByProyectoId(Long proyectoId);

    List<FormatoA> findByEstado(EstadoFormatoA estado);

    FormatoA findTopByProyectoIdOrderByNroVersionDesc(Long proyectoId);
}

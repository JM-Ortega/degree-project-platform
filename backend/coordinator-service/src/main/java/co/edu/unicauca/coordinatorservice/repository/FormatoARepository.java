package co.edu.unicauca.coordinatorservice.repository;

import co.edu.unicauca.coordinatorservice.entity.FormatoA;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FormatoARepository extends JpaRepository<FormatoA, Long> {
    Optional<FormatoA> findByProyectoId(Long proyectoId);
}

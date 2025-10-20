package co.edu.unicauca.coordinatorservice.repository;

import co.edu.unicauca.coordinatorservice.entity.Coordinador;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoordinadorRepository extends JpaRepository<Coordinador, Long> {
}

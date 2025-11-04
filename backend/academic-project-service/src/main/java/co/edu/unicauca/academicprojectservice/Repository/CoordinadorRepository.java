package co.edu.unicauca.academicprojectservice.Repository;

import co.edu.unicauca.academicprojectservice.Entity.Coordinador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CoordinadorRepository extends JpaRepository<Coordinador, Long> {
    Optional<Coordinador> findByCorreo(String correo);
}

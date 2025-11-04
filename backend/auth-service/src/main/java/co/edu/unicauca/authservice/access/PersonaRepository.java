package co.edu.unicauca.authservice.access;

import co.edu.unicauca.authservice.domain.entities.Persona;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonaRepository extends JpaRepository<Persona, String> {

    Optional<Persona> findByUsuarioId(String usuarioId);
}

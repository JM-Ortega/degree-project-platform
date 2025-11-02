package co.edu.unicauca.departmentheadservice.access;

import co.edu.unicauca.departmentheadservice.entities.JefeDeDepartamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JefeDeDepartamentoRepository extends JpaRepository<JefeDeDepartamento, String> {

    Optional<JefeDeDepartamento> findByPersonaId(String personaId);

}

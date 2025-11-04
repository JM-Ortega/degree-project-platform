package co.edu.unicauca.academicprojectservice.Repository;

import co.edu.unicauca.academicprojectservice.Entity.Docente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocenteRepository extends JpaRepository<Docente, Long> {
    Optional<Docente> findByCorreo(String correo);
}

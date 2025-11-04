package co.edu.unicauca.academicprojectservice.Repository;

import co.edu.unicauca.academicprojectservice.Entity.JefeDeDepartamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JefeDeDepartamentoRepository extends JpaRepository<JefeDeDepartamento, Long> {
    Optional<JefeDeDepartamento> findByCorreo(String correo);
}

package co.edu.unicauca.academicprojectservice.Repository;

import co.edu.unicauca.academicprojectservice.Entity.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {
    @Query("""
        SELECT p FROM Proyecto p
        JOIN p.estudiantes e
        WHERE 
            (p.director.id = :docenteId OR p.codirector.id = :docenteId)
            AND (
                :filtro IS NULL OR :filtro = '' OR
                LOWER(p.titulo) LIKE LOWER(CONCAT('%', :filtro, '%')) OR
                LOWER(e.nombres) LIKE LOWER(CONCAT('%', :filtro, '%')) OR
                LOWER(e.apellidos) LIKE LOWER(CONCAT('%', :filtro, '%')) OR
                LOWER(e.correo) LIKE LOWER(CONCAT('%', :filtro, '%'))
            )
        ORDER BY p.id DESC
    """)
    List<Proyecto> listarPorDocente(@Param("docenteId") Long docenteId, @Param("filtro") String filtro);
}

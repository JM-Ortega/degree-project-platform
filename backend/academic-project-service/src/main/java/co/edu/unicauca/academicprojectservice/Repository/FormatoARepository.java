package co.edu.unicauca.academicprojectservice.Repository;

import co.edu.unicauca.academicprojectservice.Entity.EstadoFormatoA;
import co.edu.unicauca.academicprojectservice.Entity.FormatoA;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface FormatoARepository extends JpaRepository<FormatoA, Long> {
    @Query("""
    SELECT COUNT(f)
    FROM FormatoA f
    WHERE f.proyecto.id = :proyectoId
    AND f.estado = :estadoProyecto
""")
    int countByProyectoIdAndEstado(@Param("proyectoId") Long proyectoId, @Param("estadoProyecto") EstadoFormatoA estadoProyecto);

    @Query("""
        SELECT MAX(f.nroVersion)
        FROM FormatoA f
        WHERE f.proyecto.id = :proyectoId
    """)
    Integer findMaxVersionByProyectoId(@Param("proyectoId") Long proyectoId);

    @Query("""
        SELECT f
        FROM FormatoA f
        WHERE f.proyecto.id = :proyectoId
        ORDER BY f.nroVersion DESC
    """)
    List<FormatoA> findUltimoFormatoA(@Param("proyectoId") Long proyectoId, Pageable pageable);

    @Query("""
        SELECT f
        FROM FormatoA f
        WHERE f.proyecto.id = :proyectoId
          AND f.estado = :estadoProyecto
        ORDER BY f.nroVersion DESC
    """)
    List<FormatoA> findUltimoFormatoAObservado(@Param("proyectoId") Long proyectoId, @Param("estadoProyecto") EstadoFormatoA estadoProyecto, Pageable pageable);

    @Modifying
    @Transactional
    @Query("""
        UPDATE FormatoA f
        SET f.estado = :estadoProyecto
        WHERE f.proyecto.id = :proyectoId
          AND f.nroVersion = (
              SELECT MAX(f2.nroVersion)
              FROM FormatoA f2
              WHERE f2.proyecto.id = :proyectoId
          )
    """)
    void actualizarFormatoA(@Param("proyectoId") Long proyectoId, @Param("estadoProyecto") EstadoFormatoA estadoProyecto);


    @Query("SELECT CASE WHEN COUNT(fa) > 0 THEN true ELSE false END " +
            "FROM FormatoA fa JOIN fa.proyecto p JOIN p.estudiantes e " +
            "WHERE lower(e.correo) = lower(:correo) AND fa.estado = :estado")
    boolean existeFormatoAAprobadoPorCorreo(@Param("correo") String correo, @Param("estado") EstadoFormatoA estado);

    Optional<FormatoA> findByProyectoId(Long proyectoId);
}

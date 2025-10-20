package co.edu.unicauca.coordinatorservice.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "formato_a")
public class FormatoA {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long proyectoId;
    private int nroVersion;
    private String nombre;
    private LocalDate fechaSubida;
    private byte[] blob;
    private EstadoFormatoA estado;
}

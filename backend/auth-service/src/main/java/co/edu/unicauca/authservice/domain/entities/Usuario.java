package co.edu.unicauca.authservice.domain.entities;

import co.edu.unicauca.shared.contracts.model.Rol;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entidad que representa las credenciales e identidad base
 * de un usuario dentro del sistema.
 *
 * <p>Un usuario puede tener varios roles (por ejemplo,
 * Docente y Coordinador) asociados a la misma cuenta.</p>
 */
@Entity
@Table(name = "usuarios")
@Schema(description = "Entidad que representa un usuario autenticable del sistema.")
public class Usuario {

    @Id
    @Column(length = 36, updatable = false, nullable = false)
    @Schema(description = "Identificador único del usuario (UUID).", example = "d3b07384-d9a3-4a7a-9a44-61a4c1234b8f")
    private String id;

    @Column(nullable = false, unique = true, length = 120)
    @Schema(description = "Correo institucional del usuario.", example = "juan.perez@unicauca.edu.co")
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    @Schema(description = "Contraseña almacenada en formato hash Argon2 (no visible en las respuestas).")
    private String passwordHash;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "usuario_roles",
        joinColumns = @JoinColumn(name = "usuario_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false, length = 40)
    @Schema(description = "Lista de roles asociados al usuario.")
    private List<Rol> roles = new ArrayList<>();

    protected Usuario() {
        // Constructor protegido para JPA
    }

    public Usuario(String email, String passwordHash, List<Rol> roles) {
        this.id = UUID.randomUUID().toString();
        this.email = email;
        this.passwordHash = passwordHash;
        this.roles = roles;
    }

    // Getters y setters
    public String getId() { return id; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public List<Rol> getRoles() { return roles; }

    public void setEmail(String email) { this.email = email; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setRoles(List<Rol> roles) { this.roles = roles; }
}

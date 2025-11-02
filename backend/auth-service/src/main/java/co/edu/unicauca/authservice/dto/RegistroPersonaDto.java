package co.edu.unicauca.authservice.dto;

import co.edu.unicauca.shared.contracts.model.Departamento;
import co.edu.unicauca.shared.contracts.model.Programa;
import co.edu.unicauca.shared.contracts.model.Rol;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * DTO usado para registrar una persona junto con su cuenta de usuario.
 *
 * <p>
 * Este DTO es intencionalmente genérico: el backend determinará,
 * a partir de los roles enviados, qué subclase de {@code Persona}
 * debe crearse (Estudiante, Docente, Coordinador o JefeDeDepartamento).
 * </p>
 *
 * <p>
 * Para registros públicos solo deberían enviarse roles
 * {@link Rol#Estudiante} o {@link Rol#Docente}. Los roles de mayor
 * privilegio (Coordinador, JefeDeDepartamento) deben ser creados
 * desde un endpoint administrativo.
 * </p>
 */
@Schema(description = "Datos necesarios para registrar una nueva persona en el sistema.")
public record RegistroPersonaDto(

        @NotBlank
        @Schema(description = "Nombres de la persona.", example = "Juan Sebastián")
        String nombres,

        @NotBlank
        @Schema(description = "Apellidos de la persona.", example = "Ortega Narváez")
        String apellidos,

        @NotBlank
        @Email
        @Schema(description = "Correo electrónico que usará para autenticarse.", example = "juan.ortega@unicauca.edu.co")
        String email,

        @NotBlank
        @Schema(description = "Contraseña en texto plano. El backend la hashea antes de guardar.", example = "Clave123*")
        String password,

        @Schema(description = "Número de celular de contacto.", example = "3145678901")
        String celular,

        @Schema(description = "Programa académico al que pertenece la persona.", example = "IngenieriaDeSistemas")
        Programa programa,

        @NotEmpty
        @ArraySchema(arraySchema = @Schema(description = "Roles que tendrá el usuario."), uniqueItems = true)
        @Schema(description = "Lista de roles que se asignarán al usuario. El backend escogerá el rol principal según prioridad.")
        List<Rol> roles,

        @Schema(description = "Departamento al que pertenece (solo para Docente / JefeDeDepartamento).", example = "Sistemas")
        Departamento departamento

) { }

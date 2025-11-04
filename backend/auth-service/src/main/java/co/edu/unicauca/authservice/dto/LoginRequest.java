package co.edu.unicauca.authservice.dto;

import co.edu.unicauca.shared.contracts.model.Rol;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Credenciales para iniciar sesión.")
public record LoginRequest(

        @NotBlank
        @Email
        @Schema(example = "juan@unicauca.edu.co")
        String email,

        @NotBlank
        @Schema(example = "Clave123*")
        String password,

        @NotNull
        @Schema(description = "Rol con el que desea entrar en esta sesión.")
        Rol rol
) { }

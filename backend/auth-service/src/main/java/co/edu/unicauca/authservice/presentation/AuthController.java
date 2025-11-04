package co.edu.unicauca.authservice.presentation;

import co.edu.unicauca.authservice.domain.entities.Persona;
import co.edu.unicauca.authservice.dto.LoginRequest;
import co.edu.unicauca.authservice.dto.LoginResponse;
import co.edu.unicauca.authservice.dto.RegistroPersonaDto;
import co.edu.unicauca.authservice.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para la autenticación y registro de usuarios.
 */
@Tag(name = "Auth", description = "Endpoints para registro y autenticación de usuarios")
@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Endpoint para registrar una nueva persona (solo Estudiante o Docente).
     *
     * @param dto datos de registro enviados por el cliente
     * @return la persona registrada
     */
    @Operation(summary = "Registrar una nueva persona (solo Estudiante o Docente)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Persona registrada exitosamente",
                    content = @Content(schema = @Schema(implementation = Persona.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o rol no permitido",
                    content = @Content)
    })
    @PostMapping("/register")
    public ResponseEntity<Persona> register(@Valid @RequestBody RegistroPersonaDto dto) {
        Persona persona = authService.registrarPersona(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(persona);
    }

    /**
     * Endpoint para iniciar sesión con un rol específico.
     *
     * @param request credenciales de acceso y rol seleccionado
     * @return información de sesión básica
     */
    @Operation(summary = "Iniciar sesión seleccionando el rol con el que se va a trabajar")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "400", description = "Credenciales inválidas o rol no asignado",
                    content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}

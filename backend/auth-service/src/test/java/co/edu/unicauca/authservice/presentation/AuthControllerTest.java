package co.edu.unicauca.authservice.presentation;

import co.edu.unicauca.authservice.domain.entities.Persona;
import co.edu.unicauca.authservice.domain.entities.Usuario;
import co.edu.unicauca.authservice.dto.LoginRequest;
import co.edu.unicauca.authservice.dto.LoginResponse;
import co.edu.unicauca.authservice.dto.RegistroPersonaDto;
import co.edu.unicauca.authservice.services.AuthService;
import co.edu.unicauca.authservice.services.PersonaFactory;
import co.edu.unicauca.shared.contracts.dto.SessionInfo;
import co.edu.unicauca.shared.contracts.model.Departamento;
import co.edu.unicauca.shared.contracts.model.Programa;
import co.edu.unicauca.shared.contracts.model.Rol;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testLogin() throws Exception {
        // Datos de entrada
        LoginRequest loginRequest = new LoginRequest("juan@unicauca.edu.co", "Clave123*", Rol.ESTUDIANTE);

        // Datos de respuesta simulada
        SessionInfo sessionInfo = new SessionInfo("juan@unicauca.edu.co", "Juan", Rol.ESTUDIANTE);
        LoginResponse loginResponse = new LoginResponse(sessionInfo, "token123");

        // Simular el comportamiento del servicio
        when(authService.login(loginRequest)).thenReturn(loginResponse);

        // Realizar el test de la ruta /login
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.session.email").value("juan@unicauca.edu.co"))
                .andExpect(jsonPath("$.session.nombres").value("Juan"))
                .andExpect(jsonPath("$.session.rolActivo").value("ESTUDIANTE"))
                .andExpect(jsonPath("$.token").value("token123"));
    }

    @Test
    void testRegisterBadRequest() throws Exception {
        // Datos inválidos
        RegistroPersonaDto invalidRegistroDto = new RegistroPersonaDto(
                "", // Nombres vacíos
                "", // Apellidos vacíos
                "invalid-email", // Correo inválido
                "", // Contraseña vacía
                "3145678901", // Número de celular válido
                null, // Programa nulo
                List.of(), // Roles vacíos (violando la restricción @NotEmpty)
                null // Departamento nulo
        );

        // Realizar el test de la ruta /register con datos inválidos
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRegistroDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLoginBadRequest() throws Exception {
        // Datos inválidos
        LoginRequest invalidLoginRequest = new LoginRequest("juan@unicauca.edu.co", "", Rol.ESTUDIANTE);

        // Realizar el test de la ruta /login con credenciales inválidas
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidLoginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegister() throws Exception {
        // Crear una instancia de PersonaFactory
        PersonaFactory personaFactory = new PersonaFactory();

        // Datos de entrada
        RegistroPersonaDto dto = new RegistroPersonaDto(
                "Juan Sebastián",
                "Ortega",
                "juan@unicauca.edu.co",
                "Clave123*",
                "3145678901",
                Programa.INGENIERIA_DE_SISTEMAS,
                List.of(Rol.DOCENTE),
                Departamento.SISTEMAS
        );

        // Crear un usuario mock
        Usuario usuario = new Usuario("juan@unicauca.edu.co", "HASH-ARGON2", List.of(Rol.DOCENTE));

        // Usar el factory para crear la persona concreta (en este caso, Docente)
        Persona personaRegistrada = personaFactory.crearDesdeDto(dto, usuario);

        // Verificar que la persona se haya creado correctamente
        assertNotNull(personaRegistrada);
        assertEquals("juan@unicauca.edu.co", personaRegistrada.getUsuario().getEmail());
        assertEquals("Juan Sebastián", personaRegistrada.getNombres());
        assertEquals(Rol.DOCENTE, personaRegistrada.getUsuario().getRoles().get(0));
    }


}

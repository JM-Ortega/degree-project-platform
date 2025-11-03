package co.edu.unicauca.authservice.services;

import co.edu.unicauca.authservice.access.PersonaRepository;
import co.edu.unicauca.authservice.access.UsuarioRepository;
import co.edu.unicauca.authservice.domain.entities.Docente;
import co.edu.unicauca.authservice.domain.entities.Persona;
import co.edu.unicauca.authservice.domain.entities.Usuario;
import co.edu.unicauca.authservice.dto.LoginRequest;
import co.edu.unicauca.authservice.dto.RegistroPersonaDto;
import co.edu.unicauca.authservice.infra.messaging.NotificationPublisher;
import co.edu.unicauca.authservice.infra.messaging.UserEventsPublisher;
import co.edu.unicauca.shared.contracts.model.Departamento;
import co.edu.unicauca.shared.contracts.model.Programa;
import co.edu.unicauca.shared.contracts.model.Rol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private PersonaRepository personaRepository;
    @Mock
    private PasswordHasher passwordHasher;
    @Mock
    private CodigoPersonaGenerator codigoPersonaGenerator;
    @Mock
    private PersonaFactory personaFactory;
    @Mock
    private UserEventsPublisher userEventsPublisher;
    @Mock
    private NotificationPublisher notificationPublisher;

    @InjectMocks
    private AuthService authService;

    // ORDEN REAL DEL DTO:
    // (nombres, apellidos, celular, email, password, programa, roles, departamento)
    private RegistroPersonaDto docenteDto;

    @BeforeEach
    void setUp() {
        docenteDto = new RegistroPersonaDto(
                "Andrés",                           // 1
                "García",                           // 2
                "DOCENTE.DEMO@unicauca.edu.co",     // 3 ← email
                "Uni123456*",                       // 4 ← password
                "3160000002",                       // 5 ← celular
                Programa.INGENIERIA_DE_SISTEMAS,      // 6 ← programa
                List.of(Rol.DOCENTE),               // 7 ← roles
                Departamento.SISTEMAS              // 8 ← departamento
        );

    }

    @Test
    void registrarPersona_debeCrearDocente_yPublicarEventos() {
        // Mock: el correo no existe en el sistema
        when(usuarioRepository.existsByEmail(eq("docente.demo@unicauca.edu.co")))
                .thenReturn(false);

        // Mock: hash del password
        doReturn("HASH-ARGON2").when(passwordHasher).hash(any(char[].class));

        // Mock: código de persona
        when(codigoPersonaGenerator.generar())
                .thenReturn("202501000001");

        // Mock: creación del usuario
        Usuario usuarioCreado = new Usuario(
                "docente.demo@unicauca.edu.co",
                "HASH-ARGON2",
                List.of(Rol.DOCENTE)
        );

        // Mock: creación de la entidad Docente
        Docente docenteEntidad = new Docente(
                UUID.randomUUID().toString(),
                null,
                "Andrés",
                "García",
                "3160000002",
                Programa.INGENIERIA_DE_SISTEMAS,
                usuarioCreado,
                Departamento.SISTEMAS
        );

        // Mock: llamada a la factory para crear el docente desde el DTO
        when(personaFactory.crearDesdeDto(eq(docenteDto), any(Usuario.class)))
                .thenReturn(docenteEntidad);

        // Ejecutar el método a probar
        Persona personaGuardada = authService.registrarPersona(docenteDto);

        // Verificaciones
        assertNotNull(personaGuardada);
        assertEquals("202501000001", personaGuardada.getCodigo());
        assertEquals("docente.demo@unicauca.edu.co", personaGuardada.getUsuario().getEmail());
        verify(personaRepository).save(docenteEntidad);
        verify(userEventsPublisher).publishUserCreatedEvent(any());
        verify(notificationPublisher).publishNotification(
                any(String.class),
                any(List.class),
                any(List.class),
                any(String.class),
                any(String.class)
        );


    }


    @Test
    void registrarPersona_debeFallar_siCorreoYaExiste() {
        // Mock: el correo YA existe en el sistema
        when(usuarioRepository.existsByEmail(eq("docente.demo@unicauca.edu.co")))
                .thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> authService.registrarPersona(docenteDto));

        verify(personaRepository, never()).save(any());
        verify(userEventsPublisher, never()).publishUserCreatedEvent(any());
    }

    @Test
    void registrarPersona_debeFallar_siRolNoEsDeAutoregistro() {
        // DTO de un Jefe de Departamento para el test.
        RegistroPersonaDto jefeDto = new RegistroPersonaDto(
                "Luis",                          // nombres
                "Torres",                        // apellidos
                "jefe.demo@unicauca.edu.co",     // email ✓
                "Uni123456*",                    // password ✓
                "3180000004",                    // celular ✓
                Programa.INGENIERIA_DE_SISTEMAS,   // programa
                List.of(Rol.JEFE_DE_DEPARTAMENTO), // roles
                Departamento.SISTEMAS            // departamento
        );

        // Stub: simula que el email NO existe todavía
        when(usuarioRepository.existsByEmail("jefe.demo@unicauca.edu.co"))
                .thenReturn(false);

        // El test debe fallar porque JefeDeDepartamento no es un rol de autoregistro
        assertThrows(IllegalArgumentException.class,
                () -> authService.registrarPersona(jefeDto));

        // Verifica que no se guarde la persona ni se publiquen eventos
        verify(personaRepository, never()).save(any());
        verify(userEventsPublisher, never()).publishUserCreatedEvent(any());
    }


    @Test
    void login_debeFuncionar_conCredencialesCorrectas_yRolAsignado() {
        Usuario usuario = new Usuario(
                "docente.demo@unicauca.edu.co",
                "HASH-ARGON2",
                List.of(Rol.DOCENTE)
        );
        when(usuarioRepository.findByEmail("docente.demo@unicauca.edu.co"))
                .thenReturn(Optional.of(usuario));

        doReturn(true).when(passwordHasher).verify(any(char[].class), eq("HASH-ARGON2"));

        Docente docente = new Docente(
                UUID.randomUUID().toString(),
                "202501000001",
                "Andrés",
                "García",
                "3160000002",
                Programa.INGENIERIA_DE_SISTEMAS,
                usuario,
                Departamento.SISTEMAS
        );
        when(personaRepository.findByUsuarioId(usuario.getId()))
                .thenReturn(Optional.of(docente));

        LoginRequest req = new LoginRequest(
                " DOCENTE.DEMO@unicauca.edu.co ",  // con espacios y mayúsculas
                "Uni123456*",
                Rol.DOCENTE
        );

        var resp = authService.login(req);

        assertNotNull(resp);
        assertNotNull(resp.session());
        assertEquals("Andrés García", resp.session().nombres());
        assertEquals("docente.demo@unicauca.edu.co", resp.session().email());
        assertEquals(Rol.DOCENTE, resp.session().rolActivo());
        assertTrue(resp.token().startsWith("SESSION-"));
    }

    @Test
    void login_debeFallar_siPasswordIncorrecta() {
        Usuario usuario = new Usuario(
                "docente.demo@unicauca.edu.co",
                "HASH-ARGON2",
                List.of(Rol.DOCENTE)
        );
        when(usuarioRepository.findByEmail("docente.demo@unicauca.edu.co"))
                .thenReturn(Optional.of(usuario));

        doReturn(false).when(passwordHasher).verify(any(char[].class), eq("HASH-ARGON2"));

        LoginRequest req = new LoginRequest(
                "docente.demo@unicauca.edu.co",
                "mala",
                Rol.DOCENTE
        );

        assertThrows(IllegalArgumentException.class,
                () -> authService.login(req));
    }

    @Test
    void login_debeFallar_siRolNoAsignadoAlUsuario() {
        Usuario usuario = new Usuario(
                "docente.demo@unicauca.edu.co",
                "HASH-ARGON2",
                List.of(Rol.DOCENTE)
        );
        when(usuarioRepository.findByEmail("docente.demo@unicauca.edu.co"))
                .thenReturn(Optional.of(usuario));

        doReturn(true).when(passwordHasher).verify(any(char[].class), eq("HASH-ARGON2"));

        LoginRequest req = new LoginRequest(
                "docente.demo@unicauca.edu.co",
                "Uni123456*",
                Rol.ESTUDIANTE
        );

        assertThrows(IllegalArgumentException.class,
                () -> authService.login(req));
    }

    @Test
    void login_debeFallar_siUsuarioNoExiste() {
        when(usuarioRepository.findByEmail("noexiste@unicauca.edu.co"))
                .thenReturn(Optional.empty());

        LoginRequest req = new LoginRequest(
                "noexiste@unicauca.edu.co",
                "cualquier",
                Rol.DOCENTE
        );

        assertThrows(IllegalArgumentException.class,
                () -> authService.login(req));
    }
}

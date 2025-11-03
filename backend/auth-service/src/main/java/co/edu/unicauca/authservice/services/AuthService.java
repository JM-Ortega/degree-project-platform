package co.edu.unicauca.authservice.services;

import co.edu.unicauca.authservice.access.PersonaRepository;
import co.edu.unicauca.authservice.access.UsuarioRepository;
import co.edu.unicauca.authservice.domain.entities.Docente;
import co.edu.unicauca.authservice.domain.entities.JefeDeDepartamento;
import co.edu.unicauca.authservice.domain.entities.Persona;
import co.edu.unicauca.authservice.domain.entities.Usuario;
import co.edu.unicauca.authservice.dto.LoginRequest;
import co.edu.unicauca.authservice.dto.LoginResponse;
import co.edu.unicauca.authservice.dto.RegistroPersonaDto;
import co.edu.unicauca.authservice.infra.messaging.NotificationPublisher;
import co.edu.unicauca.authservice.infra.messaging.UserEventsPublisher;
import co.edu.unicauca.shared.contracts.dto.SessionInfo;
import co.edu.unicauca.shared.contracts.events.auth.UserCreatedEvent;
import co.edu.unicauca.shared.contracts.model.Departamento;
import co.edu.unicauca.shared.contracts.model.Rol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de aplicación para el registro y autenticación de usuarios.
 */
@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private static final List<Rol> ROLES_AUTOREGISTRO = List.of(
            Rol.ESTUDIANTE,
            Rol.DOCENTE
    );

    private final UsuarioRepository usuarioRepository;
    private final PersonaRepository personaRepository;
    private final PasswordHasher passwordHasher;
    private final CodigoPersonaGenerator codigoPersonaGenerator;
    private final PersonaFactory personaFactory;
    private final UserEventsPublisher userEventsPublisher;
    private final NotificationPublisher notificationPublisher;

    public AuthService(UsuarioRepository usuarioRepository,
                       PersonaRepository personaRepository,
                       PasswordHasher passwordHasher,
                       CodigoPersonaGenerator codigoPersonaGenerator,
                       PersonaFactory personaFactory,
                       UserEventsPublisher userEventsPublisher,
                       NotificationPublisher notificationPublisher) {
        this.usuarioRepository = usuarioRepository;
        this.personaRepository = personaRepository;
        this.passwordHasher = passwordHasher;
        this.codigoPersonaGenerator = codigoPersonaGenerator;
        this.personaFactory = personaFactory;
        this.userEventsPublisher = userEventsPublisher;
        this.notificationPublisher = notificationPublisher;
    }

    /**
     * Registra una nueva persona y usuario en el sistema.
     *
     * <p>Después de persistir los datos, publica:
     * <ul>
     *   <li>Un evento de creación de usuario para sincronización entre microservicios.</li>
     *   <li>Un evento de notificación para envío de correo de bienvenida.</li>
     * </ul>
     * </p>
     *
     * @param dto datos del registro enviados por el cliente
     * @return la persona creada y persistida
     */
    public Persona registrarPersona(RegistroPersonaDto dto) {

        // 1. normalizar correo para TODO el método
        final String emailNormalizado = dto.email().trim().toLowerCase();

        // 2. Verificar correo único sobre el correo normalizado
        if (usuarioRepository.existsByEmail(emailNormalizado)) {
            throw new IllegalArgumentException("Ya existe un usuario con ese correo");
        }

        // 3. Validar roles permitidos
        boolean tieneRolNoPermitido = dto.roles().stream()
                .anyMatch(rol -> !ROLES_AUTOREGISTRO.contains(rol));

        if (tieneRolNoPermitido) {
            throw new IllegalArgumentException("No está autorizado para registrarse con ese rol");
        }

        // 4. Hashear la contraseña
        String passwordHash = passwordHasher.hash(dto.password().toCharArray());

        // 5. Crear usuario usando SIEMPRE el correo limpio
        Usuario usuario = new Usuario(emailNormalizado, passwordHash, dto.roles());

        // 6. Crear persona concreta (usa el usuario ya creado)
        Persona persona = personaFactory.crearDesdeDto(dto, usuario);

        // 7. Generar código institucional
        persona.setCodigo(codigoPersonaGenerator.generar());

        // 8. Guardar en base de datos
        personaRepository.save(persona);

        // 9. Publicar eventos
        try {
            var departamento = obtenerDepartamentoSiAplica(persona);

            UserCreatedEvent userEvent = new UserCreatedEvent(
                    persona.getId(),
                    persona.getNombres() + " " + persona.getApellidos(),
                    usuario.getEmail(),           // ya es el normalizado
                    persona.getPrograma(),
                    departamento,
                    usuario.getRoles()
            );
            userEventsPublisher.publishUserCreatedEvent(userEvent);

            String type = "auth.user.created";
            String subject = "Bienvenido a la plataforma";
            String message = "Tu cuenta ha sido creada correctamente.";

// emails destinatarios
            var emails = List.of(usuario.getEmail());

// celulares destinatarios (si hay)
            var celulares = (persona.getCelular() != null && !persona.getCelular().isBlank())
                    ? List.of(persona.getCelular())
                    : List.<String>of();

            notificationPublisher.publishNotification(
                    type,
                    emails,
                    celulares,   // <— ahora sí
                    subject,
                    message
            );

            log.info("Eventos publicados correctamente para usuario {}", usuario.getEmail());
        } catch (Exception e) {
            log.error("Error al publicar eventos para el usuario {}: {}", emailNormalizado, e.getMessage(), e);
        }

        return persona;
    }

    /**
     * Autentica a un usuario verificando sus credenciales y rol.
     */
    public LoginResponse login(LoginRequest request) {

        // 1. normalizar correo antes de buscar
        final String emailNormalizado = request.email().trim().toLowerCase();

        var usuario = usuarioRepository.findByEmail(emailNormalizado)
                .orElseThrow(() -> new IllegalArgumentException("Usuario o contraseña incorrectos"));

        boolean passwordOk = passwordHasher.verify(
                request.password().toCharArray(),
                usuario.getPasswordHash()
        );

        if (!passwordOk) {
            throw new IllegalArgumentException("Usuario o contraseña incorrectos");
        }

        if (!usuario.getRoles().contains(request.rol())) {
            throw new IllegalArgumentException("El usuario no tiene el rol solicitado");
        }

        // recuperar la persona para poder sacar el nombre visible
        var persona = personaRepository.findByUsuarioId(usuario.getId())
                .orElse(null); // si por alguna razón no está, no rompemos

        String nombreVisible = persona != null
                ? persona.getNombres() + " " + persona.getApellidos()
                : usuario.getEmail();

        SessionInfo session = new SessionInfo(
                usuario.getEmail(),  // ya normalizado
                nombreVisible,
                request.rol()
        );

        String fakeToken = "SESSION-" + usuario.getId() + "-" + request.rol().name();

        return new LoginResponse(session, fakeToken);
    }

    /**
     * Determina el departamento asociado a una persona, si aplica según su tipo concreto.
     *
     * @param persona instancia concreta de {@link co.edu.unicauca.authservice.domain.entities.Persona}
     * @return el departamento asociado, o {@code null} si no aplica
     */
    private Departamento obtenerDepartamentoSiAplica(Persona persona) {
        return switch (persona) {
            case Docente d -> d.getDepartamento();
            case JefeDeDepartamento j -> j.getDepartamento();
            default -> null;
        };
    }
}

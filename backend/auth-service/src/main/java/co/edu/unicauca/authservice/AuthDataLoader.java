package co.edu.unicauca.authservice;

import co.edu.unicauca.authservice.access.PersonaRepository;
import co.edu.unicauca.authservice.access.UsuarioRepository;
import co.edu.unicauca.authservice.domain.entities.Persona;
import co.edu.unicauca.authservice.domain.entities.Usuario;
import co.edu.unicauca.authservice.infra.messaging.NotificationPublisher;
import co.edu.unicauca.authservice.infra.messaging.UserEventsPublisher;
import co.edu.unicauca.authservice.services.CodigoPersonaGenerator;
import co.edu.unicauca.authservice.services.PasswordHasher;
import co.edu.unicauca.authservice.services.PersonaFactory;
import co.edu.unicauca.shared.contracts.events.auth.UserCreatedEvent;
import co.edu.unicauca.shared.contracts.events.notification.SendEmailEvent;
import co.edu.unicauca.shared.contracts.model.Departamento;
import co.edu.unicauca.shared.contracts.model.Programa;
import co.edu.unicauca.shared.contracts.model.Rol;
import co.edu.unicauca.authservice.dto.RegistroPersonaDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Cargador de datos de ejemplo para el microservicio de autenticación.
 *
 * <p>
 * Este componente se ejecuta al arrancar la aplicación (solo en perfiles
 * {@code dev} o {@code local}) y crea algunos usuarios de prueba en la base
 * de datos utilizando exactamente la misma infraestructura que el flujo real:
 * <ul>
 *     <li>mismo {@link PasswordHasher}</li>
 *     <li>misma {@link PersonaFactory}</li>
 *     <li>mismo {@link CodigoPersonaGenerator}</li>
 * </ul>
 * </p>
 *
 * <p>
 * A diferencia del endpoint público de registro, aquí SÍ creamos usuarios con
 * roles administrativos (Coordinador, JefeDeDepartamento) porque este código
 * no pasa por la validación de “solo Estudiante/Docente”. Esto permite tener
 * datos completos para probar los distintos dashboards del frontend.
 * </p>
 */
@Component
//@Profile({"dev", "local"})
public class AuthDataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AuthDataLoader.class);

    private final UsuarioRepository usuarioRepo;
    private final PersonaRepository personaRepo;
    private final PasswordHasher passwordHasher;
    private final PersonaFactory personaFactory;
    private final CodigoPersonaGenerator codigoPersonaGenerator;
    private final UserEventsPublisher userEventsPublisher;
    private final NotificationPublisher notificationPublisher;

    public AuthDataLoader(UsuarioRepository usuarioRepo,
                          PersonaRepository personaRepo,
                          PasswordHasher passwordHasher,
                          PersonaFactory personaFactory,
                          CodigoPersonaGenerator codigoPersonaGenerator,
                          UserEventsPublisher userEventsPublisher,
                          NotificationPublisher notificationPublisher) {
        this.usuarioRepo = usuarioRepo;
        this.personaRepo = personaRepo;
        this.passwordHasher = passwordHasher;
        this.personaFactory = personaFactory;
        this.codigoPersonaGenerator = codigoPersonaGenerator;
        this.userEventsPublisher = userEventsPublisher;
        this.notificationPublisher = notificationPublisher;
    }

    @Override
    public void run(String... args) {
        log.info("=== Iniciando carga de datos de ejemplo para AuthService ===");

        // todos con la misma clave de demo
        final String passwordPlano = "Uni123456*";

        // 1. Estudiante
        crearUsuarioDemo(
                "estudiante.demo@unicauca.edu.co",
                "Camila",
                "López",
                Programa.IngenieriaDeSistemas,
                null,
                List.of(Rol.Estudiante),
                passwordPlano
        );

        // 2. Docente
        crearUsuarioDemo(
                "docente.demo@unicauca.edu.co",
                "Andrés",
                "García",
                Programa.IngenieriaDeSistemas,
                Departamento.Sistemas,
                List.of(Rol.Docente),
                passwordPlano
        );

        // 3. Coordinador
        crearUsuarioDemo(
                "coordinador.demo@unicauca.edu.co",
                "María",
                "Pérez",
                Programa.IngenieriaDeSistemas,   // también será su programa coordinado
                null,
                List.of(Rol.Coordinador),
                passwordPlano
        );

        // 4. Jefe de departamento
        crearUsuarioDemo(
                "jefe.demo@unicauca.edu.co",
                "Jorge",
                "Ramírez",
                Programa.IngenieriaElectronicaYTelecomunicaciones,
                Departamento.Sistemas,
                List.of(Rol.JefeDeDepartamento),
                passwordPlano
        );

        // 5. Multi-rol (4 roles)
        crearUsuarioDemo(
                "multi.demo@unicauca.edu.co",
                "Laura",
                "Hernández",
                Programa.IngenieriaDeSistemas,
                Departamento.Sistemas,
                List.of(
                        Rol.Estudiante,
                        Rol.Docente,
                        Rol.Coordinador,
                        Rol.JefeDeDepartamento
                ),
                passwordPlano
        );

        log.info("=== Carga de datos de ejemplo completada ===");
    }

    /**
     * Crea un usuario/persona de demo si no existe aún el correo.
     *
     * @param email        correo del usuario (se normaliza a lower/trim)
     * @param nombres      nombres
     * @param apellidos    apellidos
     * @param programa     programa académico
     * @param departamento departamento (solo para docente / jefe)
     * @param roles        lista de roles que tendrá el usuario
     * @param passwordPlano contraseña en texto plano
     */
    private void crearUsuarioDemo(String email,
                                  String nombres,
                                  String apellidos,
                                  Programa programa,
                                  Departamento departamento,
                                  List<Rol> roles,
                                  String passwordPlano) {

        String emailNormalizado = email.trim().toLowerCase();

        // si ya existe, no lo volvemos a crear
        boolean existe = usuarioRepo.existsByEmail(emailNormalizado);
        if (existe) {
            log.info("Omitido {} → ya existe en la base de datos", emailNormalizado);
            return;
        }

        try {
            // 1) armar un DTO igual que el que mandaría el frontend
            RegistroPersonaDto dto = new RegistroPersonaDto(
                    nombres,
                    apellidos,
                    emailNormalizado,
                    passwordPlano,
                    null,            // celular opcional en este seed
                    programa,
                    roles,
                    departamento
            );

            // 2) crear la entidad Usuario
            String passwordHash = passwordHasher.hash(passwordPlano.toCharArray());
            Usuario usuario = new Usuario(emailNormalizado, passwordHash, roles);

            // 3) dejar que la factory elija la subclase de Persona
            var persona = personaFactory.crearDesdeDto(dto, usuario);

            // 4) generar código institucional igual que en el flujo real
            persona.setCodigo(codigoPersonaGenerator.generar());

            // 5) persistir
            usuarioRepo.save(usuario);
            personaRepo.save(persona);

            // 6) publicar los mismos eventos que en AuthService
            publicarEventos(persona, usuario);

            log.info("Usuario creado exitosamente: {}", emailNormalizado);
        } catch (Exception e) {
            log.warn("No se pudo crear {} → {}", emailNormalizado, e.getMessage());
        }
    }

    /**
     * Publica los eventos de dominio y de notificación asociados a la creación de usuario,
     * tal como lo hace el servicio principal.
     *
     * @param persona entidad de persona recién creada
     * @param usuario entidad de usuario asociada
     */
    private void publicarEventos(Persona persona, Usuario usuario) {
        try {
            UserCreatedEvent userEvent = new UserCreatedEvent(
                    persona.getId(),
                    persona.getNombres() + " " + persona.getApellidos(),
                    usuario.getEmail(),
                    persona.getPrograma(),
                    // si la persona es Docente/Jefe, el método de AuthService
                    // saca el departamento. Aquí podemos leerlo directo con pattern matching
                    switch (persona) {
                        case co.edu.unicauca.authservice.domain.entities.Docente d -> d.getDepartamento();
                        case co.edu.unicauca.authservice.domain.entities.JefeDeDepartamento j -> j.getDepartamento();
                        default -> null;
                    },
                    usuario.getRoles()
            );
            userEventsPublisher.publishUserCreatedEvent(userEvent);

            SendEmailEvent emailEvent = new SendEmailEvent(
                    "noreply@unicauca.edu.co",
                    List.of(usuario.getEmail()),
                    "Bienvenido a la plataforma",
                    "user.created",
                    "Tu cuenta ha sido creada correctamente.",
                    Map.of("nombre", persona.getNombres())
            );
            notificationPublisher.publishEmail(emailEvent);

            log.info("Eventos publicados correctamente para usuario {}", usuario.getEmail());
        } catch (Exception e) {
            log.error("Error al publicar eventos para {}: {}", usuario.getEmail(), e.getMessage());
        }
    }
}

package co.edu.unicauca.frontend.infra.session;

import co.edu.unicauca.frontend.dto.SessionInfo;
import co.edu.unicauca.frontend.entities.enums.Rol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class SessionManagerTest {

    private SessionManager sessionManager;

    @BeforeEach
    void setUp() throws Exception {
        // Reiniciar la instancia singleton antes de cada prueba
        Field instanceField = SessionManager.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);

        sessionManager = SessionManager.getInstance();
    }

    @Test
    void testGetInstance_ReturnsSameInstance() {
        // Preparar y ejecutar
        SessionManager primeraInstancia = SessionManager.getInstance();
        SessionManager segundaInstancia = SessionManager.getInstance();

        // Verificar
        assertNotNull(primeraInstancia);
        assertNotNull(segundaInstancia);
        assertSame(primeraInstancia, segundaInstancia, "Debería retornar la misma instancia");
    }

    @Test
    void testGetCurrentSession_InitiallyNull() {
        // Ejecutar
        SessionInfo resultado = sessionManager.getCurrentSession();

        // Verificar
        assertNull(resultado, "La sesión actual debería ser nula inicialmente");
    }

    @Test
    void testSetCurrentSession() {
        // Preparar
        SessionInfo sessionInfo = new SessionInfo("test@unicauca.edu.co", "Usuario de Prueba", Rol.JEFE_DE_DEPARTAMENTO);

        // Ejecutar
        sessionManager.setCurrentSession(sessionInfo);
        SessionInfo resultado = sessionManager.getCurrentSession();

        // Verificar
        assertNotNull(resultado);
        assertEquals("test@unicauca.edu.co", resultado.email());
        assertEquals("Usuario de Prueba", resultado.nombres());
        assertEquals(Rol.JEFE_DE_DEPARTAMENTO, resultado.rolActivo());
        assertSame(sessionInfo, resultado, "Debería retornar el mismo objeto de sesión");
    }

    @Test
    void testClear() {
        // Preparar
        SessionInfo sessionInfo = new SessionInfo("usuario1@unicauca.edu.co", "Usuario Uno", Rol.ESTUDIANTE);
        sessionManager.setCurrentSession(sessionInfo);

        // Ejecutar
        sessionManager.clear();
        SessionInfo resultado = sessionManager.getCurrentSession();

        // Verificar
        assertNull(resultado, "La sesión actual debería ser nula después de clear");
    }

    @Test
    void testIsLoggedIn_WhenNoSession() {
        // Ejecutar
        boolean resultado = sessionManager.isLoggedIn();

        // Verificar
        assertFalse(resultado, "Debería retornar false cuando no hay sesión establecida");
    }

    @Test
    void testIsLoggedIn_WhenSessionExists() {
        // Preparar
        SessionInfo sessionInfo = new SessionInfo("usuario2@unicauca.edu.co", "Usuario Dos", Rol.DOCENTE);
        sessionManager.setCurrentSession(sessionInfo);

        // Ejecutar
        boolean resultado = sessionManager.isLoggedIn();

        // Verificar
        assertTrue(resultado, "Debería retornar true cuando existe una sesión");
    }

    @Test
    void testIsLoggedIn_AfterClear() {
        // Preparar
        SessionInfo sessionInfo = new SessionInfo("usuario3@unicauca.edu.co", "Usuario Tres", Rol.COORDINADOR);
        sessionManager.setCurrentSession(sessionInfo);
        sessionManager.clear();

        // Ejecutar
        boolean resultado = sessionManager.isLoggedIn();

        // Verificar
        assertFalse(resultado, "Debería retornar false después de clear");
    }

    @Test
    void testSessionLifecycle() {
        // Probar ciclo de vida completo
        assertNull(sessionManager.getCurrentSession());
        assertFalse(sessionManager.isLoggedIn());

        SessionInfo session = new SessionInfo("juan.perez@unicauca.edu.co", "Juan Pérez", Rol.ESTUDIANTE);
        sessionManager.setCurrentSession(session);

        assertNotNull(sessionManager.getCurrentSession());
        assertEquals("juan.perez@unicauca.edu.co", sessionManager.getCurrentSession().email());
        assertEquals("Juan Pérez", sessionManager.getCurrentSession().nombres());
        assertEquals(Rol.ESTUDIANTE, sessionManager.getCurrentSession().rolActivo());
        assertTrue(sessionManager.isLoggedIn());

        sessionManager.clear();

        assertNull(sessionManager.getCurrentSession());
        assertFalse(sessionManager.isLoggedIn());
    }

    @Test
    void testMultipleSetCurrentSession() {
        // Preparar
        SessionInfo primeraSession = new SessionInfo("usuario1@unicauca.edu.co", "Usuario Uno", Rol.DOCENTE);
        SessionInfo segundaSession = new SessionInfo("usuario2@unicauca.edu.co", "Usuario Dos", Rol.JEFE_DE_DEPARTAMENTO);

        // Ejecutar y verificar
        sessionManager.setCurrentSession(primeraSession);
        assertEquals("usuario1@unicauca.edu.co", sessionManager.getCurrentSession().email());
        assertEquals(Rol.DOCENTE, sessionManager.getCurrentSession().rolActivo());

        sessionManager.setCurrentSession(segundaSession);
        assertEquals("usuario2@unicauca.edu.co", sessionManager.getCurrentSession().email());
        assertEquals(Rol.JEFE_DE_DEPARTAMENTO, sessionManager.getCurrentSession().rolActivo());
        assertNotSame(primeraSession, sessionManager.getCurrentSession());
    }

    @Test
    void testSessionInfoRecordProperties() {
        // Probar que el record SessionInfo funciona correctamente
        SessionInfo session = new SessionInfo("test@unicauca.edu.co", "Usuario de Prueba", Rol.COORDINADOR);

        assertEquals("test@unicauca.edu.co", session.email());
        assertEquals("Usuario de Prueba", session.nombres());
        assertEquals(Rol.COORDINADOR, session.rolActivo());

        // Probar toString (proporcionado automáticamente por record)
        assertNotNull(session.toString());
        assertTrue(session.toString().contains("test@unicauca.edu.co"));
        assertTrue(session.toString().contains("Usuario de Prueba"));

        // Probar equals y hashCode (proporcionados automáticamente por record)
        SessionInfo mismaSession = new SessionInfo("test@unicauca.edu.co", "Usuario de Prueba", Rol.COORDINADOR);
        assertEquals(session, mismaSession);
        assertEquals(session.hashCode(), mismaSession.hashCode());

        // Probar sesión diferente
        SessionInfo sesionDiferente = new SessionInfo("otro@unicauca.edu.co", "Otro Usuario", Rol.ESTUDIANTE);
        assertNotEquals(session, sesionDiferente);
    }

    @Test
    void testAllRoles() {
        // Probar con todos los roles disponibles
        SessionInfo sesionEstudiante = new SessionInfo("estudiante@unicauca.edu.co", "Usuario Estudiante", Rol.ESTUDIANTE);
        SessionInfo sesionDocente = new SessionInfo("docente@unicauca.edu.co", "Usuario Docente", Rol.DOCENTE);
        SessionInfo sesionCoordinador = new SessionInfo("coordinador@unicauca.edu.co", "Usuario Coordinador", Rol.COORDINADOR);
        SessionInfo sesionJefe = new SessionInfo("jefe@unicauca.edu.co", "Usuario Jefe", Rol.JEFE_DE_DEPARTAMENTO);

        sessionManager.setCurrentSession(sesionEstudiante);
        assertEquals(Rol.ESTUDIANTE, sessionManager.getCurrentSession().rolActivo());

        sessionManager.setCurrentSession(sesionDocente);
        assertEquals(Rol.DOCENTE, sessionManager.getCurrentSession().rolActivo());

        sessionManager.setCurrentSession(sesionCoordinador);
        assertEquals(Rol.COORDINADOR, sessionManager.getCurrentSession().rolActivo());

        sessionManager.setCurrentSession(sesionJefe);
        assertEquals(Rol.JEFE_DE_DEPARTAMENTO, sessionManager.getCurrentSession().rolActivo());
    }

    @Test
    void testSessionManagerSingletonThreadSafety() {
        // Probar que múltiples llamadas a getInstance retornan el mismo objeto
        SessionManager instancia1 = SessionManager.getInstance();
        SessionManager instancia2 = SessionManager.getInstance();
        SessionManager instancia3 = SessionManager.getInstance();

        assertSame(instancia1, instancia2);
        assertSame(instancia2, instancia3);
        assertSame(instancia1, instancia3);
    }
}
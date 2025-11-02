package co.edu.unicauca.frontend.infra.session;

import co.edu.unicauca.frontend.dto.SessionInfo;

public class SessionManager {

    private static SessionManager instance;
    private SessionInfo currentSession;

    private SessionManager() { }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public SessionInfo getCurrentSession() {
        return currentSession;
    }

    public void setCurrentSession(SessionInfo session) {
        this.currentSession = session;
    }

    public void clear() {
        this.currentSession = null;
    }

    public boolean isLoggedIn() {
        return currentSession != null;
    }
}

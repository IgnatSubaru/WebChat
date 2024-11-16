package app.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import java.util.HashMap;
import java.util.Map;

@Component
public class WebSocketSessionManager {

    private static final Map<Integer, WebSocketSession> sessions = new HashMap<>();

    public void addSession(Integer userId, WebSocketSession session) {
        sessions.computeIfAbsent(userId, k->session);
    }

    public void removeSession(int userId) {
        sessions.remove(userId);
    }

    public WebSocketSession getSession(Integer userId) {
        return sessions.get(userId);
    }

    public int getSessionCount(){
        return sessions.size();
    }

    public Boolean contains(int id){
        return sessions.containsKey(id);
    }
}

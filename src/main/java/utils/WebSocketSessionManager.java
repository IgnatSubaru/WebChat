package utils;

import jakarta.websocket.Session;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;

public class WebSocketSessionManager {

    public final static WebSocketSessionManager INSTANCE = new WebSocketSessionManager();
    private static final Map<Integer, WebSocketSession> sessions = new HashMap<>();

    public static WebSocketSessionManager getInstance(){
        return INSTANCE;
    }

    public void addSession(Integer userId, WebSocketSession session) {
        sessions.computeIfAbsent(userId, k->session);
    }

    public void removeSession(int userId) {
        sessions.remove(userId);
    }

    public WebSocketSession getSession(Integer userId) {
        return sessions.get(userId);
    }

    public static int SessionCount(){
        return sessions.size();
    }

    public Boolean contains(int id){
        return sessions.containsKey(id);
    }
}

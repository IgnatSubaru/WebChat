package handler;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.util.UriTemplate;
import utils.WebSocketSessionManager;

import java.util.Map;

@Component
public class MyWebSocketHandler implements WebSocketHandler {

    WebSocketSession session;
    int userId;
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        this.session = session;
        String path = session.getUri().getPath();
        UriTemplate template = new UriTemplate("/api/webSocket/{userId}");
        Map<String, String> parameters = template.match(path);
        String userIdStr = parameters.get("userId");
        this.userId = Integer.parseInt(userIdStr);
        WebSocketSessionManager sessionManager = new WebSocketSessionManager();
        sessionManager.addSession(userId, session);
        System.out.println("after open - " + sessionManager.SessionCount());
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        if (message instanceof TextMessage && message != null) {
            TextMessage textMessage = (TextMessage) message;

            String messageStr = textMessage.getPayload();

            MessageHandler handler = new MessageHandler();
            handler.handleMessage(session, messageStr);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable throwable) throws Exception {
        throwable.printStackTrace();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        WebSocketSessionManager sessionManager = new WebSocketSessionManager();
        sessionManager.removeSession(userId);
        System.out.println("after close - " + sessionManager.SessionCount());
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}

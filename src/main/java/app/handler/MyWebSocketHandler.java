package app.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.util.UriTemplate;
import app.utils.WebSocketSessionManager;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class MyWebSocketHandler implements WebSocketHandler {

    private final WebSocketSessionManager sessionManager;
    private final MessageHandler messageHandler;

    @Override
    public void afterConnectionEstablished(WebSocketSession session){
        int userId = getUserIdFromSession(session);
        sessionManager.addSession(userId, session);
        System.out.println("after open - " + sessionManager.getSessionCount());
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message){
        if (message instanceof TextMessage textMessage && message != null) {

            String messageStr = textMessage.getPayload();

            messageHandler.handleMessage(session, messageStr);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable throwable){
        throwable.printStackTrace();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus){
        int userId = getUserIdFromSession(session);
        sessionManager.removeSession(userId);
        System.out.println("after close - " + sessionManager.getSessionCount());
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    public int getUserIdFromSession(WebSocketSession session){
        String path = session.getUri().getPath();
        UriTemplate template = new UriTemplate("/webSocket/{userId}");
        Map<String, String> parameters = template.match(path);
        String userIdStr = parameters.get("userId");
        int userId = Integer.parseInt(userIdStr);
        return userId;
    }
}

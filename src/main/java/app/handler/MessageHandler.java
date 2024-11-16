package app.handler;

import app.dto.MessageDTO;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import app.dao.ChatDAO;
import app.dao.MessageDAO;
import app.dao.UserChatDAO;
import app.dao.UserDAO;
import app.exception.DaoException;
import app.model.Chat;
import app.model.Message;
import app.model.User;
import app.model.UserChat;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import app.utils.WebSocketSessionManager;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Optional;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MessageHandler {

    private final ChatDAO chatDAO;
    private final UserChatDAO userChatDAO;
    private final UserDAO userDAO;
    private final MessageDAO messageDAO;
    private final WebSocketSessionManager sessionManager;

    public void handleMessage(WebSocketSession session, String message) {
        JSONObject jsonRequest = new JSONObject(message);

        System.out.println("Received message: " + message);

        String flag = jsonRequest.getString("flag");

        switch (flag) {
            case "FORWARD_MESSAGE" -> forwardMessage(jsonRequest);
            case "DELETE_CHAT" -> deleteChat(jsonRequest, session);
            case "CREATE_CHAT" -> createChat(jsonRequest, session);
            case "ADD_USER_TO_CHAT" -> addUserToChat(jsonRequest, session);
            case "DELETE_USER_FROM_CHAT" -> deleteUserFromChat(jsonRequest, session);
        }
    }

    private void forwardMessage(JSONObject jsonRequest){
        int userId = jsonRequest.getInt("userId");
        int chatId = jsonRequest.getInt("chatId");
        String text = jsonRequest.getString("text");

        Optional<User> userOptional = userDAO.findById(userId);
        User user = userOptional.get();
        Optional<Chat> chatOptional = chatDAO.findById(chatId);
        Chat chat = chatOptional.get();
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());

        try {
            messageDAO.save(
                    new Message(0, chat, user, text, currentTime)
            );
        } catch (SQLException e) {
            throw new DaoException(e);
        }

        String chatIdStr = String.valueOf(chat.getId());

        JsonObject jsonResponse = new JsonObject();

        jsonResponse.addProperty("flag", "FORWARD_MESSAGE");
        jsonResponse.addProperty("chatId", chatIdStr);
        jsonResponse.addProperty("userName", user.getName());
        jsonResponse.addProperty("text", text);

        List<User> users = userChatDAO.findMembersOfChat(chat);

        List<WebSocketSession> selectedSessions = new ArrayList<>();

        for (User chatUser: users) {
            if (sessionManager.contains(chatUser.getId())) {
                selectedSessions.add(sessionManager.getSession(chatUser.getId()));
            }
        }

        System.out.println(jsonResponse);
        String jsonResponseStr = jsonResponse.toString();

        for (WebSocketSession addressee : selectedSessions) {
            try {
                if(addressee.isOpen()) {
                    addressee.sendMessage(new TextMessage(jsonResponseStr));
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        System.out.println("message delivered to " + selectedSessions.size() + " members of chat " + chat.getId());
    }

    private void addUserToChat(JSONObject jsonRequest, WebSocketSession session) {
        int userId = jsonRequest.getInt("userId");
        int chatId = jsonRequest.getInt("chatId");

        Optional<User> userOptional = userDAO.findById(userId);
        User user = userOptional.get();
        Optional<Chat> chatOptional = chatDAO.findById(chatId);
        Chat chat = chatOptional.get();

        userChatDAO.save(
                new UserChat(0, user, chat)
        );

        JsonObject userResponse = new JsonObject();
        JsonObject messageJson = new JsonObject();
        JsonArray messagesJson = new JsonArray();

        List<MessageDTO> messages = messageDAO.findMessagesByChatId(chat);

        for(MessageDTO messageDTO: messages){
            messageJson.addProperty("sender", messageDTO.getSenderName());
            messageJson.addProperty("text", messageDTO.getText());
            messagesJson.add(messageJson);
            messageJson = new JsonObject();
        }

        JsonObject adminResponse = new JsonObject();
        adminResponse.addProperty("flag", "ADD_USER_TO_CHANNEL");
        adminResponse.addProperty("userId", userId);
        adminResponse.addProperty("chatId", chatId);

        try {
            if(session.isOpen()) {
                session.sendMessage(new TextMessage(adminResponse.toString()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        userResponse.addProperty("flag", "ADD_USER_TO_CHANNEL");
        userResponse.addProperty("id", chat.getId());
        userResponse.addProperty("title", chat.getName());
        userResponse.add("messages", messagesJson);

        try {
            if(sessionManager.getSession(userId) != null) {
                sessionManager.getSession(userId).sendMessage(new TextMessage(userResponse.toString()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void deleteUserFromChat(JSONObject jsonRequest, WebSocketSession session){
        int userId = jsonRequest.getInt("userId");
        int chatId = jsonRequest.getInt("chatId");

        Optional<User> userOptional = userDAO.findById(userId);
        User user = userOptional.get();
        Optional<Chat> chatOptional = chatDAO.findById(chatId);
        Chat chat = chatOptional.get();

        userChatDAO.deleteUserFromChat(
                new UserChat(0, user, chat)
        );


        JsonObject adminResponse = new JsonObject();
        adminResponse.addProperty("flag", "DELETE_USER_FROM_CHANNEL");
        adminResponse.addProperty("userId", userId);
        adminResponse.addProperty("chatId", chatId);

        try {
            if(session.isOpen()) {
                session.sendMessage(new TextMessage(adminResponse.toString()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JsonObject userResponse = new JsonObject();
        userResponse.addProperty("flag", "DELETE_USER_FROM_CHANNEL");
        userResponse.addProperty("chatId", chatId);

        try {
            if(sessionManager.getSession(userId) != null) {
                sessionManager.getSession(userId).sendMessage(new TextMessage(userResponse.toString()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void createChat(JSONObject jsonRequest, WebSocketSession session){
        String name = jsonRequest.getString("title");

        Chat chat = new Chat(name);
        chatDAO.save(chat);

        Optional<User> userOptional = userDAO.findByName("admin");
        User user = userOptional.get();

        userChatDAO.save(
                new UserChat(0, user, chat)
        );

        JsonObject adminResponse = new JsonObject();
        adminResponse.addProperty("flag", "CREATE_CHANNEL");
        adminResponse.addProperty("chatId", chat.getId());
        adminResponse.addProperty("title", chat.getName());

        try {
            if(session.isOpen()) {
                session.sendMessage(new TextMessage(adminResponse.toString()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteChat(JSONObject jsonRequest, WebSocketSession session){
        int chatId = jsonRequest.getInt("chatId");

        Optional<Chat> chatOptional = chatDAO.findById(chatId);
        Chat chat = chatOptional.get();

        List<User> userIds = userChatDAO.findMembersOfChat(chat);

        List<WebSocketSession> selectedSessions = new ArrayList<>();

        for (User chatUser: userIds) {
            if (sessionManager.contains(chatUser.getId())) {
                selectedSessions.add(sessionManager.getSession(chatUser.getId()));
            }
        }

        JsonObject userResponse = new JsonObject();
        userResponse.addProperty("flag", "DELETE_CHANNEL");
        userResponse.addProperty("chatId", chatId);

        for (WebSocketSession addressee : selectedSessions) {
            try {
                if(addressee.isOpen()) {
                    addressee.sendMessage(new TextMessage(userResponse.toString()));
                }
            }catch(IOException e){
                e.printStackTrace();
            }
            System.out.println("message delivered to " + selectedSessions.size() + "members of chat " + chat.getId());
        }

        JsonObject adminResponse = new JsonObject();
        adminResponse.addProperty("flag", "DELETE_CHANNEL");
        adminResponse.addProperty("chatId", chatId);

        JsonArray userIdsJson = new JsonArray();
        for (User chatUser: userIds) {
            if(chatUser.getId() != 3) {
                userIdsJson.add(chatUser.getId());
            }
        }

        adminResponse.add("userIds", userIdsJson);

        try {
            if(session.isOpen()) {
                session.sendMessage(new TextMessage(adminResponse.toString()));
            }
        }catch(IOException e){
            e.printStackTrace();
        }

        userChatDAO.deleteChat(chat);

        messageDAO.deleteByChatId(chat);

        chatDAO.delete(chat);

    }
}

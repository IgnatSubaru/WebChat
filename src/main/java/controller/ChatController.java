package controller;

import com.google.gson.JsonArray;

import com.google.gson.JsonObject;
import dao.ChatDAO;
import dao.MessageDAO;
import dao.UserChatDAO;
import dao.UserDAO;
import jakarta.servlet.http.HttpServlet;
import model.Chat;
import model.Message;
import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ChatController extends HttpServlet {

    private final ChatDAO chatDAO;

    @Autowired
    public ChatController(ChatDAO chatDAO){
        this.chatDAO = chatDAO;
    }

    @GetMapping("/allChats")
    public List<Chat> getAllChats(){
        return chatDAO.findAll();
    }

    @GetMapping("/userChats/user_id={id}")
    public List<Chat> getUserChats(@PathVariable("id") int id){
        return UserChatDAO.findChatsByUserId(id);
    }

    @GetMapping("/messages/chat_id={id}")
    public List<Message> getChatMessages(@PathVariable("id") int id){
        return MessageDAO.findMessagesByChatId(id);
    }

    @PostMapping("/app")
    @ResponseBody
    public JsonObject getAllUserChats(@RequestBody JsonObject jsonObject) throws IOException {

        int id = jsonObject.get("userId").getAsInt();

        JsonObject jsonResponse = new JsonObject();

        if (id != 0) {

            Optional<User> userOptional = UserDAO.findById(id);
            User user = userOptional.get();

            List<Chat> chats = UserChatDAO.findChatsByUserId(id);
            List<Message> messages = new ArrayList<>();

            JsonObject messageJson = new JsonObject();
            JsonArray messagesJsonArray = new JsonArray();

            JsonObject chatJson = new JsonObject();
            JsonArray chatsJsonArray = new JsonArray();

            JsonObject adminData = new JsonObject();

            JsonObject userJson = new JsonObject();
            JsonArray usersJsonArray = new JsonArray();


            //формирование JSON для отправки
            jsonResponse.addProperty("userName", user.getName());
            jsonResponse.addProperty("isAdmin", user.isAdmin());

            if(user.isAdmin()){
                List<User> allUsers = UserDAO.findAll();
                allUsers.remove(user);
                for(User user1: allUsers){
                    userJson.addProperty("id", user1.getId());
                    userJson.addProperty("name", user1.getName());
                    List<Chat> userChats = UserChatDAO.findChatsByUserId(user1.getId());
                    for(Chat chat: userChats){
                        chatJson.addProperty("id", chat.getId());
                        chatJson.addProperty("title", chat.getName());
                        chatsJsonArray.add(chatJson);
                        chatJson = new JsonObject();
                    }
                    userJson.add("channels", chatsJsonArray);
                    chatsJsonArray = new JsonArray();
                    usersJsonArray.add(userJson);
                    userJson = new JsonObject();
                }

                adminData.add("allUsers", usersJsonArray);

                List<Chat> allChats = chatDAO.findAll();
                for(Chat chat: allChats){
                    messages = MessageDAO.findMessagesByChatId(chat.getId());
                    for(Message message: messages){
                        messageJson.addProperty("sender", message.getUser().getName());
                        messageJson.addProperty("text", message.getText());
                        messagesJsonArray.add(messageJson);
                        messageJson = new JsonObject();
                    }
                    chatJson.addProperty("id", chat.getId());
                    chatJson.addProperty("title", chat.getName());
                    chatJson.add("messages", messagesJsonArray);
                    chatsJsonArray.add(chatJson);
                    chatJson = new JsonObject();
                    messagesJsonArray = new JsonArray();
                }
                adminData.add("allChannels", chatsJsonArray);
            }

            chatsJsonArray = new JsonArray();

            jsonResponse.add("adminData", adminData);

            for(Chat chat: chats){
                messages = MessageDAO.findMessagesByChatId(chat.getId());
                for(Message message: messages){
                    messageJson.addProperty("sender", message.getUser().getName());
                    messageJson.addProperty("text", message.getText());
                    messagesJsonArray.add(messageJson);
                    messageJson = new JsonObject();
                }
                chatJson.addProperty("id", chat.getId());
                chatJson.addProperty("title", chat.getName());
                chatJson.add("messages", messagesJsonArray);
                chatsJsonArray.add(chatJson);
                chatJson = new JsonObject();
                messagesJsonArray = new JsonArray();
            }
            jsonResponse.add("userChannels", chatsJsonArray);
            return jsonResponse;
        } else {
            // Если параметр userId не указан, возвращаем ошибку 400 (Bad Request)
            JsonObject badRequestJson = new JsonObject();
            badRequestJson.addProperty("error", "UserId missing");
            return badRequestJson;
        }

        /*try {

        } catch (JSONException e) {
            System.out.println(jsonResponse);
            e.printStackTrace();
            JsonObject errorJson = new JsonObject();
            errorJson.addProperty("error", "An error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorJson);
        }*/
    }

    @PostMapping("/createChat")
    @ResponseBody
    public ResponseEntity<Chat> createChat(@RequestBody Chat chat) throws SQLException {
        return ResponseEntity.status(HttpStatus.OK).body(chatDAO.save(chat));
    }


    @PutMapping("/updateChat")
    @ResponseBody
    public ResponseEntity<Chat> updateChat(@RequestBody Chat chat){
        return ResponseEntity.status(HttpStatus.OK).body(chatDAO.update(chat));
    }

    @DeleteMapping("/deleteChat")
    @ResponseBody
    public boolean deleteChat(@RequestBody Chat chat){
        return chatDAO.delete(chat);
    }

}

package app.controller;


import app.dao.ChatDAO;
import app.dao.MessageDAO;
import app.dao.UserChatDAO;
import app.dao.UserDAO;
import app.dto.MessageDTO;
import app.dto.UserDTO;
import jakarta.servlet.http.HttpServlet;
import app.model.Chat;
import app.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/WebChat")
@RequiredArgsConstructor
public class ChatController extends HttpServlet {

    private final ChatDAO chatDAO;
    private final UserChatDAO userChatDAO;
    private final MessageDAO messageDAO;
    private final UserDAO userDAO;

    @GetMapping("/allChats")
    public List<Chat> getAllChats(){
        return chatDAO.findAll();
    }

    @GetMapping("/allUsers")
    public List<UserDTO> getAllUsers(){
        return userDAO.findAll();
    }

    @GetMapping("/user_id={id}/chats")
    public List<Chat> getUserChats(@PathVariable("id") int id){
        Optional<User> user = userDAO.findById(id);
        return userChatDAO.findChatsByUser(user.get());
    }

    @GetMapping("/chat_id={id}/messages")
    public List<MessageDTO> getChatMessages(@PathVariable("id") int id){
        Chat chat = chatDAO.findById(id).get();
        return messageDAO.findMessagesByChatId(chat);
    }

    @PostMapping("/createChat")
    @ResponseBody
    public ResponseEntity<Chat> createChat(@RequestBody Chat chat){
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

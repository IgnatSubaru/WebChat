package app.controller;


import app.dto.MessageDTO;
import app.dto.UserDTO;
import app.service.ChatService;
import app.service.MessageService;
import app.service.UserChatService;
import app.service.UserService;
import jakarta.servlet.http.HttpServlet;
import app.model.Chat;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/WebChat")
@RequiredArgsConstructor
public class ChatController extends HttpServlet {

    private final ChatService chatService;
    private final UserChatService userChatService;
    private final MessageService messageService;
    private final UserService userService;

    @GetMapping("/allChats")
    public  ResponseEntity<List<Chat>> getAllChats(){
        return ResponseEntity.status(HttpStatus.OK).body(chatService.getAllChats());
    }

    @GetMapping("/allUsers")
    public ResponseEntity<List<UserDTO>> getAllUsers(){
        return ResponseEntity.status(HttpStatus.OK).body(userService.getAllUsers());
    }

    @GetMapping("/user_id={id}/chats")
    public ResponseEntity<List<Chat>> getUserChats(@PathVariable("id") int id){
        return ResponseEntity.status(HttpStatus.OK).body(userChatService.getChatsByUserId(id));
    }

    @GetMapping("/chat_id={id}/messages")
    public ResponseEntity<List<MessageDTO>> getChatMessages(@PathVariable("id") int id){
        return ResponseEntity.status(HttpStatus.OK).body(messageService.getMessagesByChatId(id));
    }

    @PostMapping("/createChat")
    @ResponseBody
    public ResponseEntity<Chat> createChat(@RequestBody Chat chat){
        return ResponseEntity.status(HttpStatus.OK).body(chatService.create(chat));
    }

    @PutMapping("/updateChat")
    @ResponseBody
    public ResponseEntity<Chat> updateChat(@RequestBody Chat chat){
        return ResponseEntity.status(HttpStatus.OK).body(chatService.update(chat));
    }

    @DeleteMapping("/deleteChat")
    @ResponseBody
    public ResponseEntity deleteChat(@RequestBody Chat chat){
        return ResponseEntity.status(HttpStatus.OK).body(chatService.delete(chat));
    }

}

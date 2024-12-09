package app.service;

import app.dao.MessageDAO;
import app.dto.MessageDTO;
import app.model.Chat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageDAO messageDAO;
    private final ChatService chatService;

    public List<MessageDTO> getMessagesByChatId(int id){
        Chat chat = chatService.getChatById(id).get();
        return messageDAO.findMessagesByChat(chat);
    }
}

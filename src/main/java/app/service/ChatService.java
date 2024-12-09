package app.service;

import app.dao.ChatDAO;
import app.model.Chat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatDAO chatDAO;

    public List<Chat> getAllChats(){
        return chatDAO.findAll();
    }

    public Chat create(Chat chat){
        return chatDAO.save(chat);
    }

    public Chat update(Chat chat) {
        return chatDAO.update(chat);
    }

    public boolean delete(Chat chat) {
        return chatDAO.delete(chat);
    }

    public Optional<Chat> getChatById(int id){
        return chatDAO.findById(id);
    }

    public boolean isChatExisting(int id){
        Optional<Chat> chatOptional = chatDAO.findById(id);
        if(chatOptional.isPresent()){
            return true;
        }else{
            return false;
        }
    }


}

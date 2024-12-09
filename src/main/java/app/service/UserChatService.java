package app.service;

import app.dao.UserChatDAO;
import app.model.Chat;
import app.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserChatService {
    private final UserChatDAO userChatDAO;
    private final UserService userService;

    public List<Chat> getChatsByUserId(int id){
        User user = userService.getUserById(id);
        return userChatDAO.findChatsByUser(user);
    }
}

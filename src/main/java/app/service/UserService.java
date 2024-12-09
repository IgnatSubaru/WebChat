package app.service;

import app.dao.UserDAO;
import app.dto.UserDTO;
import app.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDAO userDAO;

    public List<UserDTO> getAllUsers(){
        return userDAO.findAll();
    }

    public User getUserById(int id){
        return userDAO.findById(id).get();
    }
}

package app.controller;

import com.google.gson.JsonObject;
import app.dto.dao.UserDAO;
import app.model.User;
import java.util.Map;
import java.util.Optional;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserDAO userDAO;

    public AuthController(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @PostMapping(value = "/login",
            consumes = "application/json",
            produces = "application/json")
    public ResponseEntity<String> login(@RequestBody Map<String, String> requestBody) {

        String name = requestBody.get("userName");
        String password = requestBody.get("password");

        JsonObject json = new JsonObject();

        Optional<User> userOptional = userDAO.findByName(name);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getPassword().equals(password)) {
                json.addProperty("userId", user.getId());
                return ResponseEntity.ok(json.toString());
            } else {
                json.addProperty("error", "неверный пароль");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(json.toString());
            }
        } else {
            json.addProperty("error", "пользователь не найден");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(json.toString());
        }
    }

    @PostMapping(value = "/register",
            consumes = "application/json",
            produces = "application/json")
    public ResponseEntity<String> register(@RequestBody Map<String, String> requestBody) {
        String name = requestBody.get("userName");
        String password = requestBody.get("password");

        JsonObject json = new JsonObject();

        try {
            Optional<User> existingUser = userDAO.findByName(name);
            if (existingUser.isEmpty()) {
                User user = new User(0, name, password, false);
                user = userDAO.save(user);

                json.addProperty("userId", user.getId());
                return ResponseEntity.ok(json.toString());
            } else {
                json.addProperty(
                        "error",
                        "User with name " + name + " already exists"
                );
                return ResponseEntity.status(HttpStatus.CONFLICT).body(json.toString());
            }
        } catch (Exception e) {
            json.addProperty("error", "Server error");
            return ResponseEntity.
                    status(HttpStatus.INTERNAL_SERVER_ERROR).
                    body(json.toString());
        }
    }
}

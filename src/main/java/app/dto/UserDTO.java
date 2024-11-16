package app.dto;

import lombok.Getter;

@Getter
public class UserDTO {
    private String name;
    private boolean isAdmin;

    public UserDTO(String name, boolean isAdmin){
        this.name = name;
        this.isAdmin = isAdmin;
    }
}

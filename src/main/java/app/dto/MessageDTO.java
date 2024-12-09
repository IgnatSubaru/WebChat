package app.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageDTO {
    private String text;
    private String senderName;

    public MessageDTO(String text, String senderName){
        this.text = text;
        this.senderName = senderName;
    }
}

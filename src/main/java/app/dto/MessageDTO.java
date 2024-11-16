package app.dto;

import lombok.Getter;

@Getter
public class MessageDTO {
    private String text;
    private String senderName;

    public MessageDTO(String text, String senderName){
        this.text = text;
        this.senderName = senderName;
    }
}

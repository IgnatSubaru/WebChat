package app.model;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chats", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;

    public Chat(String name){
        this.name = name;
    }
}

package app.dto.dao;

import jakarta.persistence.EntityManager;
import app.model.Chat;
import app.model.User;
import app.model.UserChat;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserChatDAO {

    private final EntityManager entityManager;
    @Autowired
    public UserChatDAO(EntityManager entityManager){
        this.entityManager = entityManager;
    }

    @Transactional
    public UserChat save(UserChat userChat){
        entityManager.persist(userChat);
        return userChat;
    }

    public List<Chat> findChatsByUser(User user){
        String jpql = "SELECT uc.chat FROM UserChat uc WHERE uc.user = :user";
        return entityManager.createQuery(jpql, Chat.class)
                .setParameter("user", user)
                .getResultList();
    }

    public List<User> findMembersOfChat(Chat chat){
        String jpql = "SELECT uc.user FROM UserChat uc WHERE uc.chat = :chat";
        return entityManager.createQuery(jpql, User.class)
                .setParameter("chat", chat)
                .getResultList();
    }

    @Transactional
    public boolean deleteUserFromChat(UserChat userChat){
        try {
            UserChat existingUserChat = entityManager.merge(userChat);
            entityManager.remove(existingUserChat);
            return true;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Transactional
    public boolean deleteChat(Chat chat){
        try {
            String jpql = "DELETE FROM UserChat uc WHERE uc.chat = :chat";
            entityManager.createQuery(jpql)
                    .setParameter("chat", chat)
                    .executeUpdate();
            return true;
        }catch(IllegalArgumentException e){
            return false;
        }
    }
}
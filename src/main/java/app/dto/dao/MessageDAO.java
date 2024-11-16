package app.dto.dao;

import app.dto.MessageDTO;
import app.model.Chat;
import app.model.Message;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MessageDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Message save(Message message) throws SQLException {
        entityManager.persist(message);
        return message;
    }

    public List<MessageDTO> findMessagesByChatId(Chat chat){
        List<MessageDTO> messages = new ArrayList<>();
        try {
            String jpql = "SELECT new app.dto.MessageDTO(m.text, m.user.name) FROM Message m WHERE m.chat = :chat";
            TypedQuery<MessageDTO> query = entityManager.createQuery(jpql, MessageDTO.class);
            query.setParameter("chat", chat);
            messages = query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return messages;
    }

    @Transactional
    public boolean deleteByChatId(Chat chat){
        try {
            String jpql = "DELETE FROM Message m WHERE m.chat = :chat";
            int deletedCount = entityManager.createQuery(jpql)
                    .setParameter("chat", chat)
                    .executeUpdate();
            return deletedCount > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

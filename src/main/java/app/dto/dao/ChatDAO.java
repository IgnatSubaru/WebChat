package app.dto.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import app.model.Chat;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public class ChatDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Chat save(Chat chat){
        entityManager.persist(chat);
        return chat;
    }

    public Optional<Chat> findById(int id){
        Chat chat = entityManager.find(Chat.class, id);
        return Optional.ofNullable(chat);
    }

    public List<Chat> findAll(){
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Chat> criteriaQuery = criteriaBuilder.createQuery(Chat.class);
        criteriaQuery.from(Chat.class);
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    @Transactional
    public Chat update(Chat chat){
        entityManager.merge(chat);
        return chat;
    }

    @Transactional
    public boolean delete(Chat chat){
        Chat existingChat = findById(chat.getId()).orElse(null);
        if (existingChat != null) {
            entityManager.remove(existingChat);
            return true;
        } else {
            System.out.println("This chat doesn't exist");
            return false;
        }
    }

}

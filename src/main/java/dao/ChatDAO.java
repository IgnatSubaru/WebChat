package dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import model.Chat;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import utils.SessionFactoryProvider;

import java.util.List;
import java.util.Optional;


@Repository
public class ChatDAO {

    @Autowired
    private SessionFactory sessionFactory = SessionFactoryProvider.provideSessionFactory();

    public Chat save(Chat chat){
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        session.save(chat);

        session.getTransaction().commit();
        return chat;
    }

    public Optional<Chat> findById(int id){
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        Chat chat = session.get(Chat.class, id);

        session.getTransaction().commit();
        return Optional.ofNullable(chat);
    }

    public List<Chat> findAll(){
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<Chat> criteriaQuery = criteriaBuilder.createQuery(Chat.class);
        criteriaQuery.from(Chat.class);
        List<Chat> chats = session.createQuery(criteriaQuery).getResultList();

        session.getTransaction().commit();
        return chats;
    }


    public Chat update(Chat chat){
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        session.update(chat);

        session.getTransaction().commit();
        return chat;
    }

    public boolean delete(Chat chat){
        if(findById(chat.getId()).get() != null) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();

            session.delete(chat);

            session.getTransaction().commit();
            return true;
        }else{
            System.out.println("Such as chat doesn't exist");
            return false;
        }
    }

}

package utils;

import dao.ChatDAO;
import dao.MessageDAO;
import dao.UserChatDAO;
import dao.UserDAO;
import model.Chat;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;

import java.sql.SQLException;
import java.util.Optional;

public class JdbcRunner {
    public static void main(String args[]) throws SQLException{

        /*var sessionFactory = SessionFactoryProvider.provideSessionFactory();

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        session.save(new Chat(1, "qweqwe"));

        session.getTransaction().commit();*/

        ChatDAO chatDAO = new ChatDAO();

        System.out.println(chatDAO.findAll());
    }
}

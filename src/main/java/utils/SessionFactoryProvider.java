package utils;

import model.Chat;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.stereotype.Component;

@Component
public class SessionFactoryProvider {

    public static SessionFactory provideSessionFactory(){
        Configuration configuration = new Configuration();
        configuration.configure();
        configuration.addAnnotatedClass(Chat.class);
        return configuration.buildSessionFactory();
    }

}

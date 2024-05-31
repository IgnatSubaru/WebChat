package dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Configuration
public class ConnectionManager {
    private static final String URL = "jdbc:postgresql://localhost:3000/webapp";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "networksize2203";

    @Bean
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}

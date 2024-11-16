package app.dto.dao;

import app.dto.UserDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import app.model.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserDAO {

    private final EntityManager entityManager;

    @Transactional
    public User save(User user){
        entityManager.persist(user);
        return user;
    }

    public List<UserDTO> findAll(){
        List<UserDTO> users = new ArrayList<>();
        try {
            String jpql = "SELECT new app.dto.UserDTO(u.name, u.isAdmin) FROM User u";
            TypedQuery<UserDTO> query = entityManager.createQuery(jpql, UserDTO.class);
            users = query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    public Optional<User> findById(int id){
        User user = entityManager.find(User.class, id);
        return Optional.ofNullable(user);
    }

    public Optional<User> findByName(String name){
        try {
            String jpql = "SELECT u FROM User u WHERE u.name = :name";
            User user = entityManager.createQuery(jpql, User.class)
                    .setParameter("name", name)
                    .getSingleResult();
            return Optional.of(user);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

}

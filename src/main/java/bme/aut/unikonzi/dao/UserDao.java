package bme.aut.unikonzi.dao;

import bme.aut.unikonzi.model.User;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Optional;

public interface UserDao {

    User insert(User user);

    List<User> findAll(int page, int limit);

    Optional<User> findById(ObjectId id);

    int deleteById(ObjectId id);

    int setAsAdminById(ObjectId id);

    Optional<User> findByEmail(String email);
}

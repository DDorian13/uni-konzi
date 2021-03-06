package bme.aut.unikonzi.dao;

import bme.aut.unikonzi.model.User;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Optional;

public interface UserDao extends CommonDao<User> {
    
    int setAsAdminById(ObjectId id);

    Optional<User> findByEmail(String email);

    Optional<User> findByName(String name);
}

package bme.aut.unikonzi.dao;

import bme.aut.unikonzi.model.User;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Optional;

public interface UserDao {

    int insertUser(ObjectId id, User user);

    default int insertUser(User user) {
        ObjectId id = ObjectId.get();
        return insertUser(id, user);
    }

    List<User> selectAllUsers();

    Optional<User> selectUserById(ObjectId id);

    int deleteUserById(ObjectId id);

    int updateUserById(ObjectId id, User user);
}

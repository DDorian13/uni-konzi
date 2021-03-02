package bme.aut.unikonzi.service;

import bme.aut.unikonzi.dao.MongoUserDataAccess;
import bme.aut.unikonzi.dao.UserDao;
import bme.aut.unikonzi.model.User;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserDao userRepository;
    //private final MongoUserDataAccess userRepository;

    @Autowired
    public UserService(UserDao userRepository) {
        this.userRepository = userRepository;
    }

    public User addUser(User user) {
        String email = user.getEmail();
        if (userRepository.findByEmail(email).isPresent()) {
            return null;
        }
        return userRepository.insert(user);
    }

    public List<User> getAllUsers(int page, int limit) {
        return userRepository.findAll(page, limit);
    }

    public Optional<User> getUserById(ObjectId id) {
        return userRepository.findById(id);
    }

    public int deleteUser(ObjectId id) {
        return userRepository.deleteById(id);
    }

    public int setUserAsAdmin(ObjectId id) {
        return userRepository.setAsAdminById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}

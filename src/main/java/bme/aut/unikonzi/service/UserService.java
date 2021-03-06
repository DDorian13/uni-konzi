package bme.aut.unikonzi.service;

import bme.aut.unikonzi.dao.UserDao;
import bme.aut.unikonzi.model.User;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserDao userRepository;

    @Autowired
    public UserService(UserDao userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> addUser(User user) {
        String email = user.getEmail();
        if (userRepository.findByEmail(email).isPresent()) {
            return Optional.empty();
        }
        return Optional.of(userRepository.insert(user));
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

    public Optional<User> getUserByName(String name) {
        return userRepository.findByName(name);
    }

    public Optional<User> loginUser(String email, String password) {
        Optional<User> user = getUserByEmail(email);
        if (user.isEmpty() || !user.get().getPassword().equals(password)) {
            return Optional.empty();
        }
        return user;
    }
}

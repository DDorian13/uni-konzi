package bme.aut.unikonzi.service;

import bme.aut.unikonzi.dao.MongoUserDataAccess;
import bme.aut.unikonzi.dao.UserDao;
import bme.aut.unikonzi.model.User;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    //private final UserDao userDao;
    private final MongoUserDataAccess userRepository;

    @Autowired
    public UserService(MongoUserDataAccess userRepository) {
        this.userRepository = userRepository;
    }

    public User addUser(User user) {
        return userRepository.insert(user);
        //return userDao.insertUser(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
        //return userDao.selectAllUsers();
    }

    public Optional<User> getUserById(ObjectId id) {
        return userRepository.findById(id);
        //return userDao.selectUserById(id);
    }

    public int deleteUser(ObjectId id) {
        userRepository.deleteById(id);
        return 1;
        //return userDao.deleteUserById(id);
    }

    public int updateUser(ObjectId id, User newUser) {
        userRepository.save(new User(id, newUser.getName(), newUser.getEmail(), newUser.getPassword()));
        return 1;
        //return userDao.updateUserById(id, newUser);
    }

    public int setUserAsAdmin(ObjectId id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null)
            return 0;
        user.setRole(User.Role.Admin);
        userRepository.save(user);
        return 1;
    }
}

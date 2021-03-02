package bme.aut.unikonzi.dao;

import bme.aut.unikonzi.model.User;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class MongoUserDao implements UserDao{

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public User insert(User user) {
        user = mongoTemplate.insert(user);
        return user;
    }

    @Override
    public List<User> findAll(int page, int limit) {
        int fromIndex = (page - 1) * limit;
        int toIndex = fromIndex + limit;
        var all = mongoTemplate.findAll(User.class);
        if (fromIndex >= all.size() || fromIndex < 0) {
            return Collections.emptyList();
        }
        if (toIndex > all.size()) {
            toIndex = all.size();
        }
        return all.subList(fromIndex, toIndex);
    }

    @Override
    public Optional<User> findById(ObjectId id) {
        User user = mongoTemplate.findById(id, User.class);
        if (user == null)
            return Optional.empty();
        return Optional.of(user);
    }

    @Override
    public int deleteById(ObjectId id) {
        User userToDelete = mongoTemplate.findById(id, User.class);
        if (userToDelete == null)
            return 0;
        mongoTemplate.remove(userToDelete);
        return 1;
    }

    @Override
    public int setAsAdminById(ObjectId id) {
        User user = mongoTemplate.findById(id, User.class);
        if (user == null)
            return 0;
        user.setRole(User.Role.Admin);
        mongoTemplate.save(user);
        return 1;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        Query query = new Query();
        query.addCriteria(Criteria.where("email").is(email));
        User user = mongoTemplate.findOne(query, User.class);
        return (user != null) ? Optional.of(user) : Optional.empty();
    }
}

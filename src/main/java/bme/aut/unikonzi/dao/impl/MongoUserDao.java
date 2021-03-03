package bme.aut.unikonzi.dao.impl;

import bme.aut.unikonzi.dao.UserDao;
import bme.aut.unikonzi.model.User;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.Optional;

@Repository
public class MongoUserDao extends MongoCommonDao<User> implements UserDao {

    private static String collectionName = "users";

    @Autowired
    private MongoTemplate mongoTemplate;

    public MongoUserDao() {
        super(User.class, collectionName);
    }

    @PostConstruct
    public void init() {
        super.setMongoTemplate(mongoTemplate);
    }

    @Override
    public int setAsAdminById(ObjectId id) {
        User user = mongoTemplate.findById(id, User.class, collectionName);
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
        User user = mongoTemplate.findOne(query, User.class, collectionName);
        return (user != null) ? Optional.of(user) : Optional.empty();
    }
}

package bme.aut.unikonzi.dao;

import bme.aut.unikonzi.model.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoUserDataAccess extends MongoRepository<User, ObjectId> {
}

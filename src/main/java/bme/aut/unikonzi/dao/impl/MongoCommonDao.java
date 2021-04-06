package bme.aut.unikonzi.dao.impl;

import bme.aut.unikonzi.dao.CommonDao;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class MongoCommonDao<T> implements CommonDao<T> {

    private final Class<T> typeParameterClass;
    private MongoTemplate mongoTemplate;
    private String collection;

    public MongoCommonDao(Class<T> typeParameterClass, String collection) {
        this.typeParameterClass = typeParameterClass;
        this.collection = collection;
    }

    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        if (this.mongoTemplate == null) {
            this.mongoTemplate = mongoTemplate;
        }
    }

    public T insert(T template) {
        template = mongoTemplate.insert(template, collection);
        return template;
    }

    public List<T> findAll(int page, int limit) {
        int fromIndex = (page - 1) * limit;
        Query query = new Query();
        query.skip(fromIndex);
        query.limit(limit);

        return mongoTemplate.find(query, typeParameterClass, collection);
    }

    public Optional<T> findById(ObjectId id) {
        T template = mongoTemplate.findById(id, typeParameterClass, collection);
        if (template == null)
            return Optional.empty();
        return Optional.of(template);
    }

    public int deleteById(ObjectId id) {
        T template = mongoTemplate.findById(id, typeParameterClass, collection);
        if (template == null)
            return 0;
        mongoTemplate.remove(template, collection);
        return 1;
    }

}

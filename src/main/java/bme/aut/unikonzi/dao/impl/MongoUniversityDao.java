package bme.aut.unikonzi.dao.impl;

import bme.aut.unikonzi.dao.UniversityDao;
import bme.aut.unikonzi.model.University;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class MongoUniversityDao extends MongoCommonDao<University> implements UniversityDao {

    private static String collectionName = "universities";

    @Autowired
    private MongoTemplate mongoTemplate;

    public MongoUniversityDao() {
        super(University.class, collectionName);
    }

    @PostConstruct
    public void init() {
        super.setMongoTemplate(mongoTemplate);
    }

    @Override
    public Optional<University> findByName(String name) {
        Query query = new Query();
        query.addCriteria(Criteria.where("name").is(name));
        University uniMaybe = mongoTemplate.findOne(query, University.class, collectionName);
        return (uniMaybe == null) ? Optional.empty() : Optional.of(uniMaybe);
    }

    @Override
    public Optional<University> updateById(ObjectId id, University university) {
        Optional<University> uniById = super.findById(id);
        if (uniById.isEmpty()) {
            return Optional.empty();
        }
        university.setId(id);
        mongoTemplate.save(university, collectionName);
        return Optional.of(university);
    }

    @Override
    public List<University> findByNameRegex(String name, int page, int limit) {
        Query query = new Query();
        query.addCriteria(Criteria.where("name").regex(name, "i"));
        int fromIndex = (page - 1) * limit;
        query.skip(fromIndex);
        query.limit(limit);
        return mongoTemplate.find(query, University.class, collectionName);
    }
}

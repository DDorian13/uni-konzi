package bme.aut.unikonzi.dao.impl;

import bme.aut.unikonzi.dao.SubjectDao;
import bme.aut.unikonzi.model.Subject;
import bme.aut.unikonzi.model.User;
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
public class MongoSubjectDao extends MongoCommonDao<Subject> implements SubjectDao {

    private static String collectionName = "subjects";

    @Autowired
    private MongoTemplate mongoTemplate;

    public MongoSubjectDao() {
        super(Subject.class, collectionName);
    }

    @PostConstruct
    public void init() {
        super.setMongoTemplate(mongoTemplate);
    }

    @Override
    public Optional<Subject> updateById(ObjectId id, Subject subject) {
        Optional<Subject> subjectById = super.findById(id);
        if (subjectById.isEmpty()) {
            return subjectById;
        }
        subject.setId(id);
        mongoTemplate.save(subject, collectionName);
        return Optional.of(subject);
    }

    @Override
    public List<Subject> findByNameRegex(String name, int page, int limit) {
        Query query = new Query();
        query.addCriteria(Criteria.where("name").regex(name, "i"));
        int fromIndex = (page - 1) * limit;
        query.skip(fromIndex);
        query.limit(limit);
        List<Subject> subjects = mongoTemplate.find(query, Subject.class, collectionName);
        return subjects;
    }

    @Override
    public List<Subject> findByCodeRegex(String code, int page, int limit) {
        Query query = new Query();
        query.addCriteria(Criteria.where("code").regex(code, "i"));
        int fromIndex = (page - 1) * limit;
        query.skip(fromIndex);
        query.limit(limit);
        List<Subject> subjects = mongoTemplate.find(query, Subject.class, collectionName);
        return subjects;
    }

    @Override
    public List<Subject> containsTutorOrPupil(String property, User user, int page, int limit) {
        Query query = new Query();
        if (!property.equals("tutors") && !property.equals("pupils")) {
            return Collections.emptyList();
        }
        query.addCriteria(Criteria.where(property).is(user));
        int fromIndex = (page - 1) * limit;

        query.skip(fromIndex);
        query.limit(limit);

        List<Subject> subjects = mongoTemplate.find(query, Subject.class, collectionName);
        return subjects;
    }


}
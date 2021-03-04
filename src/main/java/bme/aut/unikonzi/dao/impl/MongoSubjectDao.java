package bme.aut.unikonzi.dao.impl;

import bme.aut.unikonzi.dao.SubjectDao;
import bme.aut.unikonzi.model.Subject;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Optional;

public class MongoSubjectDao implements SubjectDao {

    @Override
    public Subject insert(Subject template) {
        return null;
    }

    @Override
    public List<Subject> findAll(int page, int limit) {
        return null;
    }

    @Override
    public Optional<Subject> findById(ObjectId id) {
        return Optional.empty();
    }

    @Override
    public int deleteById(ObjectId id) {
        return 0;
    }

    @Override
    public Optional<Subject> updateById(ObjectId id, Subject subject) {
        return Optional.empty();
    }

    @Override
    public List<Subject> findByNameRegex(String name, int page, int limit) {
        return null;
    }

    @Override
    public List<Subject> findByCodeRegex(String code, int page, int limit) {
        return null;
    }
}
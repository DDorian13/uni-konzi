package bme.aut.unikonzi.dao;

import bme.aut.unikonzi.model.Subject;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Optional;

public interface SubjectDao extends CommonDao<Subject> {

    Optional<Subject> updateById(ObjectId id, Subject subject);

    List<Subject> findByNameRegex(String name, int page, int limit);

    List<Subject> findByCodeRegex(String code, int page, int limit);
}

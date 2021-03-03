package bme.aut.unikonzi.dao;

import bme.aut.unikonzi.model.University;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Optional;

public interface UniversityDao extends CommonDao<University> {

    Optional<University> findByName(String name);

    Optional<University> updateById(ObjectId id, University university);

    List<University> findByNameRegex(String name, int page, int limit);
}

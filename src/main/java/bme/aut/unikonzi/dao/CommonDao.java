package bme.aut.unikonzi.dao;

import org.bson.types.ObjectId;

import java.util.List;
import java.util.Optional;

public interface CommonDao<T> {

    T insert(T template);

    List<T> findAll(int page, int limit);

    Optional<T> findById(ObjectId id);

    int deleteById(ObjectId id);
}

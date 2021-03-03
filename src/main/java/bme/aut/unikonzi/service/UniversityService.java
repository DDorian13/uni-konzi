package bme.aut.unikonzi.service;

import bme.aut.unikonzi.dao.UniversityDao;
import bme.aut.unikonzi.model.University;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UniversityService {

    private final UniversityDao universityRepository;

    @Autowired
    public UniversityService(UniversityDao universityRepository) {
        this.universityRepository = universityRepository;
    }

    public Optional<University> addUniversity(University university) {
        String name = university.getName();
        if (universityRepository.findByName(name).isPresent()) {
            return Optional.empty();
        }
        return Optional.of(universityRepository.insert(university));
    }

    public List<University> getAllUniversities(int page, int limit) {
        return universityRepository.findAll(page, limit);
    }

    public Optional<University> getUniversityById(ObjectId id) {
        return universityRepository.findById(id);
    }

    public int deleteUniversity(ObjectId id) {
        return universityRepository.deleteById(id);
    }

    public Optional<University> getUniversityByName(String name) {
        return universityRepository.findByName(name);
    }

    public Optional<University> updateUniversityById(ObjectId id, University university) {
        return universityRepository.updateById(id, university);
    }

    public List<University> getUniversitiesByNameRegex(String name, int page, int limit) {
        return universityRepository.findByNameRegex(name, page, limit);
    }
}

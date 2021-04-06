package bme.aut.unikonzi.service;

import bme.aut.unikonzi.dao.UniversityDao;
import bme.aut.unikonzi.model.Subject;
import bme.aut.unikonzi.model.University;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
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

    public Optional<University> getUniversityById(ObjectId id, int page, int limit) {
        Optional<University> uniMaybe = universityRepository.findById(id);
        if (uniMaybe.isEmpty()) {
            return uniMaybe;
        }
        return Optional.of(subjectsToPageable(uniMaybe.get(), page, limit));
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

    public int removeSubjectFromUniversity(ObjectId universityId, Subject subject) {
        Optional<University> uniMaybe = universityRepository.findById(universityId);
        if (uniMaybe.isEmpty()) {
            return 0;
        }

        University university = uniMaybe.get();
        university.removeSubject(subject);
        return 1;
    }

    public List<University> getUniversitiesByNameRegex(String name, int page, int limit) {
        return universityRepository.findByNameRegex(name, page, limit);
    }

    private University subjectsToPageable(University original, int page, int limit) {
        int fromIndex = (page - 1) * limit;
        int toIndex = fromIndex + limit;
        List<Subject> subjects = original.getSubjects();
        if (fromIndex >= subjects.size() || fromIndex < 0) {
            subjects = Collections.emptyList();
        } else if (toIndex > subjects.size()) {
            toIndex = subjects.size();
        }
        if (!subjects.isEmpty()) {
            subjects = subjects.subList(fromIndex, toIndex);
        }
        return new University(
                new ObjectId(original.getId()),
                original.getName(),
                original.getCountry(),
                original.getCity(),
                subjects
        );
    }
}

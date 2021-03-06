package bme.aut.unikonzi.service;

import bme.aut.unikonzi.dao.UniversityDao;
import bme.aut.unikonzi.model.Comment;
import bme.aut.unikonzi.model.Subject;
import bme.aut.unikonzi.model.University;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubjectService {

    private final UniversityDao universityRepository;

    @Autowired
    public SubjectService(UniversityDao universityRepository) {
        this.universityRepository = universityRepository;
    }

    public Optional<Subject> getSubjectByCode(ObjectId universityId, String code) {
        Optional<University> university = universityRepository.findById(universityId);
        if (university.isEmpty()) {
            return Optional.empty();
        }
        List<Subject> subjects = university.get().getSubjects();
        return subjects.stream().filter(subject -> subject.getCode().equalsIgnoreCase(code)).findFirst();
    }

    public Optional<Subject> addSubject(ObjectId universityId, Subject subject) {
        Optional<University> universityMaybe = universityRepository.findById(universityId);
        Optional<Subject> subjectMaybe = getSubjectByCode(universityId, subject.getCode());
        if (universityMaybe.isEmpty()) {
            return null;
        } else if (subjectMaybe.isPresent()) {
            return Optional.empty();
        }
        University university = universityMaybe.get();
        university.addSubject(subject);
        universityRepository.updateById(universityId, university);
        return Optional.of(subject);
    }

    public Optional<Subject> getSubjectById(ObjectId universityId, ObjectId subjectId) {
        Optional<University> university = universityRepository.findById(universityId);
        if (university.isEmpty()) {
            return Optional.empty();
        }
        return university.get().getSubjects()
                .stream().filter(subject -> subject.getId().equals(subjectId.toString()))
                .findFirst();
    }

    public Optional<Subject> updateOrRemoveSubjectById(ObjectId universityId,
                                                       ObjectId subjectId,
                                                       Subject subject) {
        Optional<Subject> subjectMaybe = getSubjectById(universityId, subjectId);
        if (subjectMaybe.isEmpty()) {
            return Optional.empty();
        }
        University university = universityRepository.findById(universityId).get();
        int i = 0;
        List<Subject> subjects = university.getSubjects();
        for (i = 0; i < subjects.size(); ++i) {
            if (subjects.get(i).getId().equals(subjectId.toString())) {
                break;
            }
        }

        Subject newSubject;
        if (subject != null) {
            newSubject = new Subject(subjectId, subject.getCode(), subject.getName(), subject.getComments());
            subjects.set(i, newSubject);
        } else {
            newSubject = subjects.get(i);
            subjects.remove(i);
        }

        universityRepository.updateById(universityId,
                new University(universityId,
                        university.getName(),
                        university.getCountry(),
                        university.getCity(),
                        subjects));
        return Optional.of(newSubject);
    }

    public Optional<Comment> addCommentToSubject(ObjectId universityId,
                                                 ObjectId subjectId,
                                                 Comment comment) {
        Optional<Subject> subjectMaybe = getSubjectById(universityId, subjectId);
        if (subjectMaybe.isEmpty()) {
            return Optional.empty();
        }
        Subject subject = subjectMaybe.get();
        subject.addComment(comment);
        updateOrRemoveSubjectById(universityId, subjectId, subject);
        return Optional.of(comment);
    }
}

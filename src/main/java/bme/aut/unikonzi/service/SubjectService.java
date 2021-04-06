package bme.aut.unikonzi.service;

import bme.aut.unikonzi.dao.SubjectDao;
import bme.aut.unikonzi.dao.UniversityDao;
import bme.aut.unikonzi.model.Comment;
import bme.aut.unikonzi.model.Subject;
import bme.aut.unikonzi.model.University;
import bme.aut.unikonzi.model.User;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubjectService {

    private final SubjectDao subjectRepository;

    @Autowired
    public SubjectService(SubjectDao subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    public Optional<Subject> addSubject(Subject subject) {
        return Optional.of(subjectRepository.insert(subject));
    }

    public Optional<Subject> getSubjectById(ObjectId subjectId) {
        return subjectRepository.findById(subjectId);
    }

    public int deleteSubjectById(ObjectId subjectId) {
        return subjectRepository.deleteById(subjectId);
    }

    public Optional<Subject> updateSubjectById(ObjectId id, Subject subject) {
        return subjectRepository.updateById(id, subject);
    }

    public Optional<Comment> addCommentToSubject(ObjectId subjectId,
                                                 Comment comment) {
        Optional<Subject> subjectMaybe = getSubjectById(subjectId);
        if (subjectMaybe.isEmpty()) {
            return Optional.empty();
        }
        Subject subject = subjectMaybe.get();
        subject.addComment(comment);
        subjectRepository.updateById(subjectId, subject);
        return Optional.of(comment);
    }

    public boolean addTutorOrPupilToSubject(ObjectId subjectId,
                                            User user,
                                            String property) {
        Optional<Subject> subjectMaybe = getSubjectById(subjectId);
        if (subjectMaybe.isEmpty()) {
            return false;
        }
        Subject subject = subjectMaybe.get();
        if (property.equals("tutor") && !subject.getTutors().contains(user)) {
            subject.addTutor(user);
        } else if (property.equals("pupil") && !subject.getPupils().contains(user)) {
            subject.addPupil(user);
        }

        subjectRepository.updateById(subjectId, subject);
        return true;
    }

    public List<Subject> tutorOrPupilOf(String property, User user, int page, int limit) {
        return subjectRepository.containsTutorOrPupil(property, user, page, limit);
    }

    public List<Subject> getSubjectsByName(String name, int page, int limit) {
        return subjectRepository.findByNameRegex(name, page, limit);
    }

    public boolean removeUserFromSubjectPupils(ObjectId subjectId, User user) {
        Optional<Subject> subjectMaybe = getSubjectById(subjectId);
        if (subjectMaybe.isEmpty()) {
            return false;
        }

        Subject subject = subjectMaybe.get();
        boolean success = subject.removePupil(user);
        if (!success) {
            return false;
        }

        updateSubjectById(subjectId, subject);
        return true;
    }
}

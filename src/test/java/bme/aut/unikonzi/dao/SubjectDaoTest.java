package bme.aut.unikonzi.dao;

import bme.aut.unikonzi.dao.impl.MongoSubjectDao;
import bme.aut.unikonzi.dao.impl.MongoUserDao;
import bme.aut.unikonzi.model.Subject;
import bme.aut.unikonzi.model.User;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class SubjectDaoTest {

    @Autowired
    private MongoSubjectDao subjectDao;

    @Autowired
    private MongoUserDao userDao;

    @BeforeEach
    private void init() {
        subjectDao.findAll(1, 10).forEach(
                subject -> subjectDao.deleteById( new ObjectId(subject.getId()) )
        );
    }

    @DisplayName("Update by id")
    @Test
    public void updateTest() {
        ObjectId id = new ObjectId();
        Subject subject = new Subject(id, "code", "name", Collections.emptyList());
        subjectDao.insert(subject);
        Subject dbSubject = subjectDao.findById(id).get();
        assertThat(dbSubject.getId()).isEqualTo(subject.getId());
        assertThat(dbSubject.getCode()).isEqualTo(subject.getCode());
        assertThat(dbSubject.getName()).isEqualTo(subject.getName());
        assertThat(dbSubject.getComments()).isEqualTo(subject.getComments());
        assertThat(dbSubject.getTutors()).isEqualTo(subject.getTutors());
        assertThat(dbSubject.getPupils()).isEqualTo(subject.getPupils());

        subject = new Subject(null, "new code", "new name", Collections.emptyList());
        subjectDao.updateById(id, subject);
        dbSubject = subjectDao.findById(id).get();
        assertThat(dbSubject.getCode()).isEqualTo("new code");
        assertThat(dbSubject.getName()).isEqualTo("new name");

        assertThat(subjectDao.updateById(new ObjectId(), subject)).isEqualTo(Optional.empty());
    }

    @DisplayName("Find by name")
    @Test
    public void findByNameTest() {
        Subject subject = new Subject(null, "code", "name", null);
        Subject subject_not = new Subject(null, "code", "nothing", null);
        Subject subject2 = new Subject(null, "code", "name 2", null);
        subject = subjectDao.insert(subject);
        subjectDao.insert(subject_not);
        subject2 = subjectDao.insert(subject2);
        List<Subject> subjects = subjectDao.findByNameRegex("name", 1, 10);
        assertThat(subjects.size()).isEqualTo(2);
        assertThat(subjects.get(0).getId()).isEqualTo(subject.getId());
        assertThat(subjects.get(1).getId()).isEqualTo(subject2.getId());

        subjects = subjectDao.findByNameRegex("name", 2, 1);
        assertThat(subjects.size()).isEqualTo(1);
        assertThat(subjects.get(0).getId()).isEqualTo(subject2.getId());
    }

    @DisplayName("Find by code")
    @Test
    public void findByCodeTest() {
        Subject subject = new Subject(null, "code", "name", null);
        Subject subject_not = new Subject(null, "vaffsafsafaf", "name", null);
        Subject subject2 = new Subject(null, "code2", "name", null);
        subject = subjectDao.insert(subject);
        subjectDao.insert(subject_not);
        subject2 = subjectDao.insert(subject2);
        List<Subject> subjects = subjectDao.findByCodeRegex("code", 1, 10);
        assertThat(subjects.size()).isEqualTo(2);
        assertThat(subjects.get(0).getId()).isEqualTo(subject.getId());
        assertThat(subjects.get(1).getId()).isEqualTo(subject2.getId());

        subjects = subjectDao.findByCodeRegex("code", 2, 1);
        assertThat(subjects.size()).isEqualTo(1);
        assertThat(subjects.get(0).getId()).isEqualTo(subject2.getId());
    }

    @DisplayName("Contains tutor and pupil")
    @Test
    public void tutorOrPupilOfTest() {
        User user = new User(new ObjectId(), "My Name", "a@b.c", "password", Set.of(User.Role.ROLE_USER));
        user = userDao.insert(user);
        Subject subject = new Subject(null, "code", "name", null);
        Subject subject_not = new Subject(null, "vaffsafsafaf", "name", null);
        Subject subject2 = new Subject(null, "code2", "name", null);
        subject = subjectDao.insert(subject);
        subjectDao.insert(subject_not);
        subject2 = subjectDao.insert(subject2);

        subject.addTutor(user);
        subject2.addTutor(user);
        subjectDao.updateById(new ObjectId(subject.getId()), subject);
        subjectDao.updateById(new ObjectId(subject2.getId()), subject2);

        List<Subject> subjects = subjectDao.containsTutorOrPupil("tutors", user, 1, 10);
        assertThat(subjects.size()).isEqualTo(2);
        assertThat(subjects.get(0).getId()).isEqualTo(subject.getId());
        assertThat(subjects.get(1).getId()).isEqualTo(subject2.getId());

        subject_not.addPupil(user);
        subjectDao.updateById(new ObjectId(subject_not.getId()), subject_not);
        subjects = subjectDao.containsTutorOrPupil("pupils", user, 1, 10);
        assertThat(subjects.size()).isEqualTo(1);
        assertThat(subjects.get(0).getId()).isEqualTo(subject_not.getId());

        assertThat(subjectDao.containsTutorOrPupil("wrongPropertyName", user, 1, 10)).isEqualTo(Collections.emptyList());
    }
}

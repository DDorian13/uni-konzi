package bme.aut.unikonzi.service;

import bme.aut.unikonzi.dao.SubjectDao;
import bme.aut.unikonzi.model.Comment;
import bme.aut.unikonzi.model.Subject;
import bme.aut.unikonzi.model.User;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class SubjectServiceTest {

    @Autowired
    private SubjectService service;

    @MockBean
    private SubjectDao repository;

    private ObjectId id = new ObjectId();

    @Test
    @DisplayName("Insert")
    public void insertTest() {
        Subject subject = new Subject(null, "code", "name", null);
        Mockito.when(repository.insert(subject))
                .thenReturn(new Subject(id, "code", "name", null));


        Optional<Subject> newOptSubject = service.addSubject(subject);
        Subject newSubject = newOptSubject.get();
        assertThat(newOptSubject.isPresent()).isEqualTo(true);
        assertThat(newSubject.getId()).isEqualTo(id.toString());
        assertThat(newSubject.getCode()).isEqualTo("code");
        assertThat(newSubject.getName()).isEqualTo("name");
        assertThat(newSubject.getComments().size()).isEqualTo(0);
        assertThat(newSubject.getTutors().size()).isEqualTo(0);
        assertThat(newSubject.getPupils().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("Get subject by id")
    public void getSubjectByIdTest() {
        Subject subject = new Subject(id, "code", "name", null);
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(subject));

        Optional<Subject> subjectMaybe = service.getSubjectById(id);
        assertThat(subjectMaybe.isPresent()).isEqualTo(true);
        Subject subjectGet = subjectMaybe.get();

        assertThat(subjectGet.getId()).isEqualTo(id.toString());
        assertThat(subjectGet.getCode()).isEqualTo("code");
        assertThat(subjectGet.getName()).isEqualTo("name");
        assertThat(subjectGet.getComments().size()).isEqualTo(0);
        assertThat(subjectGet.getTutors().size()).isEqualTo(0);
        assertThat(subjectGet.getPupils().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("Delete by id successful")
    public void deleteSubjectByIdSuccessfulTest() {
        Mockito.when(repository.deleteById(id)).thenReturn(1);

        assertThat(service.deleteSubjectById(id)).isEqualTo(1);
    }

    @Test
    @DisplayName("Delete by id, not exists")
    public void deleteSubjectByIdNotExistsTest() {
        Mockito.when(repository.deleteById(id)).thenReturn(0);

        assertThat(service.deleteSubjectById(id)).isEqualTo(0);
    }

    @Test
    @DisplayName("Update by id, successfull")
    public void updateByIdSuccessfulTest() {
        Subject subject = new Subject(null, "new code", "new name", null);
        Mockito.when(repository.updateById(id, subject)).thenReturn(Optional.of(
                new Subject(id, subject.getCode(), subject.getName(), subject.getComments())
        ));

        Optional<Subject> subjectMaybe = service.updateSubjectById(id, subject);
        assertThat(subjectMaybe.isPresent()).isEqualTo(true);
        assertThat(subjectMaybe.get().getId()).isEqualTo(id.toString());
        assertThat(subjectMaybe.get().getCode()).isEqualTo("new code");
        assertThat(subjectMaybe.get().getName()).isEqualTo("new name");
        assertThat(subjectMaybe.get().getComments().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("Update by id, not exists")
    public void updateByIdNotExistsTest() {
        Subject subject = new Subject(null, "new code", "new name", null);
        Mockito.when(repository.updateById(id, subject)).thenReturn(Optional.empty());

        assertThat(service.updateSubjectById(id, subject).isEmpty()).isEqualTo(true);
    }

    @Test
    @DisplayName("Add comment, successful")
    public void addCommentToSubjectSuccessfulTest() {
        Subject subject = new Subject(id, "code", "name", null);
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(subject));

        ObjectId commentId = new ObjectId();
        Comment comment = new Comment(commentId, null, "This is the comment text");
        Optional<Comment> commentMaybe = service.addCommentToSubject(id, comment);

        assertThat(commentMaybe.isPresent()).isEqualTo(true);
        Comment newComment = commentMaybe.get();
        assertThat(newComment.getId()).isEqualTo(commentId.toString());
        assertThat(newComment.getUser()).isEqualTo(null);
        assertThat(newComment.getText()).isEqualTo("This is the comment text");
    }

    @Test
    @DisplayName("Add comment, not exists")
    public void addCommentToSubjectNotExistsTest() {
        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        ObjectId commentId = new ObjectId();
        Comment comment = new Comment(commentId, null, "This is the comment text");

        assertThat(service.addCommentToSubject(id, comment)).isEqualTo(Optional.empty());
    }

    @Test
    @DisplayName("Add tutor or pupil, subject not exists")
    public void addTutorOrPupilNotExistsTest() {
        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        assertThat(service.addTutorOrPupilToSubject(id, newUser(), "tutor")).isEqualTo(false);
    }

    @Test
    @DisplayName("Add tutor or pupil, wrong property name")
    public void addTutorOrPupilWrongNameTest() {
        Subject subject = new Subject(id, "code", "name", null);
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(subject));

        assertThat(service.addTutorOrPupilToSubject(id, newUser(), "thisIsWrongPropertyName")).isEqualTo(false);
    }

    @Test
    @DisplayName("Add tutor, successful")
    public void addTutorSuccessfulTest() {
        Subject subject = new Subject(id, "code", "name", null);
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(subject));

        assertThat(service.addTutorOrPupilToSubject(id, newUser(), "tutor")).isEqualTo(true);
    }

    @Test
    @DisplayName("Add pupil, successful")
    public void addPupilSuccessfulTest() {
        Subject subject = new Subject(id, "code", "name", null);
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(subject));

        assertThat(service.addTutorOrPupilToSubject(id, newUser(), "pupil")).isEqualTo(true);
    }

    @Test
    @DisplayName("Tutors of subjects")
    public void tutorOfTest() {
        tutorOrPupilOf_Helper("tutors");
    }

    @Test
    @DisplayName("Pupils of subjects")
    public void pupilOfTest() {
        tutorOrPupilOf_Helper("pupils");
    }

    @Test
    @DisplayName("Get subjects by name regex")
    public void getSubjectsByNameTest() {
        ObjectId id2 = new ObjectId();
        ObjectId id3 = new ObjectId();
        Subject subject = new Subject(id, "code", "name", null);
        Subject subject2 = new Subject(id2, "code2", "name2", null);
        Subject subject3 = new Subject(id3, "code3", "name3", null);
        Mockito.when(repository.findByNameRegex("name", 1, 10)).thenReturn(List.of(subject, subject2, subject3));

        List<Subject> subjects = service.getSubjectsByName("name", 1, 10);
        assertThat(subjects.size()).isEqualTo(3);
        assertThat(subjects.get(0).getName()).isEqualTo("name");
        assertThat(subjects.get(1).getName()).isEqualTo("name2");
        assertThat(subjects.get(2).getName()).isEqualTo("name3");
    }

    @Test
    @DisplayName("Remove user from pupils, successful")
    public void removeUserFromPupilsSuccessfulTest() {
        User user = newUser();
        Subject subject = new Subject(id, "code", "name", null);
        subject.addPupil(user);
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(subject));

        assertThat(service.removeUserFromSubjectPupils(id, user)).isEqualTo(true);
    }

    @Test
    @DisplayName("Remove user from pupils, not contains")
    public void removeUserFromPupilsNotContainsTest() {
        User user = newUser();
        Subject subject = new Subject(id, "code", "name", null);
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(subject));

        assertThat(service.removeUserFromSubjectPupils(id, user)).isEqualTo(false);
    }

    @Test
    @DisplayName("Remove user from pupils, subject not exists")
    public void removeUserFromPupilsNotExistsTest() {
        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        assertThat(service.removeUserFromSubjectPupils(id, newUser())).isEqualTo(false);
    }

    public void tutorOrPupilOf_Helper(String property) {
        ObjectId id2 = new ObjectId();
        ObjectId id3 = new ObjectId();
        Subject subject = new Subject(id, "code", "name", null);
        Subject subject2 = new Subject(id2, "code2", "name2", null);
        Subject subject3 = new Subject(id3, "code3", "name3", null);
        User user = newUser();
        subject.addTutor(user);
        subject2.addTutor(user);
        subject3.addTutor(user);
        Mockito.when(repository.containsTutorOrPupil(property, user, 1, 10)).thenReturn(List.of(subject, subject2, subject3));

        List<Subject> subjects = service.tutorOrPupilOf(property, user, 1, 10);
        assertThat(subjects.size()).isEqualTo(3);
        assertThat(subjects.get(0).getCode()).isEqualTo("code");
        assertThat(subjects.get(1).getCode()).isEqualTo("code2");
        assertThat(subjects.get(2).getCode()).isEqualTo("code3");
    }

    private static User newUser() {
        return new User(new ObjectId(), "Username", "email", "password", Set.of(User.Role.ROLE_USER));
    }
}

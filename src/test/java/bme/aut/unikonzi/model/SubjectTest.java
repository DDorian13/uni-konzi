package bme.aut.unikonzi.model;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class SubjectTest {
    private Subject subject;

    @BeforeEach
    public void init() {
        subject = new Subject(new ObjectId("606440c76413383605eb3dd0"), "code", "name", new ArrayList<>());
    }

    @DisplayName("Constructor")
    @Test
    public void constructorTest() {
        assertThat(subject.getId()).isEqualTo("606440c76413383605eb3dd0");
        assertThat(subject.getCode()).isEqualTo("code");
        assertThat(subject.getName()).isEqualTo("name");
        assertThat(subject.getComments()).isEqualTo(Collections.emptyList());
        assertThat(subject.getPupils()).isEqualTo(Collections.emptySet());
        assertThat(subject.getTutors()).isEqualTo(Collections.emptySet());
    }

    @DisplayName("Add comment")
    @Test
    public void addCommentTest() {
        Comment comment = new Comment(new ObjectId("606440c76413383605eb3dd0"), null, "text");
        subject.addComment(comment);
        assertThat(subject.getComments()).isEqualTo(List.of(comment));
    }

    @DisplayName("Add tutor")
    @Test
    public void addTutorTest() {
        User user = new User(null, "Name", "email", "password", null);
        subject.addTutor(user);
        assertThat(subject.getTutors()).isEqualTo(Set.of(user));
    }

    @DisplayName("Add pupil")
    @Test
    public void addPupilTest() {
        User user = new User(null, "Name", "email", "password", null);
        subject.addPupil(user);
        assertThat(subject.getPupils()).isEqualTo(Set.of(user));
    }

    @DisplayName("Remove pupil")
    @Test
    public void removePupilTest() {
        User user = new User(null, "Name", "email", "password", null);
        subject.addPupil(user);
        assertThat(subject.getPupils()).isEqualTo(Set.of(user));
        subject.removePupil(user);
        assertThat(subject.getPupils()).isEqualTo(Collections.emptySet());
    }


}

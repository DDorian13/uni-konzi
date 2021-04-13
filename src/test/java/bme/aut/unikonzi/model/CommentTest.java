package bme.aut.unikonzi.model;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class CommentTest {

    @DisplayName("Construct test")
    @Test
    public void Test() {
        User user = new User(new ObjectId("606440c76413383605eb3dd0"), "name", "email", "password", Collections.emptySet());
        Comment comment = new Comment(new ObjectId("406440c76413383705eb3dd0"), user, "This is the comment");
        assertThat(comment.getId()).isEqualTo("406440c76413383705eb3dd0");
        assertThat(comment.getUser()).isEqualTo(user);
        assertThat(comment.getText()).isEqualTo("This is the comment");
    }
}

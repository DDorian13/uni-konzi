package bme.aut.unikonzi.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@JsonFilter("filterByName")
@Document("subjects")
public class Subject {

    @Id
    private ObjectId id;

    @Field("code")
    @NotBlank
    private final String code;

    @Field("name")
    @NotBlank
    private final String name;

    @Field("comments")
    private List<Comment> comments;

    @Field("tutors")
    @DBRef
    private Set<User> tutors = new HashSet<>();

    @Field("pupils")
    @DBRef
    private Set<User> pupils = new HashSet<>();

    public Subject(@JsonProperty("id") ObjectId id,
                   @JsonProperty("code") String code,
                   @JsonProperty("name") String name,
                   @JsonProperty("comments") List<Comment> comments) {
        if (id == null) {
            this.id = ObjectId.get();
        } else {
            this.id = id;
        }
        this.code = code;
        this.name = name;
        if (comments == null) {
            this.comments = new ArrayList<>();
        } else {
            this.comments = comments;
        }
    }

    public String getId() {
        return id.toString();
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void addComment(Comment newComment) {
        comments.add(newComment);
    }

    public Set<User> getTutors() {
        return tutors;
    }

    public Set<User> getPupils() {
        return pupils;
    }

    public void addTutor(User user) {
        tutors.add(user);
    }

    public void addPupil(User user) {
        pupils.add(user);
    }

    public boolean removePupil(User user) {
        return pupils.remove(user);
    }
}

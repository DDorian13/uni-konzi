package bme.aut.unikonzi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Document("subjects")
public class Subject {

    @Id
    private final ObjectId id;

    @Field("code")
    @NotBlank
    private final String code;

    @Field("name")
    @NotBlank
    private final String name;

    @Field("comments")
    private List<Comment> comments;

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

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public List<Comment> getComments() {
        return comments;
    }
}

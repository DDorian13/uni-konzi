package bme.aut.unikonzi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotBlank;

public class Comment {

    @Id
    private final ObjectId id;

    @Field("user")
    @DBRef
    private final User user;

    @Field("text")
    @NotBlank
    private final String text;

    public Comment(@JsonProperty("id") ObjectId id,
                   @JsonProperty("user") User user,
                   @JsonProperty("text") String text) {
        if (id != null) {
            this.id = id;
        } else {
            this.id = ObjectId.get();
        }
        this.user = user;
        this.text = text;
    }

    public String getId() {
        return id.toString();
    }

    public User getUser() {
        return user;
    }

    public String getText() {
        return text;
    }
}
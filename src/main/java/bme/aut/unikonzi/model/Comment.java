package bme.aut.unikonzi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class Comment {

    @Id
    private final ObjectId id;

    private final User user;

    private final String text;

    public Comment(@JsonProperty("id") ObjectId id,
                   @JsonProperty("user") User user,
                   @JsonProperty("text") String text) {
        this.id = id;
        this.user = user;
        this.text = text;
    }

    public ObjectId getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getText() {
        return text;
    }
}
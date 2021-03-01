package bme.aut.unikonzi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

public class Subject {

    @Id
    private final ObjectId id;

    private final String code;

    private final String name;

    private List<Comment> comments;

    public Subject(@JsonProperty("id") ObjectId id,
                   @JsonProperty("code") String code,
                   @JsonProperty("name") String name) {
        this.id = id;
        this.code = code;
        this.name = name;
        comments = new ArrayList<>();
    }

    public ObjectId getId() {
        return id;
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

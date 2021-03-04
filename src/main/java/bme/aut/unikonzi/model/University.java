package bme.aut.unikonzi.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@JsonFilter("filterByName")
@Document(collection = "universities")
public class University {

    @Id
    private ObjectId id;

    @Field("name")
    @NotBlank
    private final String name;

    @Field("country")
    @NotBlank
    private final String country;

    @Field("city")
    @NotBlank
    private final String city;

    @Field("subjects")
    private List<Subject> subjects;

    public University(@JsonProperty("id") ObjectId id,
                      @JsonProperty("name") String name,
                      @JsonProperty("country") String country,
                      @JsonProperty("city") String city,
                      @JsonProperty("subjects") List<Subject> subjects) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.city = city;
        if (subjects != null) {
            this.subjects = subjects;
        } else {
            this.subjects = new ArrayList<>();
        }
    }

    public String getId() {
        return id.toString();
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public void addSubject(Subject newSubject) {
        subjects.add(newSubject);
    }
}

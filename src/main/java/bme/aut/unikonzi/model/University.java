package bme.aut.unikonzi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "universities")
public class University {

    @Id
    private final ObjectId id;

    private final String name;

    private final String country;

    private final String city;

    public University(@JsonProperty("id") ObjectId id,
                      @JsonProperty("name") String name,
                      @JsonProperty("country") String country,
                      @JsonProperty("city") String city) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.city = city;
    }
}

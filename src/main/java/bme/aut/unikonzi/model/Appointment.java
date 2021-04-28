package bme.aut.unikonzi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Objects;

@Document("appointments")
public class Appointment {

    @Id
    private ObjectId id;
    private ObjectId creatorId;
    private ObjectId participantId;
    private Date date;
    @NotNull
    private int length;
    private String description;
    private String location;

    public Appointment(@JsonProperty("id") ObjectId id,
                       @JsonProperty("creatorId") ObjectId creatorId,
                       @JsonProperty("participantId") ObjectId participantId,
                       @JsonProperty("date") Date date,
                       @JsonProperty("length") int length,
                       @JsonProperty("description") String description,
                       @JsonProperty("location") String location) {
        this.id = id;
        this.creatorId = creatorId;
        this.participantId = participantId;
        this.date = date;
        this.length = length;
        this.description = description;
        this.location = location;
    }

    public String getId() {
        return this.id.toString();
    }

    public void setCreatorId(ObjectId creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorId() {
        return this.creatorId.toString();
    }

    public void setParticipantId(ObjectId participantId) {
        this.participantId = participantId;
    }

    public String getParticipantId() {
        return this.participantId.toString();
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return this.date;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getLength() {
        return this.length;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Appointment apt = (Appointment) o;
        if (!apt.id.equals(this.id) || !apt.creatorId.equals((this.creatorId))
                || !apt.participantId.equals(this.participantId) || !apt.date.equals(this.date)
                || apt.length != this.length || !apt.description.equals(this.description)
                || !apt.location.equals(this.location)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

package bme.aut.unikonzi.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

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

    public Appointment(ObjectId id,
                       ObjectId creatorId,
                       ObjectId participantId,
                       Date date,
                       int length,
                       String description,
                       String location) {
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
}

package bme.aut.unikonzi.model.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class NewContact {

    @NotNull
    private final String senderId;

    @NotNull
    private final String recipientId;

    public NewContact(@JsonProperty("senderId") String senderId,
                      @JsonProperty("recipientId") String recipientId) {
        this.senderId = senderId;
        this.recipientId = recipientId;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getRecipientId() {
        return recipientId;
    }
}

package bme.aut.unikonzi.model.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CommentBody {

    private final String text;

    public CommentBody(@JsonProperty("text") String text) {
            this.text = text;
        }

    public String getText() {
            return text;
        }
}
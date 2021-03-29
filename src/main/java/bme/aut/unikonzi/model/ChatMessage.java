package bme.aut.unikonzi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "messages")
public class ChatMessage {

    @Id
    private ObjectId id;

    private String chatId;
    private ObjectId senderId;
    private ObjectId recipientId;
    private String senderName;
    private String recipientName;
    private String message;
    private Date timestamp;
    private MessageStatus messageStatus;
}

package bme.aut.unikonzi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "chatRooms")
public class ChatRoom {

    @Id
    private ObjectId id;

    private String chatId;
    private ObjectId senderId;
    private ObjectId recipientId;
}

package bme.aut.unikonzi.dao;

import bme.aut.unikonzi.model.ChatRoom;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
    Optional<ChatRoom> findBySenderIdAndRecipientId(String senderId, String recipientId);
    List<ChatRoom> findAllBySenderId(String senderId);
}

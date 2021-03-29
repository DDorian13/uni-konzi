package bme.aut.unikonzi.dao;

import bme.aut.unikonzi.model.ChatRoom;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, ObjectId> {
    Optional<ChatRoom> findBySenderIdAndRecipientId(ObjectId senderId, ObjectId recipientId);
}

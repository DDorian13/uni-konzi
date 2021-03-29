package bme.aut.unikonzi.dao;

import bme.aut.unikonzi.model.ChatMessage;
import bme.aut.unikonzi.model.MessageStatus;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, ObjectId> {

    long countBySenderIdAndRecipientIdAndMessageStatus(ObjectId senderId, ObjectId recipientId, MessageStatus status);
    List<ChatMessage> findByChatId(String chatId);
}

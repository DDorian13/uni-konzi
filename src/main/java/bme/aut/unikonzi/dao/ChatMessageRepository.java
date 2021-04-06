package bme.aut.unikonzi.dao;

import bme.aut.unikonzi.model.ChatMessage;
import bme.aut.unikonzi.model.MessageStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    long countBySenderIdAndRecipientIdAndMessageStatus(String senderId, String recipientId, MessageStatus status);
    List<ChatMessage> findByChatId(String chatId);
    Optional<ChatMessage> findFirstByRecipientIdAndMessageStatus(String recipientId, MessageStatus status);
}

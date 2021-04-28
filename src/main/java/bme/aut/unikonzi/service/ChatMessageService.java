package bme.aut.unikonzi.service;

import bme.aut.unikonzi.dao.ChatMessageRepository;
import bme.aut.unikonzi.exception.MessageNotFoundException;
import bme.aut.unikonzi.model.ChatMessage;
import bme.aut.unikonzi.model.MessageStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ChatMessageService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private MongoOperations mongoOperations;

    public ChatMessage save(ChatMessage chatMessage) {
        chatMessage.setMessageStatus(MessageStatus.RECEIVED);
        return chatMessageRepository.save(chatMessage);
    }

    public int hasNewMessageFromAnybody(String recipientId) {
        if (chatMessageRepository.findFirstByRecipientIdAndMessageStatus(recipientId, MessageStatus.RECEIVED).isPresent()) {
            return 1;
        } else {
            return 0;
        }
    }

    public long countNewMessages(String senderId, String recipientId) {
        return chatMessageRepository.countBySenderIdAndRecipientIdAndMessageStatus(senderId, recipientId, MessageStatus.RECEIVED);
    }

    public List<ChatMessage> findChatMessages(String senderId, String recipientId) {
        Optional<String> chatId = chatRoomService.getChatId(senderId, recipientId, false);
        List<ChatMessage> messages = chatId.map(_chatId -> chatMessageRepository.findByChatId(_chatId)).orElse(new ArrayList<>());

        if (messages.size() > 0) {
            updateStatuses(senderId, recipientId, MessageStatus.DELIVERED);
        }

        return messages;
    }

    public void updateStatuses(String senderId, String recipientId, MessageStatus status) {
        Query query = new Query(Criteria.where("senderId").is(senderId)
                                .and("recipientId").is(recipientId));
        Update update = Update.update("messageStatus", status);
        mongoOperations.updateMulti(query, update, ChatMessage.class);
    }

    public ChatMessage findById(String id) {
        return chatMessageRepository
                .findById(id)
                .map(chatMessage -> {
                    chatMessage.setMessageStatus(MessageStatus.DELIVERED);
                    return chatMessageRepository.save(chatMessage);
                })
                .orElseThrow(() -> new MessageNotFoundException("Can't find message"));
    }
}

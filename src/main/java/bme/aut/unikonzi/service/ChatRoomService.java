package bme.aut.unikonzi.service;

import bme.aut.unikonzi.dao.ChatRoomRepository;
import bme.aut.unikonzi.dao.impl.MongoUserDao;
import bme.aut.unikonzi.model.ChatRoom;
import bme.aut.unikonzi.model.User;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ChatRoomService {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private MongoUserDao userRepository;

    public Optional<String> getChatId(String senderId, String recipientId, boolean createIfNotExist) {
        return chatRoomRepository.findBySenderIdAndRecipientId(senderId, recipientId)
                .map(ChatRoom::getChatId)
                .or(() -> {
                    if (!createIfNotExist) {
                        return Optional.empty();
                    }
                    String chatId = String.format("%s_%s", senderId, recipientId);

                    ChatRoom senderToRecipient = ChatRoom.builder()
                            .chatId(chatId)
                            .senderId(senderId)
                            .recipientId(recipientId)
                            .build();

                    ChatRoom recipientToSender = ChatRoom.builder()
                            .chatId(chatId)
                            .senderId(recipientId)
                            .recipientId(senderId)
                            .build();

                    chatRoomRepository.save(senderToRecipient);
                    chatRoomRepository.save(recipientToSender);

                    return Optional.of(chatId);
                });
    }

    public List<User> getContacts(String senderId) {
        List<ChatRoom> chatRooms = chatRoomRepository.findAllBySenderId(senderId);
        List<User> users = new ArrayList<>();
        chatRooms.forEach(chatRoom -> {
            Optional<User> user = userRepository.findById(new ObjectId(chatRoom.getRecipientId()));
            if (user.isPresent()) {
                users.add(user.get());
            }
        });
        return users;
    }
}

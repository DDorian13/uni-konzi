package bme.aut.unikonzi.service;

import bme.aut.unikonzi.dao.ChatRoomRepository;
import bme.aut.unikonzi.dao.UserDao;
import bme.aut.unikonzi.model.ChatRoom;
import bme.aut.unikonzi.model.User;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class ChatRoomServiceTest {

    @Autowired
    private ChatRoomService service;

    @MockBean
    private ChatRoomRepository repository;

    @MockBean
    private UserDao userRepository;

    @Test
    @DisplayName("Get chat id")
    public void getChatIdExistsTest() {
        Mockito.when(repository.findBySenderIdAndRecipientId("senderId", "recipientId"))
                .thenReturn(Optional.of(ChatRoom.builder()
                        .chatId("chatId")
                        .build()));

        assertThat(service.getChatId("senderId", "recipientId", false)).isEqualTo(Optional.of("chatId"));
    }

    @Test
    @DisplayName("Get chat id, not exists")
    public void getChatIdNotExistsTest() {
        Mockito.when(repository.findBySenderIdAndRecipientId("senderId", "recipientId")).thenReturn(Optional.empty());

        assertThat(service.getChatId("senderId", "recipientId", false)).isEqualTo(Optional.empty());
    }

    @Test
    @DisplayName("Get chat id, create")
    public void getChatIdCreateTest() {
        Mockito.when(repository.findBySenderIdAndRecipientId("senderId", "recipientId")).thenReturn(Optional.empty());

        assertThat(service.getChatId("senderId", "recipientId", true))
                .isEqualTo(Optional.of("senderId_recipientId"));
    }

    @Test
    @DisplayName("Get contacts")
    public void getContactsTest() {
        ObjectId id1 = new ObjectId();
        ObjectId id2 = new ObjectId();
        ObjectId id3 = new ObjectId();
        Mockito.when(repository.findAllBySenderId("senderId"))
                .thenReturn(List.of(
                        ChatRoom.builder().recipientId(id1.toString()).build(),
                        ChatRoom.builder().recipientId(id2.toString()).build(),
                        ChatRoom.builder().recipientId(id3.toString()).build()
                ));
        User user1 = new User(id1, "User One", "user1@email.com", "pass", null);
        User user2 = new User(id2, "User Two", "user2@email.com", "pass", null);
        Mockito.when(userRepository.findById(id1)).thenReturn(Optional.of(user1));
        Mockito.when(userRepository.findById(id2)).thenReturn(Optional.of(user2));
        Mockito.when(userRepository.findById(id3)).thenReturn(Optional.empty());

        assertThat(service.getContacts("senderId")).isEqualTo(List.of(user1, user2));
    }
}

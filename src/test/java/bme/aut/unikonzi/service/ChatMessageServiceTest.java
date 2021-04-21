package bme.aut.unikonzi.service;

import bme.aut.unikonzi.dao.ChatMessageRepository;
import bme.aut.unikonzi.exception.MessageNotFoundException;
import bme.aut.unikonzi.model.ChatMessage;
import bme.aut.unikonzi.model.MessageStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
public class ChatMessageServiceTest {

    @Autowired
    private ChatMessageService service;

    @MockBean
    private ChatRoomService chatRoomService;

    @MockBean
    private ChatMessageRepository repository;

    @Test
    @DisplayName("Insert test")
    public void saveTest() {
        ChatMessage cmsg = ChatMessage.builder()
                .chatId("chatId")
                .senderId("senderId")
                .recipientId("recipientId")
                .senderName("Sender Name")
                .recipientName("Recipient Name")
                .message("message")
                .timestamp(new Date())
                .messageStatus(MessageStatus.DELIVERED).build();
        ChatMessage repository_cmsg = cmsg;
        repository_cmsg.setId("id");
        Mockito.when(repository.save(cmsg)).thenReturn(repository_cmsg);

        ChatMessage service_cmsg = repository_cmsg;
        service_cmsg.setMessageStatus(MessageStatus.RECEIVED);

        assertThat(service.save(cmsg)).isEqualTo(service_cmsg);
    }

    @Test
    @DisplayName("New message from anybody")
    public void newMessageTest() {
        Mockito.when(repository.findFirstByRecipientIdAndMessageStatus("recipientId", MessageStatus.RECEIVED)).thenReturn(
                Optional.of(new ChatMessage())
        );
        Mockito.when(repository.findFirstByRecipientIdAndMessageStatus("otherId", MessageStatus.RECEIVED)).thenReturn(Optional.empty());

        assertThat(service.hasNewMessageFromAnybody("recipientId")).isEqualTo(1);
        assertThat(service.hasNewMessageFromAnybody("otherId")).isEqualTo(0);
    }

    @Test
    @DisplayName("Count new messages")
    public void countNewMessagesTest() {
        Mockito.when(repository.countBySenderIdAndRecipientIdAndMessageStatus("senderId", "recipientId", MessageStatus.RECEIVED)).thenReturn(Long.parseLong("3"));

        assertThat(service.countNewMessages("senderId", "recipientId")).isEqualTo(3);
    }

    @Test
    @DisplayName("Find messages")
    public void findMessagesTest() {
        Mockito.when(chatRoomService.getChatId("senderId", "recipientId", false)).thenReturn(Optional.of("chatId"));
        Mockito.when(chatRoomService.getChatId("senderId", "otherId", false)).thenReturn(Optional.empty());

        ChatMessage cmsg = ChatMessage.builder()
                .message("message1")
                .build();
        ChatMessage cmsg2 = ChatMessage.builder()
                .message("message2")
                .build();

        Mockito.when(repository.findByChatId("chatId")).thenReturn(List.of(cmsg, cmsg2));

        assertThat(service.findChatMessages("senderId", "otherId")).isEqualTo(new ArrayList<ChatMessage>());

        cmsg.setMessageStatus(MessageStatus.DELIVERED);
        cmsg2.setMessageStatus(MessageStatus.DELIVERED);
        assertThat(service.findChatMessages("senderId", "recipientId")).isEqualTo(List.of(cmsg, cmsg2));
    }

    @Test
    @DisplayName("Find by id")
    public void findByIdTest() {
        ChatMessage cmsg = ChatMessage.builder()
                .id("id")
                .senderId("sId")
                .recipientId("rId")
                .message("message")
                .build();

        String id = "id";
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(cmsg));
        Mockito.when(repository.save(cmsg)).thenReturn(cmsg);
        Mockito.when(repository.findById("otherId")).thenReturn(Optional.empty());

        ChatMessage s_cmsg = service.findById(id);
        assertThat(s_cmsg.getSenderId()).isEqualTo("sId");
        assertThat(s_cmsg.getRecipientId()).isEqualTo("rId");
        assertThat(s_cmsg.getMessage()).isEqualTo("message");
        assertThat(s_cmsg.getMessageStatus()).isEqualTo(MessageStatus.DELIVERED);

        assertThrows(MessageNotFoundException.class, () -> service.findById("otherId"));
    }
}

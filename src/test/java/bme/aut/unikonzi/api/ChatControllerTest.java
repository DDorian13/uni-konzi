package bme.aut.unikonzi.api;

import bme.aut.unikonzi.dao.UserDao;
import bme.aut.unikonzi.helper.TokenMock;
import bme.aut.unikonzi.model.ChatMessage;
import bme.aut.unikonzi.model.MessageStatus;
import bme.aut.unikonzi.model.User;
import bme.aut.unikonzi.model.payload.request.NewContact;
import bme.aut.unikonzi.service.ChatMessageService;
import bme.aut.unikonzi.service.ChatRoomService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserDao userRepository;

    private ObjectMapper mapper = new ObjectMapper();

    @MockBean
    private ChatMessageService chatMessageService;

    private String token = TokenMock.getAdminToken("username");

    @MockBean
    private ChatRoomService chatRoomService;

    @BeforeEach
    public void init() {
        Mockito.when(userRepository.findByName(any(String.class)))
                .thenReturn(Optional.of(TokenMock.user));
    }

    @Test
    @DisplayName("Has new message")
    public void hasNewMessageTest() throws Exception {
        Mockito.when(chatMessageService.hasNewMessageFromAnybody(any(String.class)))
                .thenReturn(1);

        MvcResult result = mockMvc.perform(get("/api/messages/id1/has-new")
                .header("Authorization", TokenMock.getAdminToken("username")))
                .andExpect(status().isOk())
                .andReturn();
        int hasNewMessage = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<Integer>() {});

        assertThat(hasNewMessage).isEqualTo(1);
    }

    @Test
    @DisplayName("No new message")
    public void noNewMessageTest() throws Exception {
        Mockito.when(chatMessageService.hasNewMessageFromAnybody(any(String.class)))
                .thenReturn(0);

        MvcResult result = mockMvc.perform(get("/api/messages/id1/has-new")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn();
        int hasNewMessage = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<Integer>() {});

        assertThat(hasNewMessage).isEqualTo(0);
    }

    @Test
    @DisplayName("Count new messages")
    public void countNewMessagesTest() throws Exception {
        long expectedCount = 4L;
        Mockito.when(chatMessageService.countNewMessages(any(String.class), any(String.class))).thenReturn(expectedCount);

        MvcResult result = mockMvc.perform(get("/api/messages/id1/id2/count")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn();
        long newMessagesCount = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<Long>(){});

        assertThat(newMessagesCount).isEqualTo(expectedCount);
    }

    @Test
    @DisplayName("Find chat messages")
    public void findChatMessagesTest() throws Exception {
        ChatMessage cm1 = ChatMessage.builder()
                .id("id1")
                .chatId("chatId")
                .senderId("sId")
                .recipientId("rId")
                .build();

        ChatMessage cm2 = ChatMessage.builder()
                .id("id2")
                .chatId("chatId")
                .senderId("rId")
                .recipientId("sId")
                .build();
        Mockito.when(chatMessageService.findChatMessages(any(String.class), any(String.class)))
                .thenReturn(List.of(cm1, cm2));

        MvcResult result = mockMvc.perform(get("/api/messages/id1/id2")
                .header("Authorization", token))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<ChatMessage> chatMessages = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<ChatMessage>>() {});
        assertThat(chatMessages.size()).isEqualTo(2);
        ChatMessage newMessage1 = chatMessages.get(0);
        assertThat(newMessage1.getId()).isEqualTo("id1");
        assertThat(newMessage1.getChatId()).isEqualTo("chatId");
        assertThat(newMessage1.getSenderId()).isEqualTo("sId");
        assertThat(newMessage1.getRecipientId()).isEqualTo("rId");

        ChatMessage newMessage2 = chatMessages.get(1);
        assertThat(newMessage2.getId()).isEqualTo("id2");
        assertThat(newMessage2.getChatId()).isEqualTo("chatId");
        assertThat(newMessage2.getSenderId()).isEqualTo("rId");
        assertThat(newMessage2.getRecipientId()).isEqualTo("sId");
    }

    @Test
    @DisplayName("Find message by id")
    public void findMessageTest() throws Exception {
        Date date = new Date();
        ChatMessage cm1 = ChatMessage.builder()
                .id("id")
                .chatId("chatId")
                .senderId("sId")
                .recipientId("rId")
                .senderName("sName")
                .recipientName("rName")
                .timestamp(date)
                .message("message")
                .messageStatus(MessageStatus.RECEIVED)
                .build();
        Mockito.when(chatMessageService.findById(any(String.class))).thenReturn(cm1);

        MvcResult result = mockMvc.perform(get("/api/messages/id")
                .header("Authorization", token))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        ChatMessage foundMessage = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<ChatMessage>() {});

        assertThat(foundMessage.getId()).isEqualTo("id");
        assertThat(foundMessage.getChatId()).isEqualTo("chatId");
        assertThat(foundMessage.getSenderId()).isEqualTo("sId");
        assertThat(foundMessage.getRecipientId()).isEqualTo("rId");
        assertThat(foundMessage.getSenderName()).isEqualTo("sName");
        assertThat(foundMessage.getRecipientName()).isEqualTo("rName");
        assertThat(foundMessage.getTimestamp()).isEqualTo(date);
        assertThat(foundMessage.getMessage()).isEqualTo("message");
        assertThat(foundMessage.getMessageStatus()).isEqualTo(MessageStatus.RECEIVED);
    }

    @Test
    @DisplayName("Get contacts")
    public void getContactsTest() throws Exception {
        User user1 = TokenMock.admin;
        User user2 = new User(new ObjectId(), "name", "email", "pass", Set.of(User.Role.ROLE_USER));
        Mockito.when(chatRoomService.getContacts(any(String.class))).thenReturn(List.of(user1, user2));

        MvcResult result = mockMvc.perform(get("/api/messages/id/contacts")
                .header("Authorization", token))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<User> contacts = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<User>>() {});
        assertThat(contacts.size()).isEqualTo(2);
        assertThat(contacts.get(0)).isEqualTo(user1);
        assertThat(contacts.get(1)).isEqualTo(user2);
    }

    @Test
    @DisplayName("New contact")
    public void newContactTest() throws Exception {
        User user1 = TokenMock.admin;
        User user2 = new User(new ObjectId(), "name", "email", "pass", Set.of(User.Role.ROLE_USER));
        NewContact newContact = new NewContact(user1.getId(), user2.getId());
        Mockito.when(chatRoomService.getChatId(any(String.class), any(String.class), any(Boolean.class)))
                .thenReturn(Optional.of("chatId"));

        MvcResult result = mockMvc.perform(post("/api/messages/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(newContact))
                .header("Authorization", token))
                .andExpect(status().isCreated())
                .andReturn();

        String chatId = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<String>() {});
        assertThat(chatId).isEqualTo("chatId");
    }
}
package bme.aut.unikonzi.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class ChatMessageTest {

    @DisplayName("Builder")
    @Test
    public void builderTest() {
        ChatMessage cm = ChatMessage.builder()
                .chatId("customChatId")
                .message("message")
                .build();

        assertThat(cm.getChatId()).isEqualTo("customChatId");
        assertThat(cm.getMessage()).isEqualTo("message");
    }

    @DisplayName("All args constructor")
    @Test
    public void allArgsConstructorTest() throws ParseException {
        ChatMessage cm = new ChatMessage("id", "chatId", "senderId", "recipientId",
                "senderName", "recipientName", "message",
                new SimpleDateFormat("yyyy-MM-dd hh:mm").parse("2021-04-10 13:26"), MessageStatus.RECEIVED);

        assertThat(cm.getId()).isEqualTo("id");
        assertThat(cm.getChatId()).isEqualTo("chatId");
        assertThat(cm.getSenderId()).isEqualTo("senderId");
        assertThat(cm.getRecipientId()).isEqualTo("recipientId");
        assertThat(cm.getSenderName()).isEqualTo("senderName");
        assertThat(cm.getRecipientName()).isEqualTo("recipientName");
        assertThat(cm.getMessage()).isEqualTo("message");

        Date date = new SimpleDateFormat("yyyy-MM-dd hh:mm").parse("2021-04-10 13:26");
        assertThat(cm.getTimestamp()).isEqualTo(date);

        assertThat(cm.getMessageStatus()).isEqualTo(MessageStatus.RECEIVED);
    }

    @DisplayName("Setter")
    @Test
    public void setterTest() {
        ChatMessage cm = ChatMessage.builder().senderName("Somebody").build();
        assertThat(cm.getSenderName()).isEqualTo("Somebody");
        cm.setSenderName("Anybody");
        assertThat(cm.getSenderName()).isEqualTo("Anybody");
    }
}

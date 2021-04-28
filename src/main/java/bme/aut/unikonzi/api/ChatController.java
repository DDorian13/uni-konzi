package bme.aut.unikonzi.api;

import bme.aut.unikonzi.model.ChatMessage;
import bme.aut.unikonzi.model.ChatNotification;
import bme.aut.unikonzi.model.payload.request.NewContact;
import bme.aut.unikonzi.service.ChatMessageService;
import bme.aut.unikonzi.service.ChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping(value = "api/messages", produces = "application/json")
@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private ChatRoomService chatRoomService;

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage chatMessage) {
        Optional<String> chatId = chatRoomService
                .getChatId(chatMessage.getSenderId(), chatMessage.getRecipientId(), true);
        chatMessage.setChatId(chatId.get());

        ChatMessage saved = chatMessageService.save(chatMessage);
        messagingTemplate.convertAndSendToUser(
                chatMessage.getRecipientId(), "/queue/messages",
                new ChatNotification(
                        saved.getId(),
                        saved.getSenderId(),
                        saved.getSenderName()
                )
        );
    }

    @GetMapping("{recipientId}/has-new")
    public ResponseEntity<Integer> hasNewMessageFromAnybody(@PathVariable("recipientId") String recipientId) {
        return ResponseEntity.ok(chatMessageService.hasNewMessageFromAnybody(recipientId));
    }

    @GetMapping("{senderId}/{recipientId}/count")
    public ResponseEntity<Long> countNewMessages(@PathVariable("senderId") String senderId,
                                                 @PathVariable("recipientId") String recipientId) {
        return ResponseEntity.ok(chatMessageService.countNewMessages(senderId, recipientId));
    }

    @GetMapping("{senderId}/{recipientId}")
    public ResponseEntity<?> findChatMessages (@PathVariable("senderId") String senderId,
                                               @PathVariable("recipientId") String recipientId) {
        return ResponseEntity.ok(chatMessageService.findChatMessages(senderId, recipientId));
    }

    @GetMapping("{id}")
    public ResponseEntity<?> findMessage (@PathVariable("id") String id){
        return ResponseEntity.ok(chatMessageService.findById(id));
    }

    @GetMapping("{senderId}/contacts")
    public ResponseEntity<?> findContacts(@PathVariable("senderId") String senderId) {
        return ResponseEntity.ok(chatRoomService.getContacts(senderId));
    }

    @PostMapping("new")
    public ResponseEntity<?> newContact(@Valid @NonNull @RequestBody NewContact contact){
        return new ResponseEntity<>(
                chatRoomService.getChatId(contact.getSenderId(), contact.getRecipientId(), true),
                HttpStatus.CREATED);
    }
}

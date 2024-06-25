package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChatDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MessageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MessageDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.service.MessageService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = MessageEndpoint.BASE_PATH)
public class MessageEndpoint {
    static final String BASE_PATH = "/api/v1/messages";
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    public MessageEndpoint(MessageService messageService, SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        this.messageService = messageService;
    }

    /**
     * Send a message to a treatment chat.
     *
     * @param message        the message to send
     * @param headerAccessor the header accessor
     */
    @MessageMapping("/chat/{to}")
    public void sendMessage(@Payload @Valid MessageDtoCreate message, SimpMessageHeaderAccessor headerAccessor) {
        LOG.info("Message sent to treatment chat: {}", message.treatmentId());
        MessageDto returnMessage = messageService.sendMessage(message, headerAccessor.getUser());
        if (returnMessage != null) {
            messagingTemplate.convertAndSend("/topic/messages/" + message.treatmentId(), returnMessage);
        }
    }

    /**
     * Get all messages for a treatment.
     *
     * @param treatmentId the id of the treatment
     * @return the chat
     */
    @Secured({"PATIENT", "DOCTOR"})
    @GetMapping("/{treatmentId}")
    public ChatDto getMessages(@PathVariable("treatmentId") long treatmentId) {
        LOG.info("Get messages for treatment: {}", treatmentId);
        return messageService.getChat(treatmentId);
    }

    /**
     * Get all active chats.
     *
     * @return list of active chats
     */
    @GetMapping("/active")
    @Secured({"PATIENT", "DOCTOR"})
    public List<ChatDto> getActiveChats() {
        LOG.info("Get active chats}");
        return messageService.getChats(true);
    }

    /**
     * Get all available chats.
     *
     * @return list of available chats
     */
    @GetMapping("/available")
    @Secured("PATIENT")
    public List<ChatDto> getAvailableChats() {
        LOG.info("Get available chats}");
        return messageService.getChats(false);
    }
}

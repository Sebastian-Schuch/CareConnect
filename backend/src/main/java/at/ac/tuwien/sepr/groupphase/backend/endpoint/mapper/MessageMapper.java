package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MessageDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Component
public class MessageMapper {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public MessageDto messageEntityToMessageDto(Message message) {
        LOG.trace("messageEntityToMessageDto({})", message);
        return new MessageDto(
            message.getId(),
            message.getContent(),
            message.getTimestamp(),
            message.getTreatment().getId(),
            message.getSender().getFirstName().strip() + " " + message.getSender().getLastName().strip(),
            message.getSender().getEmail(),
            message.isRead());
    }

    public List<MessageDto> messageEntitiesToMessageDtos(List<Message> messages) {
        LOG.trace("messageEntitiesToMessageDtos({})", messages);
        List<MessageDto> messageDtos = new ArrayList<>();
        for (Message message : messages) {
            messageDtos.add(messageEntityToMessageDto(message));
        }
        return messageDtos;
    }
}

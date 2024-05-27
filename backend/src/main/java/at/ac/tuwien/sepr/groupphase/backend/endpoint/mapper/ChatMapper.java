package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChatDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Message;
import at.ac.tuwien.sepr.groupphase.backend.entity.Treatment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.text.SimpleDateFormat;
import java.util.List;

@Component
public class ChatMapper {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final MessageMapper messageMapper;

    public ChatMapper(MessageMapper messageMapper) {
        this.messageMapper = messageMapper;
    }

    public ChatDto treatmentAndMessagesToChatDto(Treatment treatment, List<Message> messages) {
        LOG.trace("treatmentAndMessagesToChatDto({}, {})", treatment, messages);

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yy HH:mm");
        String formattedDateTime = formatter.format(treatment.getTreatmentStart());

        return new ChatDto(treatment.getId(), treatment.getTreatmentTitle() + " (" + formattedDateTime + ")",
            messageMapper.messageEntitiesToMessageDtos(messages));
    }
}

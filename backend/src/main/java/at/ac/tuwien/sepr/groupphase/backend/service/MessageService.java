package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChatDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MessageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MessageDtoCreate;

import java.security.Principal;
import java.util.List;

public interface MessageService {

    /**
     * Send a content to a chat.
     *
     * @param message the content to send
     * @param user    the email of the user sending the message
     */
    MessageDto sendMessage(MessageDtoCreate message, Principal user);

    /**
     * Get all open chats.
     *
     * @param activeChats whether to get the already active chats
     * @return list of all chats but only containing the last content
     */
    List<ChatDto> getChats(boolean activeChats);

    /**
     * Get all messages of a chat.
     *
     * @param chatId the chatId to get the messages from
     * @return list of all messages of the chat
     */
    ChatDto getChat(Long chatId);

}

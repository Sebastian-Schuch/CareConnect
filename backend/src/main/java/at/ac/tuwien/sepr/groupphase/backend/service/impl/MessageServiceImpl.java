package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChatDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorDtoSparse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MessageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MessageDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDtoSparse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ChatMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;
import at.ac.tuwien.sepr.groupphase.backend.entity.Message;
import at.ac.tuwien.sepr.groupphase.backend.entity.Treatment;
import at.ac.tuwien.sepr.groupphase.backend.repository.CredentialRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.MessageRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PatientRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.TreatmentRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.DoctorService;
import at.ac.tuwien.sepr.groupphase.backend.service.MessageService;
import at.ac.tuwien.sepr.groupphase.backend.service.PatientService;
import at.ac.tuwien.sepr.groupphase.backend.service.TreatmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class MessageServiceImpl implements MessageService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final CustomUserDetailService customUserDetailService;

    private final PatientService patientService;

    private final DoctorService doctorService;

    private final TreatmentService treatmentService;
    private final CredentialRepository credentialRepository;
    private final TreatmentRepository treatmentRepository;
    private final MessageRepository messageRepository;
    private final ChatMapper chatMapper;

    public MessageServiceImpl(PatientService patientService, DoctorService doctorService, TreatmentService treatmentService,
                              CredentialRepository credentialRepository, TreatmentRepository treatmentRepository, MessageRepository messageRepository,
                              ChatMapper chatMapper, PatientRepository patientRepository, CustomUserDetailService customUserDetailService) {
        this.customUserDetailService = customUserDetailService;
        this.treatmentService = treatmentService;
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.credentialRepository = credentialRepository;
        this.treatmentRepository = treatmentRepository;
        this.messageRepository = messageRepository;
        this.chatMapper = chatMapper;
    }

    @Override
    public MessageDto sendMessage(MessageDtoCreate message, Principal user) {
        LOG.trace("sendMessage({},{})", message, user.getName());
        if (isAllowedToChat(user, message.treatmentId())) {
            Credential userCredentials = customUserDetailService.findApplicationUserByEmail(user.getName());
            Treatment treatment = treatmentService.getTreatmentEntityById(message.treatmentId());
            Message messageEntity = new Message()
                .setContent(message.content())
                .setTreatment(treatment)
                .setSender(userCredentials)
                .setTimestamp(LocalDateTime.now())
                .setRead(false);
            messageEntity = messageRepository.save(messageEntity);

            return new MessageDto(1L, messageEntity.getContent(),
                messageEntity.getTimestamp(),
                messageEntity.getTreatment().getId(),
                userCredentials.getFirstName() + " " + userCredentials.getLastName(),
                user.getName(),
                messageEntity.isRead());
        }
        LOG.warn("User {} is not allowed to chat with treatment {}", user.getName(), message.treatmentId());
        return null;
    }

    @Override
    public List<ChatDto> getChats(boolean activeChats) {
        LOG.trace("getChats({})", activeChats);
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("DOCTOR"))) {
            return getChatsAsDoctor(principal);
        } else {
            return getChatsAsPatient(principal, activeChats);
        }
    }

    private List<ChatDto> getChatsAsPatient(Object principal, boolean activeChats) {
        LOG.trace("getChatsAsPatient({},{})", principal, activeChats);
        String email = principal.toString();
        Credential user = credentialRepository.findByEmailAndActiveTrue(email);
        List<Treatment> chatRooms;
        List<Message> messages;
        List<ChatDto> chats = new ArrayList<>();
        if (activeChats) {
            chatRooms = treatmentRepository.findTreatmentsWithExistingMessagesForPatientCredentials(user.getId());
            messages = messageRepository.findLatestMessagesForTreatments(chatRooms.stream().map(Treatment::getId).toList());

            for (int i = 0; i < chatRooms.size(); i++) {
                chats.add(chatMapper.treatmentAndMessagesToChatDto(chatRooms.get(i), List.of(messages.get(i))));
            }
        } else {
            chatRooms = treatmentRepository.findTreatmentsWithoutExistingMessagesForPatientCredentials(user.getId());

            for (Treatment chatRoom : chatRooms) {
                chats.add(chatMapper.treatmentAndMessagesToChatDto(chatRoom, List.of()));
            }
        }
        return chats;
    }

    private List<ChatDto> getChatsAsDoctor(Object principal) {
        LOG.trace("getChatsAsDoctor({})", principal);
        String email = principal.toString();
        Credential user = credentialRepository.findByEmailAndActiveTrue(email);
        List<Treatment> chatRooms = treatmentRepository.findTreatmentsByDoctorsCredentialsWithMessages(user.getId());
        List<Message> messages = messageRepository.findLatestMessagesForTreatments(chatRooms.stream().map(Treatment::getId).toList());
        List<ChatDto> chats = new ArrayList<>();
        for (int i = 0; i < chatRooms.size(); i++) {
            chats.add(chatMapper.treatmentAndMessagesToChatDto(chatRooms.get(i), List.of(messages.get(i))));
        }

        return chats;
    }

    @Override
    public ChatDto getChat(Long chatId) {
        LOG.trace("getChat({})", chatId);
        Principal user = SecurityContextHolder.getContext().getAuthentication();
        if (isAllowedToChat(user, chatId)) {
            Treatment chatRoom = treatmentRepository.findById(chatId).orElseThrow(NoSuchElementException::new);
            List<Message> messages = messageRepository.findByTreatment_IdOrderByTimestampAsc(chatId);
            if (messages.isEmpty()) {
                return chatMapper.treatmentAndMessagesToChatDto(chatRoom, List.of());
            }

            return chatMapper.treatmentAndMessagesToChatDto(messages.get(0).getTreatment(), messages);
        }
        return null;
    }

    private boolean isAllowedToChat(Principal user, Long treatmentId) {
        LOG.trace("isAllowedToChat({},{})", user.getName(), treatmentId);
        UserDetails userDetails = this.customUserDetailService.loadUserByUsername(user.getName());
        if (userDetails.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("PATIENT"))) {
            Credential cred = customUserDetailService.findApplicationUserByEmail(userDetails.getUsername());
            PatientDtoSparse patient = this.patientService.findPatientByCredential(cred);
            List<TreatmentDto> treatments = treatmentService.getAllTreatmentsFromPatient(patient.id());
            return treatments.stream().anyMatch(t -> t.id() == (treatmentId));
        } else if (userDetails.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("DOCTOR"))) {
            Credential cred2 = customUserDetailService.findApplicationUserByEmail(userDetails.getUsername());
            DoctorDtoSparse doctor = this.doctorService.findDoctorByCredential(cred2);
            List<TreatmentDto> treatments = treatmentService.getAllTreatmentsFromDoctor(doctor.id());
            return treatments.stream().anyMatch(t -> t.id() == (treatmentId));
        }

        return false;
    }
}

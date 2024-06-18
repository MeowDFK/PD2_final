package com.example.application.data.service;


import com.example.application.data.entity.ChatRoom;
import com.example.application.data.entity.Message;
import com.example.application.data.entity.User;
import com.example.application.data.repository.ChatRoomRepository;
import com.example.application.data.repository.MessageRepository;
import com.example.application.data.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.ArrayList;
import javax.annotation.processing.Messager;
import dev.hilla.exception.EndpointException;

@Service
public class ChatService {
    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserRepository userRepository;

    
    private final Map<Long, Sinks.Many<MessageRecord>> chatRoomSinks = new HashMap<>();

    private static final Logger logger = Logger.getLogger(ChatService.class.getName());
    public record MessageRecord(
        
        @NotNull
        String username,
        @NotNull
        String content,
        @NotNull
        Instant timestamp
    ){}
    private MessageRecord toMessageRecord(Message m){
        return new MessageRecord(
        
            m.getSender().getUsername(),         
            m.getContent(),
            m.getTimestamp()
        );
    }
    public ChatRoom FindChatRoomByUsersAndCreate(User user1, User user2){
        Optional<ChatRoom> room =chatRoomRepository.findByUsers(user1,user2);  
        if(room.isPresent())return room.get();
        else return createChatRoom(user1, user2);
    }
  
    public ChatService(ChatRoomRepository chatRoomRepository) {
        this.chatRoomRepository = chatRoomRepository;
        // Ensure that the map is properly initialized for all existing chat rooms
        initializeChatRoomSinks();
    }

    private void initializeChatRoomSinks() {
        List<ChatRoom> chatRooms = chatRoomRepository.findAll();
        for (ChatRoom chatRoom : chatRooms) {
            chatRoomSinks.put(chatRoom.getId(), Sinks.many().multicast().directBestEffort());
            logger.info("Initialized chat room sink for room ID: " + chatRoom.getId());
           
           
        }
    }

    public ChatRoom createChatRoom(User user1, User user2) {
        if (user1.equals(user2)) {
            throw new EndpointException("A chat room must have two different users.");
        }

        Optional<ChatRoom> existingChatRoom = chatRoomRepository.findByUsers(user1, user2);
        if (existingChatRoom.isPresent()) {
            return existingChatRoom.get();
        }

        ChatRoom chatRoom = new ChatRoom(user1, user2);
        chatRoom = chatRoomRepository.save(chatRoom);
        chatRoomSinks.put(chatRoom.getId(), Sinks.many().multicast().directBestEffort());
        logger.info("Created and initialized chat room sink for room ID: " + chatRoom.getId());
        return chatRoom;
    }

    public List<ChatRoom> getUserChatRooms(User user) {
        return chatRoomRepository.findByUser1OrUser2(user, user).orElseThrow(() -> new EndpointException("Chat room not found"));
    }

    public Optional<ChatRoom> getChatRoomById(Long id) {
        return chatRoomRepository.findById(id);
    }

    public Flux<MessageRecord> joinChatRoom(Long chatRoomId) {
        Sinks.Many<MessageRecord> sink = chatRoomSinks.get(chatRoomId);
        if(!(chatRoomRepository.findById(chatRoomId) == null) &&sink == null){
            initializeChatRoomSinks();
        }
        sink = chatRoomSinks.get(chatRoomId);
        if (sink == null) {
            
            logger.severe("Chat room sink not found for room ID: " + chatRoomId);
            throw new EndpointException("Chat room not found");
        }
        
    
        return sink.asFlux();
    }
    public void enterRoom(Long chatRoomId,User user) {
        logger.info("Joined chat room ID: " + chatRoomId);
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
        .orElseThrow(() -> new EndpointException("Chat room not found"));
        Sinks.Many<MessageRecord> sink = chatRoomSinks.get(chatRoomId);
        for(Message history: chatRoom.getMessages()){
            MessageRecord msgHis =new MessageRecord(
       
                history.getSender().getUsername(),         
                history.getContent(),
                history.getTimestamp()
                );
            if (sink != null) {
                sink.tryEmitNext(msgHis);
                
            }
        }
        
    }
    public MessageRecord sendMessage(Long chatRoomId, User sender, String content) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new EndpointException("Chat room not found"));
    
        Message message = new Message();
        message.setChatRoom(chatRoom);
        message.setSender(sender);
        message.setContent(content);
        message.setTimestamp(Instant.now());
        messageRepository.save(message);
        logger.info("Sent message:" +message.getContent()+"sender:"+sender.getUsername());
        // Adding the message to the chat room's messages
        
        Sinks.Many<MessageRecord> sink = chatRoomSinks.get(chatRoomId);
        // Emit the message to subscribers
        MessageRecord msg =new MessageRecord(
           
            message.getSender().getUsername(),         
            message.getContent(),
            message.getTimestamp()
            );
            
        chatRoom.getMessages().add(message);
        chatRoomRepository.save(chatRoom);
        
        if (sink != null) {
            sink.tryEmitNext(msg);
            
        }
        return msg ;
    }

    public List<MessageRecord> getMessages(ChatRoom chatRoom) {
        List<MessageRecord> messageRecords = new ArrayList<MessageRecord>();
        for(Message msg:messageRepository.findByChatRoom(chatRoom)){
            messageRecords.add(toMessageRecord(msg));
        }
        return messageRecords;
    }

    public User getOtherUserInChatRoom(Long chatRoomId, String Account) {
        ChatRoom chatRoom = getChatRoomById(chatRoomId).orElseThrow(() -> new IllegalArgumentException("Chat room not found"));
       
        return chatRoom.getOtherUser(userRepository.findByAccount(Account).orElseThrow(() -> new IllegalArgumentException("User in room not found")));
    }
}

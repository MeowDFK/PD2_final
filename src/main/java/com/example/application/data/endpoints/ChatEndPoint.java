// src/main/java/com/example/application/endpoints/ChatEndpoint.java
package com.example.application.data.endpoints;

import com.example.application.data.service.UserService.UserRecord;
import com.example.application.data.entity.ChatRoom;
import com.example.application.data.entity.Message;
import com.example.application.data.entity.User;
import com.example.application.data.service.ChatService;
import com.example.application.data.service.UserService;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import dev.hilla.BrowserCallable;
import dev.hilla.Endpoint;
import reactor.core.publisher.Flux;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import dev.hilla.exception.EndpointException;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@BrowserCallable
@AnonymousAllowed
public class ChatEndPoint {

    private final ChatService chatService;
    private final UserService userService;
    
    public record RoomRecord(
        Long id,
        @NotNull
        UserRecord user1,
        @NotNull
        UserRecord user2
    ){}
    private RoomRecord toRoomRecord(ChatRoom c) {
        return new RoomRecord(
                c.getId(),
                new  UserRecord(c.getUser1().getId(),
                                c.getUser1().getAccount(),
                                c.getUser1().getPassword(),
                                c.getUser1().getPassword(),
                                c.getUser1().getUsername()
                                ),              
                new  UserRecord(c.getUser2().getId(),
                                c.getUser2().getAccount(),
                                c.getUser2().getPassword(),
                                c.getUser1().getPassword(),
                                c.getUser2().getUsername()
                                )           

        );
    }
    
    public ChatEndPoint(ChatService chatService, UserService userService) {
        this.chatService = chatService;
        this.userService = userService;
    }
    public ChatRoom FindRoomByUsersAndCreate(String account1,String account2){
        User user1 = userService.getUserByAccount(account1);
        User user2 = userService.getUserByAccount(account2);
        return  chatService.FindChatRoomByUsersAndCreate(user1, user2);
    }
    public void enterRoom(Long chatRoomId, String account) {
        ChatRoom chatRoom = chatService.getChatRoomById(chatRoomId)
            .orElseThrow(() -> new EndpointException("Chat room not found"));
        User sender = userService.getUserByAccount(account);
       chatService.enterRoom(chatRoomId, sender);
    }
    public List<RoomRecord> getUserChatRooms(String account) {
        User user = userService.findByAccount(account)
            .orElseThrow(() -> new EndpointException("User not found"));
               return chatService.getUserChatRooms(user).stream()
            .map(this::toRoomRecord) // Convert to ChatRoom objects
            .collect(Collectors.toList()); // Collect into a List

    }

    public ChatRoom createChatRoom(String account1, String account2) {
        User user1 = userService.getUserByAccount(account1);
        User user2 = userService.getUserByAccount(account2);
        return chatService.createChatRoom(user1, user2);
    }

    public List<ChatService.MessageRecord> getMessages(Long chatRoomId) {
        ChatRoom chatRoom = chatService.getChatRoomById(chatRoomId)
            .orElseThrow(() -> new EndpointException("Chat room not found"));
        return chatService.getMessages(chatRoom);
    }
 
    public ChatService.MessageRecord sendMessage(Long chatRoomId, String account, String content) {
        ChatRoom chatRoom = chatService.getChatRoomById(chatRoomId)
            .orElseThrow(() -> new EndpointException("Chat room not found"));
        User sender = userService.getUserByAccount(account);
        return chatService.sendMessage(chatRoomId, sender, content);
         // Convert to ChatRoom objects // Collect into a List;
    }

    public Flux<ChatService.MessageRecord> joinChatRoom(Long chatRoomId) {
        return chatService.joinChatRoom(chatRoomId);
    }

    public UserRecord getOtherUserInChatRoom(Long chatRoomId, String account) {
        return userService.toUserRecord(chatService.getOtherUserInChatRoom(chatRoomId, account));
    }
}
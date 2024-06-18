package com.example.application.data;

import com.example.application.data.entity.ChatRoom;
import com.example.application.data.entity.Message;
import com.example.application.data.entity.User;
import com.example.application.data.repository.ChatRoomRepository;
import com.example.application.data.repository.MessageRepository;
import com.example.application.data.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;


@Component
@Transactional
public class DataInitializer2 implements CommandLineRunner {

    private static final Logger logger = Logger.getLogger(DataInitializer2.class.getName());

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Override
    public void run(String... args) throws Exception {
        try {
            // Create users if not already present
            User user1 = createUserIfNotExist("user1","Bob", "password1","ENFP");
            User user2 = createUserIfNotExist("user2","Judy", "password2","INFP" );
            User user3 = createUserIfNotExist("user3","Rrrr", "password3","ENTJ" );

            // Create chat rooms if not already present
            ChatRoom chatRoom1 = createChatRoomIfNotExist(user1, user2);
            ChatRoom chatRoom2 = createChatRoomIfNotExist(user1, user3);

            // Create messages in chat rooms
            createMessage(chatRoom1, user1, "Hello, user2!");
            createMessage(chatRoom2, user1, "Hello, user3!");

            logger.info("Data initialization completed successfully.");
        } catch (Exception e) {
            logger.severe("Data initialization failed: " + e.getMessage());
            throw e; // rethrow to ensure transaction rollback
        }
    }

    private User createUserIfNotExist(String account,String username, String password,String Mbti) {
        String encodedPassword = password; // Encode the password if needed
        logger.info("Encoded password: " + encodedPassword); // Log the encoded password (for debugging purposes, usually not recommended to log passwords)

        return userRepository.findByAccount(account).orElseGet(() -> {
            User user = new User();
            user.setUsername(username);
            user.setPassword(encodedPassword); // Save the encoded password to the user
            user.setAccount(account); // Assuming the account is the same as username for this example
            user.setGender("Male"); // Default value if gender is not provided
            user.setMbti(Mbti); // Default value if MBTI is not provided
            user.setSports(new ArrayList<String>());
            user.addSport("swimming");
            user.setMovies(new ArrayList<String>());
            user.addMovie("horror");
            user.setFoods(new ArrayList<String>());
            user.addFood("hotpot");
            logger.info("Creating user: " + username);
            return userRepository.save(user);
        });
    }

    private ChatRoom createChatRoomIfNotExist(User user1, User user2) {
        Optional<ChatRoom> chatRoomOptional = chatRoomRepository.findByUser1AndUser2(user1, user2);
        if (chatRoomOptional.isPresent()) {
            return chatRoomOptional.get();
        } else {
            ChatRoom chatRoom = new ChatRoom();
            chatRoom.setUser1(user1);
            chatRoom.setUser2(user2);
            logger.info("Creating chat room between users: " + user1.getAccount() + " and " + user2.getAccount());
            return chatRoomRepository.save(chatRoom);
        }
    }

    private void createMessage(ChatRoom chatRoom, User sender, String content) {
        Message message = new Message();
        message.setChatRoom(chatRoom);
        message.setSender(sender);
        message.setContent(content);
        message.setTimestamp(Instant.now());
        logger.info("Creating message from " + sender.getAccount() + ": " + content);
        messageRepository.save(message);

        // Adding the message to the chatRoom's messages
        Set<Message> messages = new HashSet<>(chatRoom.getMessages());
        messages.add(message);
        chatRoom.setMessages(messages);
        chatRoomRepository.save(chatRoom);
    }
}

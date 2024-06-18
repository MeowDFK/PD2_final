package com.example.application.data.repository;

import com.example.application.data.entity.ChatRoom;
import com.example.application.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long>, JpaSpecificationExecutor<ChatRoom>{
    Optional<List<ChatRoom>> findByUser1OrUser2(User user1, User user2);

    // Additional method to check if a chat room exists between two users
    Optional<ChatRoom> findByUser1AndUser2(User user1, User user2);
    Optional<ChatRoom> findByUser2AndUser1(User user2, User user1);

    // Combine methods to simplify the check for a chat room between two users
    default Optional<ChatRoom> findByUsers(User user1, User user2) {
        return findByUser1AndUser2(user1, user2)
                .or(() -> findByUser2AndUser1(user1, user2));
    }
}

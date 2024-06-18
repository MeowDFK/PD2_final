// src/main/java/com/example/application/repository/MessageRepository.java
package com.example.application.data.repository;

import com.example.application.data.entity.ChatRoom;
import com.example.application.data.entity.Message;
import com.example.application.data.entity.User;
import com.vaadin.pro.licensechecker.Product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> ,JpaSpecificationExecutor<Message> {
    List<Message> findByChatRoom(ChatRoom chatRoom);
}

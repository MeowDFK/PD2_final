package com.example.application.data.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.*;
import java.util.*;
import com.example.application.data.entity.*;

@Repository("matchRepository")
public interface MatchRepository extends JpaRepository<Match, Long> {

    Match getReferenceById(Long ID); // 用該天室的ID找聊天室

    //List<Match> findByUser1OrUser2(User user1, User user2);

    List<Match> findByUser1AndUser2(User user1, User user2);// 應該只會找到一個
    List<Match> findByUser1(User user1);
    List<Match> findByUser2(User user2);

    //List<Match> findByMatchedTime(Instant matchedTime);

    List<Match> findByMatchedDate(LocalDate matchedDate);

    List<Match> findByUser1AndMatchedDate(User user1, LocalDate date);

    List<Match> findByUser2AndMatchedDate(User user2, LocalDate date);
}
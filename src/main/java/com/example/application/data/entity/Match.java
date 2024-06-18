package com.example.application.data.entity;

import java.time.*;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Match implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    @ManyToOne
    @JoinColumn(name = "user1_id", nullable = false)
    private User user1;

    @ManyToOne
    @JoinColumn(name = "user2_id", nullable = false)
    private User user2;

    @Column(name = "matched_date", nullable = false)
    private LocalDate matchedDate;

    public Match() {
    }

    public void setUser1(User user1){
        this.user1 = user1;
    }
    public User getUser1(){
        return this.user1;
    }

    public void setUser2(User user2){
        this.user2 = user2;
    }
    public User getUser2(){
        return this.user2;
    }

    public User getOtherUser(User user) {
        if (user1.equals(user)) {
            return user2;
        } else {
            return user1;
        }
    }

    public Match(User user1, User user2, LocalDate matchedDate) {
        this.user1 = user1;
        this.user2 = user2;
        //this.matchedTime = matchedTime;
        this.matchedDate = matchedDate;
    }
}
package com.example.application.data.endpoints;

import com.example.application.data.service.AIChatService;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import dev.hilla.BrowserCallable;
import dev.hilla.Endpoint;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;

@BrowserCallable
@AnonymousAllowed
public class AIChatEndpoint {
    @Autowired
    private AIChatService chatService;
    //private UserService userService;

    public Flux<String> chatStream(String message){
        return chatService.chatStream(message);
    }
    public String getDivination1(String mbti){
        /*String account = userEndPoint.getCurrentUser();
        String mbti = userService.getUserByAccount(account).getMbti();
        return aiService.getDivination(mbti);*/
       
        return chatService.getDivination(mbti);
    } 
}

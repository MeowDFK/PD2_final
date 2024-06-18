package com.example.application.data.service;

import com.example.application.data.entity.User;
import com.example.application.data.repository.UserRepository;
import com.vaadin.flow.server.VaadinRequest;

import dev.hilla.exception.EndpointException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class UserService {

    private static final Logger logger = Logger.getLogger(UserService.class.getName());

    private final UserRepository userRepository;
   
    public record Userhobbies(
        @NotNull
        @NotBlank
        String mbti,
        String age,
        String gender,
        List<String> sports,
        List<String> movies,
        List<String> foods
    ){}
    public record UserRecord(
        Long id,
        @NotNull
        @NotBlank
        String account,
        @NotBlank
        @NotNull
        String password,
        @NotBlank
        @NotNull
        String repeatPassword,
        @NotBlank
        @NotNull
        String username
    ){}

    public UserRecord toUserRecord(User u){
        return new UserRecord(
            u.getId(),
            u.getAccount(),
            u.getPassword(),
            u.getPassword(),
            u.getUsername()

        );
    }

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        
    }
    
    public UserRecord registerUser( 
            String account,
            String password,
            String nickName
            ) {
        logger.info("Registering user: " + account);
        logger.info("Registering user: " + password);
        logger.info("Registering user: " + nickName);
        if (userRepository.findByAccount(account).isPresent() || userRepository.findByPassword(password).isPresent()) {
            throw new EndpointException("Username or email already exists");
           
        }

        User user = new User();
        user.setAccount(account);
        user.setPassword(password);
        user.setUsername(nickName);
        userRepository.save(user);
        logger.info("Registering user: " + account+"seccess");
        return toUserRecord(user);
    }

    public Optional<User> findByAccount(String account) {
        return userRepository.findByAccount(account);
    }

     public UserService.UserRecord login(String account, String password, HttpSession session) {
        Optional<User> userOpt = userRepository.findByAccountAndPassword(account, password);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            session.setAttribute("currentUser", user); // Store user in session
            logger.info("Registering user: "+user+"login seccess");
            return toUserRecord(user);
        } else {
            throw new EndpointException("Invalid username or password");
        }
    }
    public void setUserHobbies(String account, Userhobbies hobbies){
        Optional<User> userOpt = findByAccount(account);
        if (userOpt.isPresent()) {
            
            User user = userOpt.get();
            user.setMbti(hobbies.mbti());
            user.setGender(hobbies.gender());
            user.setAge(hobbies.age());
            user.setFoods(hobbies.foods());
            user.setMovies(hobbies.movies());
            user.setSports(hobbies.sports());
            logger.info("hobbies"+hobbies.mbti()+hobbies.age()+hobbies.gender()+hobbies.foods()+hobbies.movies()+hobbies.sports());
            logger.info("hobbies"+user.getMbti()+user.getAge()+user.getGender()+user.getFoods()+user.getMovies()+user.getSports());
            userRepository.save(user);
        } else {
            throw new EndpointException("Invalid account ");
        }
    }
    public User getUserByAccount(String account) {
        return userRepository.findByAccount(account).orElseThrow(() -> new EndpointException("User not found by account"+account));
    }
    public void logout(HttpSession session) {
        session.invalidate(); // Invalidate the session
    }
    public User getCurrentUser(HttpSession session) {
        return (User) session.getAttribute("currentUser"); // Retrieve user from session
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new EndpointException("User not found"));
    }

    
}

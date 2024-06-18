package com.example.application.data.entity;

import jakarta.persistence.*;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "account")
    private String account;

    @NotBlank
    @Column(name = "password", nullable = false)
    private String password;

    @NotBlank
    @Column(name = "user_name", nullable = false)
    private String username;

    @Column(name = "gender")
    private String gender;


    @Column(name = "age")
    private String age;

    @Column(name = "mbti")
    private String mbti;

    @ElementCollection
    @CollectionTable(name = "user_sports", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "sport")
    private List<String> sports;

    @ElementCollection
    @CollectionTable(name = "user_movies", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "movie")
    private List<String> movies;

    @ElementCollection
    @CollectionTable(name = "user_foods", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "food")
    private List<String> foods;

    @Column(name = "other_guy_want_match_me", nullable = false)
    private boolean otherGuyWantMatchMe = false;

    @Column(name = "accept_match", nullable = false)
    private boolean acceptMatch = false;

    public User() {}

    public User(String username, String password, String account) {
        this.username = username;
        this.password = password;
        this.account = account;
     
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getMbti() {
        return mbti;
    }

    public void setMbti(String mbti) {
        this.mbti = mbti;
    }

    public List<String> getSports() {
        return sports;
    }

    public void setSports(List<String> sports) {
        this.sports = sports;
    }

    public List<String> getMovies() {
        return movies;
    }

    public void setMovies(List<String> movies) {
        this.movies = movies;
    }

    public List<String> getFoods() {
        return foods;
    }

    public void setFoods(List<String> foods) {
        this.foods = foods;
    }

    public boolean isOtherGuyWantMatchMe() {
        return otherGuyWantMatchMe;
    }

    public void setOtherGuyWantMatchMe(boolean otherGuyWantMatchMe) {
        this.otherGuyWantMatchMe = otherGuyWantMatchMe;
    }

    public boolean isAcceptMatch() {
        return acceptMatch;
    }
    
    public boolean getOtherGuyWantMatchMe() {
        return this.otherGuyWantMatchMe;
    }


    public void setAcceptMatch(boolean acceptMatch) {
        this.acceptMatch = acceptMatch;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void addSport(String sport) {
        this.sports.add(sport);
    }

    public void removeSport(String sport) {
        this.sports.remove(sport);
    }

    public void addMovie(String movie) {
        this.movies.add(movie);
    }

    public void removeMovie(String movie) {
        this.movies.remove(movie);
    }

    public void addFood(String food) {
        this.foods.add(food);
    }

    public void removeFood(String food) {
        this.foods.remove(food);
    }
}

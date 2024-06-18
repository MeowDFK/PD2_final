package com.example.application.data.repository;

import com.example.application.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByUsername(String userName);
    Optional<User> findByAccountAndPassword(String Account, String password); 
    Optional<User> findByAccount(String account); 
    Optional<User> findByPassword(String password); 
    List<User> findByMbtiStartingWith(String prefix); //找到 i人或 e人
}

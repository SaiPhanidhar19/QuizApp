package com.phani.quizApp.dao;

import com.phani.quizApp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDao extends JpaRepository<User, Long> {

    public Optional<User> findByUsername(String username);
}

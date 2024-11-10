package com.phani.quizApp.dao;

import com.phani.quizApp.entity.UserQuizMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserQuizMappingDao extends JpaRepository<UserQuizMapping, Long> {

    @Query(value = "SELECT * FROM user_quiz_mapping uq WHERE uq.quiz_id=:quizId ORDER BY score DESC, time_taken ASC "
            , nativeQuery = true)
    public List<UserQuizMapping> getLeaderBoard(String quizId);

    @Query(value = "SELECT * from user_quiz_mapping uq WHERE uq.user_id=:userId", nativeQuery = true)
    public List<UserQuizMapping> getUserQuizMappingByUserId(String userId);
}

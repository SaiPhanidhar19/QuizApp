package com.phani.quizApp.dtos;

import com.phani.quizApp.entity.Quiz;
import lombok.Data;

@Data
public class QuizWrapper extends Quiz {

    private int score;

    public QuizWrapper(Quiz quiz) {
        this.setId(quiz.getId());
        this.setCategory(quiz.getCategory());
        this.setDifficulty(quiz.getDifficulty());
        this.setQuestions(quiz.getQuestions());
        this.setTitle(quiz.getTitle());
        this.setCreatedDate(quiz.getCreatedDate());
        this.setUserId(quiz.getUserId());
    }
}

package com.phani.quizApp.dtos;

import lombok.Data;

import java.util.List;

@Data
public class GetUserQuizesResponse {

    private String userId;
    private ResultInfo resultInfo;
    private List<QuizWrapper> quizzes;
}

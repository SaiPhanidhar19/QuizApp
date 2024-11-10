package com.phani.quizApp.dtos;

import lombok.Data;

@Data
public class QuestionResponse {

    private Long questionId;
    private String selectedOption;
}

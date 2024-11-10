package com.phani.quizApp.dtos;

import lombok.Data;

import java.util.List;

@Data
public class QuizAPIResponse {

    List<QuestionDetails> questionDetails;
}

package com.phani.quizApp.dtos;

import com.phani.quizApp.entity.Quiz;
import lombok.Data;

@Data
public class CreateQuizResponse {

    private Quiz quiz;
    private ResultInfo resultInfo;
}

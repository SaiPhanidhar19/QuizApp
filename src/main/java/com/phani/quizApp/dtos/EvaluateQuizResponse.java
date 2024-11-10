package com.phani.quizApp.dtos;

import com.phani.quizApp.entity.Question;
import lombok.Data;

import java.util.List;

@Data
public class EvaluateQuizResponse {

    private Long quizId;
    private int score;
    private List<Question> questions;
    private ResultInfo resultInfo;
}

package com.phani.quizApp.dtos;

import com.phani.quizApp.entity.Quiz;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetQuizResponse {

    private Quiz quiz;
    private ResultInfo resultInfo;
}

package com.phani.quizApp.controllers;


import com.phani.quizApp.entity.Question;
import com.phani.quizApp.services.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("question")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @PostMapping("storeQuestions")
    public ResponseEntity<List<Question>> storeQuestions(@RequestParam String limit) {

        List<Question> questionList = questionService.storeQuestions(limit);
        return new ResponseEntity<>(questionList, HttpStatus.OK);
    }
}

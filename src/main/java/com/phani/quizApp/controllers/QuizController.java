package com.phani.quizApp.controllers;


import com.phani.quizApp.dtos.*;
import com.phani.quizApp.services.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("quiz")
@RestController
public class QuizController {

    @Autowired
    private QuizService quizService;

    @GetMapping("/get/{id}")
    public ResponseEntity<GetQuizResponse> getQuizById(@PathVariable String id) {
        GetQuizResponse getQuizResponse = quizService.getQuizById(id);
        return new ResponseEntity<>(getQuizResponse, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<CreateQuizResponse> createQuiz(@RequestBody CreateQuizRequest createQuizRequest) {
        CreateQuizResponse createQuizResponse = quizService.createQuiz(createQuizRequest);
        return new ResponseEntity<>(createQuizResponse, HttpStatus.CREATED);
    }

    @PostMapping("/evaluate")
    public ResponseEntity<EvaluateQuizResponse> evaluateQuiz(@RequestBody EvaluateQuizRequest evaluateQuizRequest) {
        EvaluateQuizResponse evaluateQuizResponse = quizService.evaluateQuiz(evaluateQuizRequest);
        return new ResponseEntity(evaluateQuizResponse, HttpStatus.OK);
    }

    @GetMapping("/get/leaderboard/{quizId}")
    public ResponseEntity<GetLeaderBoardResponse> getLeaderBoard(@PathVariable String quizId) {
        GetLeaderBoardResponse getLeaderBoardResponse = quizService.getLeaderBoard(quizId);
        return new ResponseEntity<>(getLeaderBoardResponse, HttpStatus.OK);
    }
}

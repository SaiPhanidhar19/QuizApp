package com.phani.quizApp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class QuizAPIService {

    @Autowired
    private RestTemplate restTemplate;

    public String getQuizQuestions(String quizapiURL) {
        //getting quiz questions from quiz API
        ResponseEntity<String> quizAPIResponse = new ResponseEntity<>(null, HttpStatus.OK);
        try {
            quizAPIResponse = restTemplate.exchange(quizapiURL, HttpMethod.GET, null, String.class);
        } catch (Exception e) {
            System.out.println("some error occurred while getting the quiz questions : " + e.getMessage());
        }
        return quizAPIResponse.getBody();
    }

}

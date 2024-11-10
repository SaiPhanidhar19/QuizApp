package com.phani.quizApp.controllers;

import com.phani.quizApp.dtos.*;
import com.phani.quizApp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest jwtRequest) {

        JwtResponse response = userService.login(jwtRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<UserSignUpResponse> signup(@RequestBody UserSignUpRequest userSignUpRequest) {

        UserSignUpResponse userSignUpResponse = userService.signUp(userSignUpRequest);
        return new ResponseEntity<>(userSignUpResponse, HttpStatus.CREATED);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<GetUserResponse> getUser(@PathVariable String id) {
        GetUserResponse userResponse = userService.getUser(id);
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @GetMapping("/get/quizzes/{userId}")
    public ResponseEntity<GetUserQuizesResponse> getQuizzesByUser(@PathVariable String userId) {
        GetUserQuizesResponse userQuizesResponse = userService.getUserQuizesResponse(userId);
        return new ResponseEntity<>(userQuizesResponse, HttpStatus.OK);
    }
}

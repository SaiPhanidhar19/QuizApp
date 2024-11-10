package com.phani.quizApp.dtos;

import lombok.Data;

@Data
public class UserSignUpRequest {

    private String username;
    private String password;
    private String passwordConfirmation;
}

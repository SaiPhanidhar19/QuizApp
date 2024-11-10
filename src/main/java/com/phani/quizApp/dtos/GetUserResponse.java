package com.phani.quizApp.dtos;

import lombok.Data;

@Data
public class GetUserResponse {

    private Long userId;
    private String username;
    private ResultInfo resultInfo;
}

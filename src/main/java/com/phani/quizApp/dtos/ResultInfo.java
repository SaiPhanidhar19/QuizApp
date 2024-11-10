package com.phani.quizApp.dtos;

import lombok.Data;

@Data
public class ResultInfo {

    private String status;
    private String message;

    public ResultInfo() {
        status = "SUCCESS";
        message = "success";
    }
}

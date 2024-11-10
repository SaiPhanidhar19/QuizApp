package com.phani.quizApp.dtos;


import lombok.Data;

import java.util.List;

@Data
public class GetLeaderBoardResponse {

    List<UserQuizDetails> leaderBoard;
    private ResultInfo resultInfo;
}

package com.phani.quizApp.services;

import com.phani.quizApp.dao.QuestionDao;
import com.phani.quizApp.dao.QuizDao;
import com.phani.quizApp.dao.UserDao;
import com.phani.quizApp.dao.UserQuizMappingDao;
import com.phani.quizApp.entity.Question;
import com.phani.quizApp.entity.Quiz;
import com.phani.quizApp.entity.User;
import com.phani.quizApp.entity.UserQuizMapping;
import com.phani.quizApp.dtos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QuizService {

    @Autowired
    private QuizDao quizDao;

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private UserQuizMappingDao userQuizMappingDao;

    @Autowired
    private UserDao userDao;

    private static final String url = "https://quizapi.io/api/v1/questions?apiKey=xMaFOkwskMeeENxfE29vH42ikGrJWaOyHpuB0Pz4";

    public GetQuizResponse getQuizById(String id) {
        //getting quiz by Id
        Quiz quiz = null;
        try {
            quiz = quizDao.findById(Long.valueOf(id)).get();
        } catch (Exception e) {
            System.out.println("no such Quiz exists with the given Id : " + id);
            GetQuizResponse getQuizResponse = new GetQuizResponse();
            getQuizResponse.setQuiz(null);
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setMessage("No such Quiz exists with the given Id!");
            resultInfo.setStatus("FAIL");
            getQuizResponse.setResultInfo(resultInfo);
            return getQuizResponse;
        }

        //setting answer as null while sending question details
        if(quiz != null) {
            for (Question question : quiz.getQuestions()) {
                question.setAnswer(null);
            }
        }
        GetQuizResponse getQuizResponse = new GetQuizResponse();
        getQuizResponse.setQuiz(quiz);
        getQuizResponse.setResultInfo(new ResultInfo());
        return getQuizResponse;
    }

    public CreateQuizResponse createQuiz(CreateQuizRequest createQuizRequest) {

        //getting quizQuestions from the question db
        List<Question> quizQuestions = null;

        try {
            quizQuestions = questionDao.findRandomQuestionsByCategory(createQuizRequest.getCategory(), createQuizRequest.getNumOfQues());
        } catch (Exception e) {
            System.out.println("some error occurred while fetching questions from question db : " + e.getMessage());
            return null;
        }
        System.out.println("questions fetched : " + quizQuestions);

        //creating QUIZ to store in DB
        Quiz quiz = new Quiz();
        quiz.setCategory(createQuizRequest.getCategory());
        quiz.setDifficulty(createQuizRequest.getDifficulty());
        quiz.setTitle(createQuizRequest.getTitle());
        quiz.setQuestions(quizQuestions);
        quiz.setUserId(getUserId());

        //storing quiz in db
        Quiz savedQuiz = null;
        try {
            savedQuiz = quizDao.save(quiz);
        } catch (Exception e) {
            System.out.println("some error occurred while saving quiz in db : " + e);
        }

        //removing answer while sending quiz details
        for(Question question : savedQuiz.getQuestions()) {
            question.setAnswer(null);
        }
        CreateQuizResponse createQuizResponse = new CreateQuizResponse();
        createQuizResponse.setQuiz(savedQuiz);
        ResultInfo resultInfo = new ResultInfo();
        createQuizResponse.setResultInfo(resultInfo);
        return createQuizResponse;
    }

    public EvaluateQuizResponse evaluateQuiz(EvaluateQuizRequest evaluateQuizRequest) {

        //getting quiz from db and evaluating user responses
        Quiz quiz = null;
        try {
            quiz = quizDao.findById(evaluateQuizRequest.getQuizId()).get();
        } catch (Exception e) {
            System.out.println("no such Quiz exists with the given Id : " + evaluateQuizRequest.getQuizId());
        }

        if(quiz == null) {
            System.out.println("no such Quiz exists with the given Id : " + evaluateQuizRequest.getQuizId());
            EvaluateQuizResponse evaluateQuizResponse = new EvaluateQuizResponse();
            evaluateQuizResponse.setQuizId(evaluateQuizRequest.getQuizId());
            evaluateQuizResponse.setScore(0);
            evaluateQuizResponse.setQuestions(null);
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setStatus("FAIL");
            resultInfo.setMessage("No Quiz exists with the given Id!");
            evaluateQuizResponse.setResultInfo(resultInfo);
            return evaluateQuizResponse;
        }

        int score = getScoreForTheQuiz(evaluateQuizRequest, quiz);
        System.out.println("user score : " + score);

        //storing userId, quizId, score in the user-quiz-mapping table
        UserQuizMapping userQuizMapping = new UserQuizMapping();
        userQuizMapping.setQuizId(quiz.getId());
        userQuizMapping.setUserId(getUserId());
        userQuizMapping.setScore(score);
        userQuizMapping.setTimeTaken(evaluateQuizRequest.getTimeTaken());

        try {
            userQuizMappingDao.save(userQuizMapping);
        } catch (Exception e) {
            System.out.println("some error occurred while storing in user-quiz-mapping table : " + e);
        }

        EvaluateQuizResponse evaluateQuizResponse = new EvaluateQuizResponse();
        evaluateQuizResponse.setQuizId(evaluateQuizRequest.getQuizId());
        evaluateQuizResponse.setScore(score);
        evaluateQuizResponse.setQuestions(quiz.getQuestions());
        ResultInfo resultInfo = new ResultInfo();
        evaluateQuizResponse.setResultInfo(resultInfo);
        return evaluateQuizResponse;
    }

    public GetLeaderBoardResponse getLeaderBoard(String quizId) {

        //getting users in desc order of scores and asc order of timeTaken from UserQuizMapping for quizId
        List<UserQuizMapping> userQuizMappings = null;
        try {
            userQuizMappings = userQuizMappingDao.getLeaderBoard(quizId);
        } catch (Exception e) {
            System.out.println("some error occurred while getting the leaderboard : " + e);
        }

        GetLeaderBoardResponse getLeaderBoardResponse = new GetLeaderBoardResponse();
        List<UserQuizDetails> leaderboard = new ArrayList<>();

        if(userQuizMappings == null) {
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setStatus("FAIL");
            resultInfo.setMessage("unable to get leaderboard!");
            getLeaderBoardResponse.setResultInfo(resultInfo);
            getLeaderBoardResponse.setLeaderBoard(leaderboard);
            return getLeaderBoardResponse;
        }

        for(UserQuizMapping userQuizMapping : userQuizMappings) {

            UserQuizDetails userQuizDetails = new UserQuizDetails();
            userQuizDetails.setQuizId(userQuizMapping.getQuizId());
            userQuizDetails.setUserId(userQuizMapping.getUserId());
            userQuizDetails.setScore(userQuizMapping.getScore());
            userQuizDetails.setTimeTaken(userQuizMapping.getTimeTaken());

            try {
                userQuizDetails.setUsername(userDao.findById(userQuizMapping.getUserId()).get().getUsername());
            } catch (Exception e) {
                System.out.println("some error occurred while getting username : " + e);
            }
            leaderboard.add(userQuizDetails);
        }
        getLeaderBoardResponse.setLeaderBoard(leaderboard);
        getLeaderBoardResponse.setResultInfo(new ResultInfo());
        return getLeaderBoardResponse;

    }

    private int getScoreForTheQuiz(EvaluateQuizRequest evaluateQuizRequest, Quiz quiz) {

        //calculating the score for the quiz
        Map<Long, String> correctResponses = new HashMap<>();
        for(Question question : quiz.getQuestions()) {
            correctResponses.put(question.getId(), question.getAnswer());
        }

        int score = 0;
        for(QuestionResponse questionResponse : evaluateQuizRequest.getQuestionResponses()) {
            String correctAnswer = correctResponses.get(questionResponse.getQuestionId());

            if(correctAnswer != null) {
                score += (correctAnswer.equalsIgnoreCase(questionResponse.getSelectedOption()) ? 1 : 0);
            } else if(correctResponses.containsKey(questionResponse.getQuestionId())) {
                score += 1;
            }
        }
        return  score;
    }

    private Long getUserId() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user.getUserId();
    }
}

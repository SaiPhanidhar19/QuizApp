package com.phani.quizApp.services;

import com.phani.quizApp.dao.QuestionDao;
import com.phani.quizApp.entity.Question;
import com.phani.quizApp.dtos.QuestionDetails;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuizAPIService quizAPIService;

    @Autowired
    private QuestionDao questionDao;

    private static final String url = "https://quizapi.io/api/v1/questions?apiKey=xMaFOkwskMeeENxfE29vH42ikGrJWaOyHpuB0Pz4";

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public List<Question> storeQuestions(String limit) {

        //getting questions from quiz API and storing in db
        if(limit == null) {
            limit = "50";
        }
        StringBuilder urlBuilder = new StringBuilder(url);
        urlBuilder.append("&limit=").append(limit);
        String questionsJson = quizAPIService.getQuizQuestions(urlBuilder.toString());

        System.out.println("quiz API response :: " + questionsJson);
        List<QuestionDetails> questionDetailsResponse = null;
        try {
            questionDetailsResponse  = objectMapper.readValue(questionsJson, new TypeReference<List<QuestionDetails>>() { });
        } catch (Exception e) {
            System.out.println("some error occurred while converting quiz API json questionsResponse to object : " + e.getMessage());
        }

        System.out.println("questionDetailsResponse :: " + questionDetailsResponse);

        List<Question> questionsSaved = null;
        if(questionDetailsResponse != null) {
            //creating questionList to store in db
            List<Question> questionList = new ArrayList<>();

            for(QuestionDetails questionDetails : questionDetailsResponse) {

                Question question = new Question();
                question.setQuestion(questionDetails.getQuestion());
                question.setCategory(questionDetails.getCategory());
                question.setDifficulty(questionDetails.getDifficulty());
                question.setAnswer(questionDetails.getCorrect_answer());
                question.setOptionA(questionDetails.getAnswers().getAnswer_a());
                question.setOptionB(questionDetails.getAnswers().getAnswer_b());
                question.setOptionC(questionDetails.getAnswers().getAnswer_c());
                question.setOptionD(questionDetails.getAnswers().getAnswer_d());

                questionList.add(question);
            }
            System.out.println("questionList :: " + questionList);
            questionsSaved = questionDao.saveAll(questionList);
        }
        return questionsSaved;
    }

}

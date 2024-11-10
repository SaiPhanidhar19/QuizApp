package com.phani.quizApp.services;

import com.phani.quizApp.dao.QuizDao;
import com.phani.quizApp.dao.UserDao;
import com.phani.quizApp.dao.UserQuizMappingDao;
import com.phani.quizApp.entity.Quiz;
import com.phani.quizApp.entity.User;
import com.phani.quizApp.entity.UserQuizMapping;
import com.phani.quizApp.dtos.*;
import com.phani.quizApp.security.JwtHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserQuizMappingDao userQuizMappingDao;

    @Autowired
    private QuizDao quizDao;


    public JwtResponse login(JwtRequest jwtRequest) {
        doAuthenticate(jwtRequest.getUsername(), jwtRequest.getPassword());

        UserDetails userDetails = userDetailsService.loadUserByUsername(jwtRequest.getUsername());
        String jwtToken = jwtHelper.generateToken(userDetails);

        JwtResponse response = JwtResponse.builder().jwtToken(jwtToken).username(userDetails.getUsername()).build();
        return response;
    }

    public UserSignUpResponse signUp(UserSignUpRequest userSignUpRequest) {

        if(userSignUpRequest == null
                || userSignUpRequest.getUsername() == null
                || userSignUpRequest.getPassword() == null
                || userSignUpRequest.getPasswordConfirmation() == null) {
            return handleError("Invalid signup request!");
        }

        if(!userSignUpRequest.getPassword().equalsIgnoreCase(userSignUpRequest.getPasswordConfirmation())) {
            return handleError("passwords doesn't match!");
        }

        if(userDao.findByUsername(userSignUpRequest.getUsername()).isPresent()) {
            return handleError("username already exists!");
        }

        User user = new User();
        user.setUsername(userSignUpRequest.getUsername());
        user.setPassword(passwordEncoder.encode(userSignUpRequest.getPassword()));

        User savedUser = null;
        try {
            savedUser = userDao.save(user);
        } catch (Exception e) {
            System.out.println("some error occurred while saving user : " + e);
            return handleError("Internal error occurred, Please try again later!");
        }

        UserSignUpResponse userSignUpResponse = new UserSignUpResponse();
        userSignUpResponse.setUserId(savedUser.getUserId());
        userSignUpResponse.setResultInfo(new ResultInfo());
        return userSignUpResponse;
    }

    public GetUserResponse getUser(String userId) {

        //getting user by userId
        User user = null;
        try {
            Optional<User> optionalUser = userDao.findById(Long.valueOf(userId));
            if(optionalUser.isPresent()) {
                user = optionalUser.get();
            }
        } catch (Exception e) {
            System.out.println("some error occurred while getting user : " + e);
        }

        GetUserResponse userResponse = new GetUserResponse();
        userResponse.setResultInfo(new ResultInfo());
        if(user == null) {
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setStatus("FAIL");
            resultInfo.setMessage("unable to get user!");
        }
        userResponse.setUserId(user == null ? null : user.getUserId());
        userResponse.setUsername(user == null ? null : user.getUsername());
        return userResponse;
    }

    public GetUserQuizesResponse getUserQuizesResponse(String userId) {

        //get all the quizzes attempted by user
        List<UserQuizMapping> userQuizMappingList = null;
        try {
            userQuizMappingList = userQuizMappingDao.getUserQuizMappingByUserId(userId);
        } catch (Exception e) {
            System.out.println("some error occurred while get UserQuizMappingList : " + e);
        }

        //checking if the userId is passed actually belongs to user
        if(!doesUserIdBelongsToUser(userId)) {
            GetUserQuizesResponse getUserQuizesResponse = new GetUserQuizesResponse();
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setStatus("FAIL");
            resultInfo.setMessage("cannot access details of another user!");
            getUserQuizesResponse.setResultInfo(resultInfo);
            return getUserQuizesResponse;
        }

        //handling null case
        GetUserQuizesResponse getUserQuizesResponse = new GetUserQuizesResponse();
        getUserQuizesResponse.setResultInfo(new ResultInfo());
        getUserQuizesResponse.setUserId(userId);
        if(userQuizMappingList == null) {
            getUserQuizesResponse.setQuizzes(null);
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setStatus("FAIL");
            resultInfo.setMessage("unable to get the quizzes given by user!");
            getUserQuizesResponse.setResultInfo(resultInfo);
            return getUserQuizesResponse;
        }

        Map<Long, Integer> quizIdScoreMapping = new HashMap<>();
        for(UserQuizMapping userQuizMapping : userQuizMappingList) {
            quizIdScoreMapping.put(userQuizMapping.getQuizId(), userQuizMapping.getScore());
        }

        //getting quiz details from quiz ids
        List<Long> quizIds = new ArrayList<>();
        for(UserQuizMapping userQuizMapping : userQuizMappingList) {
            quizIds.add(userQuizMapping.getQuizId());
        }

        //getting quizzes from quiz db
        List<Quiz> quizzes = null;
        try {
            quizzes = quizDao.findAllById(quizIds);
        } catch (Exception e) {
            System.out.println("some error occurred while getting the quiz details from the quiz db : " + e);
        }

        if(quizzes == null) {
            getUserQuizesResponse.setQuizzes(null);
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setStatus("FAIL");
            resultInfo.setMessage("unable to get the details now, please try again later!");
            getUserQuizesResponse.setResultInfo(resultInfo);
            return getUserQuizesResponse;
        }

        //setting score along with the quiz details in quizWrapper
        List<QuizWrapper> quizWrapperList = new ArrayList<>();
        for(Quiz quiz : quizzes) {
            QuizWrapper quizWrapper = new QuizWrapper(quiz);
            quizWrapper.setScore(quizIdScoreMapping.get(quiz.getId()));
            quizWrapperList.add(quizWrapper);
        }
        getUserQuizesResponse.setQuizzes(quizWrapperList);
        return getUserQuizesResponse;
    }

    private UserSignUpResponse handleError(String message) {

        UserSignUpResponse userSignUpResponse = new UserSignUpResponse();
        userSignUpResponse.setUserId(null);
        ResultInfo resultInfo = new ResultInfo();
        resultInfo.setMessage(message);
        resultInfo.setStatus("FAIL");
        userSignUpResponse.setResultInfo(resultInfo);
        return userSignUpResponse;
    }

    private boolean doesUserIdBelongsToUser(String userId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return String.valueOf(user.getUserId()).equals(userId);
    }

    private void doAuthenticate(String username, String password) {

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, password);
        try {
            manager.authenticate(authentication);
        } catch (BadCredentialsException e) {
            throw new RuntimeException("Invalid Username or Password!");
        }
    }
}

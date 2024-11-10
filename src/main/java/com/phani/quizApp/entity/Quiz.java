package com.phani.quizApp.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "quiz")
@Data
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String title;
    private String category;
    private String difficulty;
    private Long userId;
    @ManyToMany
    private List<Question> questions;
    @CreatedDate
    private Date createdDate;
}

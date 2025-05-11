package model;

import java.time.LocalDate;

public class Quiz {
    private String title;
    private String question;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctAnswer;
    private String documentName; // Add this field
    private LocalDate dueDate;   // Add this field

    // Constructor for quizzes with questions and options
    public Quiz(String title, String question, String optionA, String optionB, String optionC, String optionD, String correctAnswer) {
        this.title = title;
        this.question = question;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctAnswer = correctAnswer;
    }

    // Constructor for quizzes with a document
    public Quiz(String title, String documentName, LocalDate dueDate) {
        this.title = title;
        this.documentName = documentName;
        this.dueDate = dueDate;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getDocumentName() {
        return documentName;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public String getQuestion() {
        return question;
    }

    public String getOptionA() {
        return optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }
}
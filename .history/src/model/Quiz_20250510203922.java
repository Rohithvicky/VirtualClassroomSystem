package model;

public class Quiz {
  private String title;
  private String question;
  private String optionA;
  private String optionB;
  private String optionC;
  private String optionD;
  private String correctAnswer;

  public Quiz(String title, String question, String optionA, String optionB, String optionC, String optionD,
      String correctAnswer) {
    this.title = title;
    this.question = question;
    this.optionA = optionA;
    this.optionB = optionB;
    this.optionC = optionC;
    this.optionD = optionD;
    this.correctAnswer = correctAnswer;
  }

  public String getTitle() {
    return title;
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
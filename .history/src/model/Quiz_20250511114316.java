package model;

import java.time.LocalDate;

public class Quiz {
  private int id;
  private String title;
  private String documentName;
  private LocalDate dueDate;

  // Constructor that matches the one used in DataManager.java
  public Quiz(String title, String documentName, LocalDate dueDate) {
    this.title = title;
    this.documentName = documentName;
    this.dueDate = dueDate;
  }

  // Getters and setters
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDocumentName() {
    return documentName;
  }

  public void setDocumentName(String documentName) {
    this.documentName = documentName;
  }

  public LocalDate getDueDate() {
    return dueDate;
  }

  public void setDueDate(LocalDate dueDate) {
    this.dueDate = dueDate;
  }

  @Override
  public String toString() {
    return title + " (due: " + dueDate + ")";
  }
}
package model;

import java.time.LocalDate;

public class Quiz {
  private String title;
  private String documentName;
  private LocalDate dueDate;

  public Quiz(String title, String documentName, LocalDate dueDate) {
    this.title = title;
    this.documentName = documentName;
    this.dueDate = dueDate;
  }

  public String getTitle() {
    return title;
  }

  public String getDocumentName() {
    return documentName;
  }

  public LocalDate getDueDate() {
    return dueDate;
  }
}
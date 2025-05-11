package model;

public class Course {
    private int id;
    private String title;
    private String meetLink;

    public Course(int id, String title, String meetLink) {
        this.id = id;
        this.title = title;
        this.meetLink = meetLink;
    }
    
    public int getId() {
        return id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getMeetLink() {
        return meetLink;
    }
    
    // Method called by the UI code to display title
    public String getDisplayTitle() {
        return title;
    }
}
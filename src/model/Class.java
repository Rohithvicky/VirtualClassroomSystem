package model;

public class Class {
    private int id;
    private String title;
    private String meetLink;
    private int createdBy;

    public Class(int id, String title, String meetLink, int createdBy) {
        this.id = id;
        this.title = title;
        this.meetLink = meetLink;
        this.createdBy = createdBy;
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

    public int getCreatedBy() {
        return createdBy;
    }
}
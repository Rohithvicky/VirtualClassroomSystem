package model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

/**
 * Model class representing an Assignment in the Virtual Classroom System.
 */
public class Assignment {
    private int id;
    private String title;
    private String description;
    private String filePath;
    private int uploadedById;
    private String uploadedByName;
    private Timestamp uploadDate;

    // Default constructor
    public Assignment() {
        // Default constructor
    }

    // Overloaded constructor to match DataManager usage
    public Assignment(int id, String title, LocalDate dueDate) {
        this.id = id;
        this.title = title;
        // Convert dueDate to a Timestamp (using the start of the day)
        this.uploadDate = Timestamp.valueOf(dueDate.atStartOfDay());
    }

    // Getters and Setters
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getUploadedById() {
        return uploadedById;
    }

    public void setUploadedById(int uploadedById) {
        this.uploadedById = uploadedById;
    }

    public String getUploadedByName() {
        return uploadedByName;
    }

    public void setUploadedByName(String uploadedByName) {
        this.uploadedByName = uploadedByName;
    }

    public Timestamp getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Timestamp uploadDate) {
        this.uploadDate = uploadDate;
    }

    /**
     * Get a formatted date string for display.
     * 
     * @return formatted date string
     */
    public String getFormattedDate() {
        if (uploadDate == null) {
            return "Unknown";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm");
        return sdf.format(uploadDate);
    }

    @Override
    public String toString() {
        return title;
    }
}
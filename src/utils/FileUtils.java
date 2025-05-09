package utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileUtils {
    private static final String ASSIGNMENTS_DIRECTORY = "assignments";

    /**
     * Get the directory path for assignments.
     * Creates the directory if it doesn't exist.
     * 
     * @return Absolute path to the assignments directory.
     */
    public static String getAssignmentsDirectory() {
        File dir = new File(ASSIGNMENTS_DIRECTORY);
        if (!dir.exists()) {
            dir.mkdirs(); // Create the directory if it doesn't exist
        }
        return dir.getAbsolutePath();
    }

    /**
     * Copy a file from source to destination.
     * 
     * @param source      Source file.
     * @param destination Destination file.
     * @throws IOException If an I/O error occurs.
     */
    public static void copyFile(File source, File destination) throws IOException {
        Files.copy(source.toPath(), destination.toPath());
    }
}
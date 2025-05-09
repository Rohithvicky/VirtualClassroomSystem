package ui;

import db.DBConnection;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import model.Assignment;
import model.User;
import utils.FileUtils;
import utils.UIConstants;

/**
 * AssignmentPanel provides the UI for viewing, uploading, and managing assignments
 * in the Virtual Classroom System.
 */
public class AssignmentPanel extends JPanel {
    // Constants
    private static final String[] TABLE_COLUMNS = {"Title", "Description", "Uploaded By", "Date", "Status"};
    private static final int PADDING = 15;
    private static final int BUTTON_WIDTH = 150;
    private static final int BUTTON_HEIGHT = 30;
    
    // UI Components
    private DefaultTableModel tableModel;
    private JTable assignmentsTable;
    private JTextField searchField;
    private JButton uploadButton;
    private JButton viewButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private JComboBox<String> filterComboBox;
    private JButton searchButton;
    
    // Data
    private User currentUser;
    private List<Assignment> assignments;
    
    /**
     * Constructor for the AssignmentPanel.
     * 
     * @param user The currently logged-in user
     */
    public AssignmentPanel(User user) {
        this.currentUser = user;
        this.assignments = new ArrayList<>();
        
        setLayout(new BorderLayout(PADDING, PADDING));
        setBorder(new EmptyBorder(PADDING, PADDING, PADDING, PADDING));
        
        // Initialize and add components
        initializeComponents();
        setupLayout();
        
        // Load data
        loadAssignments();
    }
    
    /**
     * Initialize all UI components
     */
    private void initializeComponents() {
        // Table setup
        tableModel = new DefaultTableModel(TABLE_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };
        
        assignmentsTable = new JTable(tableModel);
        assignmentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        assignmentsTable.setRowHeight(25);
        assignmentsTable.getTableHeader().setReorderingAllowed(false);
        assignmentsTable.getSelectionModel().addListSelectionListener(this::handleTableSelection);
        
        // Search field
        searchField = new JTextField(20);
        searchField.setToolTipText("Search assignments");
        
        searchButton = new JButton("Search");
        searchButton.setFont(UIConstants.LABEL_FONT);
        searchButton.setBackground(UIConstants.PRIMARY_COLOR);
        searchButton.setForeground(Color.WHITE);
        searchButton.addActionListener(e -> performSearch());
        
        // Filter dropdown
        String[] filterOptions = {"All Assignments", "My Uploads", "Recent First", "Oldest First"};
        filterComboBox = new JComboBox<>(filterOptions);
        filterComboBox.addActionListener(e -> applyFilter());
        
        // Action buttons
        uploadButton = createButton("Upload Assignment", this::uploadAssignment);
        viewButton = createButton("View Details", this::viewAssignment);
        deleteButton = createButton("Delete", this::deleteAssignment);
        refreshButton = createButton("Refresh", e -> loadAssignments());
        
        // Initially disable buttons that need selection
        viewButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }
    
    /**
     * Helper method to create a styled button
     */
    private JButton createButton(String text, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        button.addActionListener(listener);
        return button;
    }
    
    /**
     * Set up the component layout using panels
     */
    private void setupLayout() {
        // Top panel for search and filter
        JPanel topPanel = new JPanel(new BorderLayout(PADDING, 0));
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterPanel.add(new JLabel("Filter:"));
        filterPanel.add(filterComboBox);
        
        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(filterPanel, BorderLayout.EAST);
        
        // Center panel with the assignments table
        JScrollPane tableScrollPane = new JScrollPane(assignmentsTable);
        tableScrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(PADDING, 0, PADDING, 0),
                BorderFactory.createLineBorder(Color.GRAY)
        ));
        
        // Bottom panel for action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, PADDING, 0));
        buttonPanel.add(uploadButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        
        // Add panels to main layout
        add(topPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Handle table row selection events
     */
    private void handleTableSelection(ListSelectionEvent event) {
        if (!event.getValueIsAdjusting()) {
            boolean hasSelection = assignmentsTable.getSelectedRow() != -1;
            viewButton.setEnabled(hasSelection);
            
            // Only enable delete for the user's own uploads
            if (hasSelection) {
                int selectedIndex = assignmentsTable.getSelectedRow();
                Assignment selected = assignments.get(selectedIndex);
                deleteButton.setEnabled(selected.getUploadedById() == currentUser.getId());
            } else {
                deleteButton.setEnabled(false);
            }
        }
    }
    
    /**
     * Upload a new assignment
     */
    private void uploadAssignment(ActionEvent e) {
        // Create a file chooser with file type filters
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Assignment File");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        // Show dialog and process selection
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            
            // Show input dialog for assignment title and description
            JTextField titleField = new JTextField(selectedFile.getName());
            JTextArea descriptionArea = new JTextArea(3, 30);
            descriptionArea.setLineWrap(true);
            descriptionArea.setWrapStyleWord(true);
            JScrollPane descScroll = new JScrollPane(descriptionArea);
            
            JPanel inputPanel = new JPanel(new BorderLayout(0, PADDING));
            inputPanel.add(new JLabel("Title:"), BorderLayout.NORTH);
            inputPanel.add(titleField, BorderLayout.CENTER);
            inputPanel.add(new JLabel("Description:"), BorderLayout.SOUTH);
            
            JPanel mainPanel = new JPanel(new BorderLayout(0, 5));
            mainPanel.add(inputPanel, BorderLayout.NORTH);
            mainPanel.add(descScroll, BorderLayout.CENTER);
            
            int dialogResult = JOptionPane.showConfirmDialog(
                    this,
                    mainPanel,
                    "Assignment Details",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );
            
            if (dialogResult == JOptionPane.OK_OPTION) {
                String title = titleField.getText().trim();
                String description = descriptionArea.getText().trim();
                
                if (title.isEmpty()) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Please enter a title for the assignment",
                            "Missing Information",
                            JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }
                
                // Save file to designated storage location
                String storagePath = saveAssignmentFile(selectedFile);
                if (storagePath != null) {
                    saveAssignmentToDatabase(title, description, storagePath);
                    loadAssignments(); // Refresh the list
                }
            }
        }
    }
    
    /**
     * Save the selected file to the designated storage location
     */
    private String saveAssignmentFile(File sourceFile) {
        try {
            // Create a timestamped filename to prevent conflicts
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = timestamp + "_" + sourceFile.getName();
            
            // Copy file to assignments directory
            String destinationPath = FileUtils.getAssignmentsDirectory() + File.separator + fileName;
            FileUtils.copyFile(sourceFile, new File(destinationPath));
            
            return destinationPath;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to save assignment file: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Save assignment metadata to the database
     */
    private void saveAssignmentToDatabase(String title, String description, String filePath) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO assignments (title, description, file_path, uploaded_by, upload_date) " +
                         "VALUES (?, ?, ?, ?, ?)";
                         
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, title);
                stmt.setString(2, description);
                stmt.setString(3, filePath);
                stmt.setInt(4, currentUser.getId());
                stmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Assignment uploaded successfully",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                } else {
                    throw new SQLException("Failed to insert assignment record");
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Database error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            ex.printStackTrace();
        }
    }
    
    /**
     * View the selected assignment details
     */
    private void viewAssignment(ActionEvent e) {
        int selectedRow = assignmentsTable.getSelectedRow();
        if (selectedRow >= 0) {
            Assignment assignment = assignments.get(selectedRow);
            showAssignmentDetailsDialog(assignment);
        }
    }
    
    /**
     * Show assignment details in a dialog
     */
    private void showAssignmentDetailsDialog(Assignment assignment) {
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        addDetailRow(detailsPanel, gbc, "Title:", assignment.getTitle());
        addDetailRow(detailsPanel, gbc, "Description:", assignment.getDescription());
        addDetailRow(detailsPanel, gbc, "Uploaded By:", assignment.getUploadedByName());
        addDetailRow(detailsPanel, gbc, "Date:", assignment.getFormattedDate());
        addDetailRow(detailsPanel, gbc, "File Path:", assignment.getFilePath());
        
        JButton openFileButton = new JButton("Open File");
        openFileButton.addActionListener(evt -> {
            try {
                Desktop.getDesktop().open(new File(assignment.getFilePath()));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Failed to open file: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
        
        gbc.gridy++;
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        detailsPanel.add(openFileButton, gbc);
        
        JOptionPane.showMessageDialog(
                this,
                detailsPanel,
                "Assignment Details",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Helper method to add a row of details in the details dialog
     */
    private void addDetailRow(JPanel panel, GridBagConstraints gbc, String label, String value) {
        gbc.gridx = 0;
        panel.add(new JLabel(label), gbc);
        
        gbc.gridx = 1;
        panel.add(new JLabel(value), gbc);
        
        gbc.gridy++;
    }
    
    /**
     * Delete the selected assignment
     */
    private void deleteAssignment(ActionEvent e) {
        int selectedRow = assignmentsTable.getSelectedRow();
        if (selectedRow >= 0) {
            Assignment assignment = assignments.get(selectedRow);
            
            // Confirm deletion
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete the assignment: " + assignment.getTitle() + "?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DBConnection.getConnection()) {
                    // Delete from database
                    String sql = "DELETE FROM assignments WHERE id = ?";
                    
                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setInt(1, assignment.getId());
                        int rowsAffected = stmt.executeUpdate();
                        
                        if (rowsAffected > 0) {
                            // Delete file from storage
                            File file = new File(assignment.getFilePath());
                            if (file.exists()) {
                                file.delete();
                            }
                            
                            JOptionPane.showMessageDialog(
                                    this,
                                    "Assignment deleted successfully",
                                    "Success",
                                    JOptionPane.INFORMATION_MESSAGE
                            );
                            
                            loadAssignments(); // Refresh the list
                        } else {
                            throw new SQLException("Failed to delete assignment record");
                        }
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Database error: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                    ex.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Perform search on assignments based on search text
     */
    private void performSearch() {
        String searchText = searchField.getText().trim().toLowerCase();
        
        if (searchText.isEmpty()) {
            loadAssignments(); // Reset to show all
            return;
        }
        
        tableModel.setRowCount(0);
        
        for (Assignment assignment : assignments) {
            if (assignment.getTitle().toLowerCase().contains(searchText) ||
                assignment.getDescription().toLowerCase().contains(searchText) ||
                assignment.getUploadedByName().toLowerCase().contains(searchText)) {
                
                addAssignmentToTable(assignment);
            }
        }
        
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "No assignments found matching '" + searchText + "'",
                    "Search Results",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
    
    /**
     * Apply filter based on selected option
     */
    private void applyFilter() {
        String selectedFilter = (String) filterComboBox.getSelectedItem();
        loadAssignments(selectedFilter);
    }
    
    /**
     * Load assignments from database with optional filter
     */
    private void loadAssignments() {
        String filter = (String) filterComboBox.getSelectedItem();
        loadAssignments(filter);
    }
    
    /**
     * Load assignments with specified filter
     */
    private void loadAssignments(String filter) {
        assignments.clear();
        tableModel.setRowCount(0);
        
        try (Connection conn = DBConnection.getConnection()) {
            StringBuilder sqlBuilder = new StringBuilder(
                "SELECT a.*, u.username FROM assignments a " +
                "LEFT JOIN users u ON a.uploaded_by = u.id"
            );
            
            // Apply filters
            if (filter != null) {
                if (filter.equals("My Uploads")) {
                    sqlBuilder.append(" WHERE a.uploaded_by = ?");
                }
                
                if (filter.equals("Recent First")) {
                    sqlBuilder.append(" ORDER BY a.upload_date DESC");
                } else if (filter.equals("Oldest First")) {
                    sqlBuilder.append(" ORDER BY a.upload_date ASC");
                } else {
                    // Default sort by most recent
                    sqlBuilder.append(" ORDER BY a.upload_date DESC");
                }
            }
            
            try (PreparedStatement stmt = conn.prepareStatement(sqlBuilder.toString())) {
                // Set parameters if needed
                if (filter != null && filter.equals("My Uploads")) {
                    stmt.setInt(1, currentUser.getId());
                }
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Assignment assignment = new Assignment();
                        assignment.setId(rs.getInt("id"));
                        assignment.setTitle(rs.getString("title"));
                        assignment.setDescription(rs.getString("description"));
                        assignment.setFilePath(rs.getString("file_path"));
                        assignment.setUploadedById(rs.getInt("uploaded_by"));
                        assignment.setUploadedByName(rs.getString("username"));
                        assignment.setUploadDate(rs.getTimestamp("upload_date"));
                        
                        assignments.add(assignment);
                        addAssignmentToTable(assignment);
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to load assignments: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
            ex.printStackTrace();
        }
    }
    
    /**
     * Add an assignment to the table model
     */
    private void addAssignmentToTable(Assignment assignment) {
        String status = assignment.getUploadedById() == currentUser.getId() ? "My Upload" : "";
        
        Object[] rowData = {
            assignment.getTitle(),
            abbreviateText(assignment.getDescription(), 40),
            assignment.getUploadedByName(),
            assignment.getFormattedDate(),
            status
        };
        
        tableModel.addRow(rowData);
    }
    
    /**
     * Abbreviate text if longer than maxLength
     */
    private String abbreviateText(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }
    
    /**
     * Handle file upload
     */
    private void handleFileUpload(File sourceFile, String fileName) {
        try {
            String destinationPath = FileUtils.getAssignmentsDirectory() + File.separator + fileName;
            FileUtils.copyFile(sourceFile, new File(destinationPath));
            JOptionPane.showMessageDialog(this, "File uploaded successfully to: " + destinationPath);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error uploading file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
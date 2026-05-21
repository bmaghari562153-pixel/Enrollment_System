package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class StudentsController implements Initializable {

   
    @FXML private TextField         searchField;
    @FXML private TextField         studentIdField;
    @FXML private TextField         firstNameField;
    @FXML private TextField         lastNameField;
    @FXML private TextField         programField;
    @FXML private ComboBox<Integer> yearLevelBox;
    @FXML private Label             statusLabel;

    @FXML private TableView<Student>            studentTable;
    @FXML private TableColumn<Student, String>  idCol;
    @FXML private TableColumn<Student, String>  firstNameCol;
    @FXML private TableColumn<Student, String>  lastNameCol;
    @FXML private TableColumn<Student, String>  programCol;
    @FXML private TableColumn<Student, Integer> yearCol;

    
    private final Datasyncservice dataSync   = Main.dataSync;
    private final Student_BST     studentBST = Main.studentBST;
    private final StudentDao      studentDao = new StudentDao();

  
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        yearLevelBox.setItems(FXCollections.observableArrayList(1, 2, 3, 4));
        yearLevelBox.getSelectionModel().selectFirst();

        idCol.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        programCol.setCellValueFactory(new PropertyValueFactory<>("program"));
        yearCol.setCellValueFactory(new PropertyValueFactory<>("yearLevel"));

        studentTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> { if (newVal != null) populateForm(newVal); }
        );

        loadAllStudents();
    }

   
    private void loadAllStudents() {
        List<Student> list = studentDao.getAllStudents();
        studentTable.setItems(FXCollections.observableArrayList(list));
    }

   
    private void populateForm(Student s) {
        studentIdField.setText(s.getStudentId());
        firstNameField.setText(s.getFirstName());
        lastNameField.setText(s.getLastName());
        programField.setText(s.getProgram());
        yearLevelBox.setValue(s.getYearLevel());
        statusLabel.setText("");
    }

    
    @FXML
    void searchStudent(ActionEvent event) {
        String query = searchField.getText().trim();
        if (query.isEmpty()) { loadAllStudents(); return; }

       
        Student found = studentBST.search(query);
        if (found != null) {
            studentTable.setItems(FXCollections.observableArrayList(found));
            return;
        }

        
        List<Student> results = studentDao.searchByLastName(query);
        if (results.isEmpty()) {
            setStatus("No students found for: " + query, false);
        } else {
            studentTable.setItems(FXCollections.observableArrayList(results));
        }
    }

    
    @FXML
    void saveStudent(ActionEvent event) {
        if (!validateForm()) return;

        Student s = new Student(
            studentIdField.getText().trim(),
            firstNameField.getText().trim(),
            lastNameField.getText().trim(),
            programField.getText().trim(),
            yearLevelBox.getValue()
        );

        Student existing = studentBST.search(s.getStudentId());
        boolean success;

        if (existing != null) {
            success = dataSync.updateStudent(s);
            setStatus(success ? "Student updated successfully." : "Update failed.", success);
        } else {
            success = dataSync.addStudent(s);
            setStatus(success ? "Student added successfully." : "Failed to add student. ID may already exist.", success);
        }

        if (success) {
            loadAllStudents();
            clearForm();
        }
    }

    
    @FXML
    void deleteStudent(ActionEvent event) {
        String id = studentIdField.getText().trim();
        if (id.isEmpty()) {
            setStatus("Select a student to delete.", false);
            return;
        }

        boolean success = dataSync.removeStudent(id);
        setStatus(success ? "Student deleted." : "Delete failed — student not found.", success);

        if (success) {
            loadAllStudents();
            clearForm();
        }
    }

    
    @FXML
    void clearForm(ActionEvent event) { clearForm(); }

    private void clearForm() {
        studentIdField.clear();
        firstNameField.clear();
        lastNameField.clear();
        programField.clear();
        yearLevelBox.getSelectionModel().selectFirst();
        searchField.clear();
        statusLabel.setText("");
        studentTable.getSelectionModel().clearSelection();
        loadAllStudents();
    }


    private boolean validateForm() {
        if (studentIdField.getText().trim().isEmpty()) {
            setStatus("Student ID is required.", false); return false;
        }
        if (firstNameField.getText().trim().isEmpty()) {
            setStatus("First name is required.", false); return false;
        }
        if (lastNameField.getText().trim().isEmpty()) {
            setStatus("Last name is required.", false); return false;
        }
        if (programField.getText().trim().isEmpty()) {
            setStatus("Program is required.", false); return false;
        }
        if (yearLevelBox.getValue() == null) {
            setStatus("Year level is required.", false); return false;
        }
        return true;
    }

    private void setStatus(String message, boolean success) {
        statusLabel.setText(message);
        statusLabel.setStyle(success
            ? "-fx-text-fill: #4ade80; -fx-font-size: 12px;"
            : "-fx-text-fill: #f87171; -fx-font-size: 12px;");
    }

    
    @FXML void showDashBoard(ActionEvent event)   { loadScene("DashBoard.fxml"); }
    @FXML void showStudents(ActionEvent event)    { loadScene("StudentView.fxml"); }
    @FXML void showCourses(ActionEvent event)     { loadScene("CourseView.fxml"); }
    @FXML void showEnrollments(ActionEvent event) { loadScene("EnrollmentView.fxml"); }
    @FXML void showSchedule(ActionEvent event)    { loadScene("ScheduleView.fxml"); }

   
    private void loadScene(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) studentTable.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
            stage.setScene(scene);
        } catch (Exception e) {
            System.err.println("[StudentsController] Could not load " + fxmlFile + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
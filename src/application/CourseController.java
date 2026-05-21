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
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CourseController implements Initializable {

   
    @FXML private TextField        searchField;
    @FXML private TextField        courseCodeField;
    @FXML private TextField        courseNameField;
    @FXML private TextField        unitsField;
    @FXML private ComboBox<String> dayComboBox;
    @FXML private TextField        startTimeField;
    @FXML private TextField        endTimeField;
    @FXML private ListView<String> prerequisitesList;  
    @FXML private Label            statusLabel;

    @FXML private TableView<Course>            courseTable;
    @FXML private TableColumn<Course, String>  codeCol;
    @FXML private TableColumn<Course, String>  nameCol;
    @FXML private TableColumn<Course, Integer> unitsCol;
    @FXML private TableColumn<Course, String>  dayCol;
    @FXML private TableColumn<Course, String>  scheduleCol;

   
    private final Datasyncservice dataSync  = Main.dataSync;
    private final CourseDao       courseDao = new CourseDao();

   
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        
        dayComboBox.setItems(FXCollections.observableArrayList(
            "Mon", "Tue", "Wed", "Thu", "Fri",
            "MWF", "TTh", "MTWThF", "Sat", "Sun"
        ));

       
        prerequisitesList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

       
        loadPrerequisiteOptions();

       
        codeCol.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        unitsCol.setCellValueFactory(new PropertyValueFactory<>("units"));
        dayCol.setCellValueFactory(new PropertyValueFactory<>("day"));
        scheduleCol.setCellValueFactory(new PropertyValueFactory<>("schedule"));

       
        courseTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> { if (newVal != null) populateForm(newVal); }
        );

        loadAllCourses();
    }

  
    private void loadPrerequisiteOptions() {
        List<Course> all = courseDao.getAllCourses();
        List<String> codes = new ArrayList<>();
        for (Course c : all) {
            codes.add(c.getCourseCode() + "  —  " + c.getCourseName());
        }
        prerequisitesList.setItems(FXCollections.observableArrayList(codes));
    }

   
    private void loadAllCourses() {
        courseTable.setItems(FXCollections.observableArrayList(courseDao.getAllCourses()));
        loadPrerequisiteOptions(); 
    }

   
    private void populateForm(Course c) {
        courseCodeField.setText(c.getCourseCode());
        courseNameField.setText(c.getCourseName());
        unitsField.setText(String.valueOf(c.getUnits()));
        dayComboBox.setValue(c.getDay());
        startTimeField.setText(c.getStartTime() != null ? c.getStartTime() : "");
        endTimeField.setText(c.getEndTime()     != null ? c.getEndTime()   : "");

       
        prerequisitesList.getSelectionModel().clearSelection();
        for (String prereqCode : c.getPrerequisites()) {
            for (int i = 0; i < prerequisitesList.getItems().size(); i++) {
                if (prerequisitesList.getItems().get(i).startsWith(prereqCode)) {
                    prerequisitesList.getSelectionModel().select(i);
                }
            }
        }

        statusLabel.setText("");
    }

   
    private List<String> getSelectedPrerequisites() { 
        List<String> selected = new ArrayList<>();
        for (String item : prerequisitesList.getSelectionModel().getSelectedItems()) {          
            String code = item.split("  —  ")[0].trim();
            selected.add(code);
        }
        return selected;
    }

   
    @FXML
    void searchCourse(ActionEvent event) {
        String query = searchField.getText().trim().toUpperCase();
        if (query.isEmpty()) { loadAllCourses(); return; }

        Course found = courseDao.getCourse(query);
        if (found != null) {
            courseTable.setItems(FXCollections.observableArrayList(found));
        } else {
            setStatus("Course not found: " + query, false);
        }
    }

    
    @FXML
    void saveCourse(ActionEvent event) {
        if (!validateForm()) return;

        List<String> prereqs = getSelectedPrerequisites();

        Course c = new Course(
            courseCodeField.getText().trim().toUpperCase(),
            courseNameField.getText().trim(),
            Integer.parseInt(unitsField.getText().trim()),
            dayComboBox.getValue(),
            startTimeField.getText().trim(),
            endTimeField.getText().trim(),
            prereqs
        );

        Course existing = courseDao.getCourse(c.getCourseCode());
        boolean success;

        if (existing != null) {
            success = dataSync.updateCourse(c);
            setStatus(success ? "Course updated successfully." : "Update failed.", success);
        } else {
            success = dataSync.addCourse(c);
            setStatus(success ? "Course added successfully." : "Failed to add course.", success);
        }

        if (success) { loadAllCourses(); clearForm(null); }
    }

   
    @FXML
    void deleteCourse(ActionEvent event) {
        String code = courseCodeField.getText().trim().toUpperCase();
        if (code.isEmpty()) { setStatus("Select a course to delete.", false); return; }

        boolean success = dataSync.removeCourse(code);
        setStatus(success ? "Course deleted." : "Delete failed.", success);
        if (success) { loadAllCourses(); clearForm(null); }
    }

 
    @FXML
    void clearForm(ActionEvent event) { clearForm(); }

    private void clearForm() {
        courseCodeField.clear();
        courseNameField.clear();
        unitsField.clear();
        dayComboBox.setValue(null);
        startTimeField.clear();
        endTimeField.clear();
        prerequisitesList.getSelectionModel().clearSelection();
        searchField.clear();
        statusLabel.setText("");
        courseTable.getSelectionModel().clearSelection();
        loadAllCourses();
    }

   
    private boolean validateForm() {
        if (courseCodeField.getText().trim().isEmpty()) {
            setStatus("Course code is required.", false); return false;
        }
        if (courseNameField.getText().trim().isEmpty()) {
            setStatus("Course name is required.", false); return false;
        }
        try {
            int u = Integer.parseInt(unitsField.getText().trim());
            if (u <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            setStatus("Units must be a positive number.", false); return false;
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
            Stage stage = (Stage) courseTable.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
            stage.setScene(scene);
        } catch (Exception e) {
            System.err.println("[CourseController] Could not load " + fxmlFile + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
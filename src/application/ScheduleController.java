package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ScheduleController implements Initializable {

   
    @FXML private TextField studentIdField;
    @FXML private Label     statusLabel;

    @FXML private HBox  studentBanner;
    @FXML private Label studentNameLabel;
    @FXML private Label studentProgramLabel;
    @FXML private Label studentYearLabel;

    @FXML private HBox  summaryBox;
    @FXML private Label totalCoursesLabel;
    @FXML private Label totalUnitsLabel;
    @FXML private Label conflictsLabel;

    @FXML private VBox                       scheduleCard;
    @FXML private TableView<Course>          scheduleTable;
    @FXML private TableColumn<Course, String>  codeCol;
    @FXML private TableColumn<Course, String>  nameCol;
    @FXML private TableColumn<Course, Integer> unitsCol;
    @FXML private TableColumn<Course, String>  dayCol;
    @FXML private TableColumn<Course, String>  scheduleCol;

   
    private final Scheduler_Service scheduler =
            new Scheduler_Service(Main.studentBST, Main.courseGraph);

   
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        codeCol.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        unitsCol.setCellValueFactory(new PropertyValueFactory<>("units"));
        dayCol.setCellValueFactory(new PropertyValueFactory<>("day"));
        scheduleCol.setCellValueFactory(new PropertyValueFactory<>("schedule"));
    }

   
    @FXML
    void generateSchedule(ActionEvent event) {
        String studentId = studentIdField.getText().trim();

        if (studentId.isEmpty()) {
            setStatus("Please enter a Student ID.", false);
            return;
        }

       
        Student student = Main.studentBST.search(studentId);
        if (student == null) {
            setStatus("Student not found: " + studentId, false);
            hideResults();
            return;
        }

       
        studentNameLabel.setText(student.getFullName());
        studentProgramLabel.setText(student.getProgram());
        studentYearLabel.setText(String.valueOf(student.getYearLevel()));
        show(studentBanner);

       
        List<Course> schedule = scheduler.generateSchedule(studentId);

        if (schedule.isEmpty()) {
            setStatus("No eligible courses found for this student.", false);
            hideResults();
            return;
        }

      
        ObservableList<Course> data = FXCollections.observableArrayList(schedule);
        scheduleTable.setItems(data);

       
        int totalUnits = schedule.stream().mapToInt(Course::getUnits).sum();
        totalCoursesLabel.setText(String.valueOf(schedule.size()));
        totalUnitsLabel.setText(String.valueOf(totalUnits));

      
        boolean hasConflicts = hasAnyConflict(schedule);
        if (hasConflicts) {
            conflictsLabel.setText("Conflicts detected");
            conflictsLabel.setStyle("-fx-text-fill: #f87171; -fx-font-size: 28px; -fx-font-weight: bold;");
        } else {
            conflictsLabel.setText("None detected");
            conflictsLabel.setStyle("-fx-text-fill: #4ade80; -fx-font-size: 28px; -fx-font-weight: bold;");
        }

        
        show(summaryBox);
        show(scheduleCard);

        setStatus("Schedule generated: " + schedule.size() + " courses, " + totalUnits + " units.", true);
    }

  
    @FXML
    void clearSchedule(ActionEvent event) {
        studentIdField.clear();
        statusLabel.setText("");
        scheduleTable.setItems(FXCollections.observableArrayList());
        hideResults();
    }

   

   
    private void show(javafx.scene.Node node) {
        node.setVisible(true);
        node.setManaged(true);
    }

    
    private void hideResults() {
        studentBanner.setVisible(false); studentBanner.setManaged(false);
        summaryBox.setVisible(false);    summaryBox.setManaged(false);
        scheduleCard.setVisible(false);  scheduleCard.setManaged(false);
    }

   
    private boolean hasAnyConflict(List<Course> schedule) {
        for (int i = 0; i < schedule.size(); i++) {
            for (int j = i + 1; j < schedule.size(); j++) {
                if (scheduler.hasConflict(schedule.get(i), schedule.get(j))) {
                    return true;
                }
            }
        }
        return false;
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
            Stage stage = (Stage) studentIdField.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
            stage.setScene(scene);
        } catch (Exception e) {
            System.err.println("[ScheduleController] Could not load " + fxmlFile + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
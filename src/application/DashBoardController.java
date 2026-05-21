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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DashBoardController implements Initializable {

  
    @FXML private TableColumn<Enrollment, String> courseCol;
    @FXML private Label   coursesCountLabel;
    @FXML private Label   enrolledCountLabel;
    @FXML private Label   pendingCountLabel;
    @FXML private TableView<Enrollment> recentTable;
    @FXML private TableColumn<Enrollment, String> statusCol;
    @FXML private TableColumn<Enrollment, String> studentCol;
    @FXML private Label   studentCountLabel;

   
    private final StudentDao    studentDao    = new StudentDao();
    private final CourseDao     courseDao     = new CourseDao();
    private final EnrollmentDao enrollmentDao = new EnrollmentDao();

   
    @Override
    public void initialize(URL url, ResourceBundle rb) {
     
        studentCol.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        courseCol.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

       
        loadDashboardData();
    }

   
    private void loadDashboardData() {
        
        int studentCount = studentDao.getAllStudents().size();
        int courseCount  = courseDao.getAllCourses().size();

        List<Enrollment> all = enrollmentDao.getAllEnrollments();
        long enrolled = all.stream().filter(Enrollment::isEnrolled).count();
        long pending  = all.stream().filter(Enrollment::isPending).count();

        studentCountLabel.setText(String.valueOf(studentCount));
        coursesCountLabel.setText(String.valueOf(courseCount));
        enrolledCountLabel.setText(String.valueOf(enrolled));
        pendingCountLabel.setText(String.valueOf(pending));

       
        int from = Math.max(0, all.size() - 10);
        ObservableList<Enrollment> data = FXCollections.observableArrayList(
                all.subList(from, all.size()));
        recentTable.setItems(data);
    }

  
    @FXML
    void showDashBoard(ActionEvent event) {
        loadDashboardData(); 
    }

    @FXML void showStudents(ActionEvent event)    { loadScene("StudentView.fxml"); }
    @FXML void showCourses(ActionEvent event)     { loadScene("CourseView.fxml"); }
    @FXML void showEnrollments(ActionEvent event) { loadScene("EnrollmentView.fxml"); }
    @FXML void showSchedule(ActionEvent event)    { loadScene("ScheduleView.fxml"); }

   
    private void loadScene(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            Stage stage = (Stage) recentTable.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
            stage.setScene(scene);

        } catch (Exception e) {
            System.err.println("[DashBoardController] Could not load " + fxmlFile + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
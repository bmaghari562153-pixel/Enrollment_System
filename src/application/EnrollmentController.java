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
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class EnrollmentController implements Initializable {

    // ── FXML bindings ─────────────────────────────────────────────────────────
    @FXML private TextField        searchField;
    @FXML private TextField        studentIdField;
    @FXML private TextField        courseCodeField;
    @FXML private ComboBox<String> statusComboBox;

    @FXML private HBox  validationBanner;
    @FXML private Label validationIcon;
    @FXML private Label validationMessage;
    @FXML private Label statusLabel;

    @FXML private TableView<EnrollmentRow>           enrollmentTable;
    @FXML private TableColumn<EnrollmentRow, String> colStudentId;
    @FXML private TableColumn<EnrollmentRow, String> colStudentName;
    @FXML private TableColumn<EnrollmentRow, String> colCourseCode;
    @FXML private TableColumn<EnrollmentRow, String> colCourseName;
    @FXML private TableColumn<EnrollmentRow, String> colStatus;

    // ── Shared instances ──────────────────────────────────────────────────────
    private final EnrollmentDao     enrollmentDao = new EnrollmentDao();
    private final StudentDao        studentDao    = new StudentDao();
    private final CourseDao         courseDao     = new CourseDao();
    private final Scheduler_Service scheduler     =
            new Scheduler_Service(Main.studentBST, Main.courseGraph);

    // ── Initialize ────────────────────────────────────────────────────────────
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // All five statuses available in the ComboBox
        statusComboBox.setItems(FXCollections.observableArrayList(
            Enrollment.STATUS_PENDING,
            Enrollment.STATUS_ENROLLED,
            Enrollment.STATUS_DROPPED,
            Enrollment.STATUS_FAILED,
            Enrollment.STATUS_COMPLETED
        ));
        statusComboBox.getSelectionModel().select(Enrollment.STATUS_PENDING);

        // Wire table columns
        colStudentId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colStudentName.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        colCourseCode.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        colCourseName.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Click row → populate form
        enrollmentTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> { if (newVal != null) populateForm(newVal); }
        );

        loadAll();
    }

    // ── Load all enrollment rows ──────────────────────────────────────────────
    private void loadAll() {
        enrollmentTable.setItems(buildRows(enrollmentDao.getAllEnrollments()));
    }

    // ── Build EnrollmentRow list from Enrollment list ─────────────────────────
    private ObservableList<EnrollmentRow> buildRows(List<Enrollment> enrollments) {
        ObservableList<EnrollmentRow> rows = FXCollections.observableArrayList();
        for (Enrollment e : enrollments) {
            String studentName = "";
            String courseName  = "";
            Student s = studentDao.getStudent(e.getStudentId());
            if (s != null) studentName = s.getFullName();
            Course c = courseDao.getCourse(e.getCourseCode());
            if (c != null) courseName = c.getCourseName();
            rows.add(new EnrollmentRow(
                e.getStudentId(), studentName,
                e.getCourseCode(), courseName,
                e.getStatus()
            ));
        }
        return rows;
    }

    // ── Populate form on row click ────────────────────────────────────────────
    private void populateForm(EnrollmentRow row) {
        studentIdField.setText(row.getStudentId());
        courseCodeField.setText(row.getCourseCode());
        statusComboBox.setValue(row.getStatus());
        resetValidationBanner();
        statusLabel.setText("");
    }

    // ── SEARCH ────────────────────────────────────────────────────────────────
    @FXML
    void searchEnrollment(ActionEvent event) {
        String query = searchField.getText().trim();
        if (query.isEmpty()) { loadAll(); return; }

        List<Enrollment> results = enrollmentDao.getEnrollmentsByStudent(query);
        if (results.isEmpty())
            results = enrollmentDao.getEnrollmentsByCourse(query.toUpperCase());

        if (results.isEmpty()) {
            setStatus("No records found for: " + query, false);
        } else {
            enrollmentTable.setItems(buildRows(results));
        }
    }

    // ── VALIDATE ──────────────────────────────────────────────────────────────
    @FXML
    void handleValidate(ActionEvent event) {
        String studentId  = studentIdField.getText().trim();
        String courseCode = courseCodeField.getText().trim().toUpperCase();

        if (studentId.isEmpty() || courseCode.isEmpty()) {
            setValidation("Enter both Student ID and Course code first.", false);
            return;
        }

        Student student = Main.studentBST.search(studentId);
        if (student == null) {
            setValidation("Student not found: " + studentId, false);
            return;
        }

        if (scheduler.isAlreadyEnrolled(studentId, courseCode)) {
            setValidation("Already enrolled in " + courseCode + ".", false);
            return;
        }

        Scheduler_Service.ValidationResult result =
                scheduler.validatePrerequisites(studentId, courseCode);
        setValidation(result.getMessage(), result.isPassed());
    }

    // ── ENROLL — uses selected status from ComboBox ───────────────────────────
    @FXML
    void handleEnroll(ActionEvent event) {
        String studentId  = studentIdField.getText().trim();
        String courseCode = courseCodeField.getText().trim().toUpperCase();
        String status     = statusComboBox.getValue();

        if (studentId.isEmpty() || courseCode.isEmpty()) {
            setStatus("Student ID and Course code are required.", false);
            return;
        }

        // Check if already has any record
        Enrollment existing = enrollmentDao.getEnrollment(studentId, courseCode);
        if (existing != null) {
            setStatus("Record already exists. Use Update Status to change it.", false);
            return;
        }

        // Validate prerequisites only if enrolling as ENROLLED or PENDING
        if (status.equals(Enrollment.STATUS_ENROLLED) || status.equals(Enrollment.STATUS_PENDING)) {
            Scheduler_Service.ValidationResult result =
                    scheduler.validatePrerequisites(studentId, courseCode);
            if (!result.isPassed()) {
                setStatus("Cannot enroll: " + result.getMessage(), false);
                setValidation(result.getMessage(), false);
                return;
            }
        }

        Enrollment e = new Enrollment(studentId, courseCode, status);
        boolean success = enrollmentDao.enrollStudent(e);
        setStatus(success
            ? "Enrolled successfully with status: " + status
            : "Enrollment failed.", success);

        if (success) { loadAll(); clearForm(); }
    }

    // ── UPDATE STATUS — changes status of existing record ────────────────────
    @FXML
    void handleUpdateStatus(ActionEvent event) {
        String studentId  = studentIdField.getText().trim();
        String courseCode = courseCodeField.getText().trim().toUpperCase();
        String newStatus  = statusComboBox.getValue();

        if (studentId.isEmpty() || courseCode.isEmpty()) {
            setStatus("Select an enrollment record first.", false);
            return;
        }

        if (newStatus == null) {
            setStatus("Select a status from the dropdown.", false);
            return;
        }

        boolean success = enrollmentDao.updateStatus(studentId, courseCode, newStatus);
        setStatus(success
            ? "Status updated to: " + newStatus
            : "Update failed — record not found.", success);

        if (success) loadAll();
    }

    // ── DROP — shortcut to set DROPPED ───────────────────────────────────────
    @FXML
    void handleDrop(ActionEvent event) {
        String studentId  = studentIdField.getText().trim();
        String courseCode = courseCodeField.getText().trim().toUpperCase();

        if (studentId.isEmpty() || courseCode.isEmpty()) {
            setStatus("Select an enrollment record to drop.", false);
            return;
        }

        boolean success = enrollmentDao.updateStatus(studentId, courseCode, Enrollment.STATUS_DROPPED);
        setStatus(success ? "Enrollment dropped." : "Drop failed.", success);
        if (success) { loadAll(); clearForm(); }
    }

    // ── CLEAR ─────────────────────────────────────────────────────────────────
    @FXML
    void clearForm(ActionEvent event) { clearForm(); }

    private void clearForm() {
        studentIdField.clear();
        courseCodeField.clear();
        searchField.clear();
        statusComboBox.getSelectionModel().select(Enrollment.STATUS_PENDING);
        statusLabel.setText("");
        resetValidationBanner();
        enrollmentTable.getSelectionModel().clearSelection();
        loadAll();
    }

    // ── HELPERS ───────────────────────────────────────────────────────────────
    private void setStatus(String message, boolean success) {
        statusLabel.setText(message);
        statusLabel.setStyle(success
            ? "-fx-text-fill: #4ade80; -fx-font-size: 12px;"
            : "-fx-text-fill: #f87171; -fx-font-size: 12px;");
    }

    private void setValidation(String message, boolean passed) {
        validationMessage.setText(message);
        if (passed) {
            validationIcon.setText("✓");
            validationIcon.setStyle("-fx-text-fill: #4ade80; -fx-font-size: 14px;");
            validationMessage.setStyle("-fx-text-fill: #4ade80; -fx-font-size: 12px;");
            validationBanner.setStyle("-fx-background-color: #14532d; -fx-background-radius: 8; -fx-padding: 10;");
        } else {
            validationIcon.setText("✗");
            validationIcon.setStyle("-fx-text-fill: #f87171; -fx-font-size: 14px;");
            validationMessage.setStyle("-fx-text-fill: #f87171; -fx-font-size: 12px;");
            validationBanner.setStyle("-fx-background-color: #7f1d1d; -fx-background-radius: 8; -fx-padding: 10;");
        }
    }

    private void resetValidationBanner() {
        validationIcon.setText("ℹ");
        validationIcon.setStyle("-fx-text-fill: #60a5fa; -fx-font-size: 14px;");
        validationMessage.setText("Enter a Student ID and Course code, then click Validate.");
        validationMessage.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px;");
        validationBanner.setStyle("-fx-background-color: #0f3460; -fx-background-radius: 8; -fx-padding: 10;");
    }

    // ── SIDEBAR NAVIGATION ────────────────────────────────────────────────────
    @FXML void showDashBoard(ActionEvent event)   { loadScene("DashBoard.fxml"); }
    @FXML void showStudents(ActionEvent event)    { loadScene("StudentView.fxml"); }
    @FXML void showCourses(ActionEvent event)     { loadScene("CourseView.fxml"); }
    @FXML void showEnrollments(ActionEvent event) { loadScene("EnrollmentView.fxml"); }
    @FXML void showSchedule(ActionEvent event)    { loadScene("ScheduleView.fxml"); }

    private void loadScene(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) enrollmentTable.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
            stage.setScene(scene);
        } catch (Exception e) {
            System.err.println("[EnrollmentController] Could not load " + fxmlFile + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ── EnrollmentRow inner class ─────────────────────────────────────────────
    public static class EnrollmentRow {
        private final String studentId;
        private final String studentName;
        private final String courseCode;
        private final String courseName;
        private final String status;

        public EnrollmentRow(String studentId, String studentName,
                             String courseCode, String courseName, String status) {
            this.studentId   = studentId;
            this.studentName = studentName;
            this.courseCode  = courseCode;
            this.courseName  = courseName;
            this.status      = status;
        }

        public String getStudentId()   { return studentId; }
        public String getStudentName() { return studentName; }
        public String getCourseCode()  { return courseCode; }
        public String getCourseName()  { return courseName; }
        public String getStatus()      { return status; }
    }
}
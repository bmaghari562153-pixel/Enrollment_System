module Enrollment_System_Final {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens application to javafx.fxml, javafx.base, javafx.graphics;
    exports application;
}
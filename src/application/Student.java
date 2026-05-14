package application;

import java.util.ArrayList;
import java.util.List;

public class Student {

    private String studentId;
    private String firstName;
    private String lastName;
    private String program;
    private int    yearLevel;
    private List<String> enrolledCourses;

    public Student() {
        this.enrolledCourses = new ArrayList<>();
    }

    public Student(String studentId, String firstName, String lastName,
                   String program, int yearLevel) {
        this.studentId       = studentId;
        this.firstName       = firstName;
        this.lastName        = lastName;
        this.program         = program;
        this.yearLevel       = yearLevel;
        this.enrolledCourses = new ArrayList<>();
    }

    public Student(String studentId, String firstName, String lastName,
                   String program, int yearLevel, List<String> enrolledCourses) {
        this(studentId, firstName, lastName, program, yearLevel);
        if (enrolledCourses != null) {
            this.enrolledCourses = enrolledCourses; 
        }
    }

    // Getters
    public String       getStudentId()       { return studentId; }
    public String       getFirstName()       { return firstName; }
    public String       getLastName()        { return lastName; }
    public String       getProgram()         { return program; }
    public int          getYearLevel()       { return yearLevel; }
    public List<String> getEnrolledCourses() { return enrolledCourses; }

    // Setters
    public void setStudentId(String studentId)           { this.studentId       = studentId; }
    public void setFirstName(String firstName)           { this.firstName       = firstName; }
    public void setLastName(String lastName)             { this.lastName        = lastName; }
    public void setProgram(String program)               { this.program         = program; }
    public void setYearLevel(int yearLevel)              { this.yearLevel       = yearLevel; }
    public void setEnrolledCourses(List<String> courses) { this.enrolledCourses = courses; }

    // Helpers
    public String getFullName() { return firstName + " " + lastName; }

    public void addEnrolledCourse(String courseCode) {
        if (courseCode != null && !enrolledCourses.contains(courseCode))
            enrolledCourses.add(courseCode);
    }

    public void removeEnrolledCourse(String courseCode) { enrolledCourses.remove(courseCode); }

    public boolean isEnrolledInCourse(String courseCode) { return enrolledCourses.contains(courseCode); }

    @Override
    public String toString() {
        return "Student [studentId=" + studentId + ", firstName=" + firstName
                + ", lastName=" + lastName + ", program=" + program
                + ", yearLevel=" + yearLevel + ", enrolledCourses=" + enrolledCourses + "]";
    }
}
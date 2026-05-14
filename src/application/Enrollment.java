package application;

public class Enrollment {

    
    public static final String STATUS_ENROLLED  = "ENROLLED";
    public static final String STATUS_PENDING   = "PENDING";   
    public static final String STATUS_DROPPED   = "DROPPED";
    public static final String STATUS_FAILED    = "FAILED";
    public static final String STATUS_COMPLETED = "COMPLETED";

    private String studentId;
    private String courseCode;
    private String status;

    public Enrollment() {
        this.status = STATUS_PENDING;
    }

    public Enrollment(String studentId, String courseCode, String status) {
        this.studentId  = studentId;
        this.courseCode = courseCode;
        this.status     = (status != null) ? status : STATUS_PENDING;
    }

    public Enrollment(String studentId, String courseCode) {
        this(studentId, courseCode, STATUS_PENDING);
    }

   
    public String getStudentId()  { return studentId; }
    public String getCourseCode() { return courseCode; }
    public String getStatus()     { return status; }

  
    public void setStudentId(String studentId)   { this.studentId  = studentId; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    public void setStatus(String status)         { this.status     = status; }

   
    public boolean isEnrolled()  { return STATUS_ENROLLED.equals(status); }
    public boolean isPending()   { return STATUS_PENDING.equals(status); }
    public boolean isDropped()   { return STATUS_DROPPED.equals(status); }
    public boolean isFailed()    { return STATUS_FAILED.equals(status); }
    public boolean isCompleted() { return STATUS_COMPLETED.equals(status); }

    @Override
    public String toString() {
        return "Enrollment{studentId='" + studentId + "', courseCode='"
                + courseCode + "', status='" + status + "'}";
    }
}
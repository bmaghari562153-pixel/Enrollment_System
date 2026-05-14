package application;

import java.util.ArrayList;
import java.util.List;

public class Course {

    private String courseCode;
    private String courseName;
    private int    units;
    private String day;
    private String startTime;
    private String endTime;
    private List<String> prerequisites;

    public Course() {
        this.prerequisites = new ArrayList<>();
    }

    public Course(String courseCode, String courseName, int units,
                  String day, String startTime, String endTime,
                  List<String> prerequisites) {
        this.courseCode    = courseCode;
        this.courseName    = courseName;
        this.units         = units;
        this.day           = day;
        this.startTime     = startTime;
        this.endTime       = endTime;
        this.prerequisites = prerequisites != null ? prerequisites : new ArrayList<>();
    }

    public Course(String courseCode, String courseName, int units,
                  String day, String startTime, String endTime) {
        this(courseCode, courseName, units, day, startTime, endTime, new ArrayList<>());
    }

    public Course(String courseCode, String courseName, int units,
                  List<String> prerequisites) {
        this(courseCode, courseName, units, null, null, null, prerequisites);
    }

    
    public String       getCourseCode()    { return courseCode; }
    public String       getCourseName()    { return courseName; }
    public int          getUnits()         { return units; }
    public String       getDay()           { return day; }
    public String       getStartTime()     { return startTime; }
    public String       getEndTime()       { return endTime; }
    public List<String> getPrerequisites() { return prerequisites; }

 
    public void setCourseCode(String courseCode)       { this.courseCode    = courseCode; }
    public void setCourseName(String courseName)       { this.courseName    = courseName; }
    public void setUnits(int units)                    { this.units         = units; }
    public void setDay(String day)                     { this.day           = day; }
    public void setStartTime(String startTime)         { this.startTime     = startTime; }
    public void setEndTime(String endTime)             { this.endTime       = endTime; }
    public void setPrerequisites(List<String> prereqs) { this.prerequisites = prereqs; }

   
    public void addPrerequisite(String courseCode) {
        if (courseCode != null && !prerequisites.contains(courseCode))
            prerequisites.add(courseCode);
    }

    public void removePrerequisite(String courseCode) { prerequisites.remove(courseCode); }

    public boolean hasPrerequisite(String courseCode) { return prerequisites.contains(courseCode); }

    public String getSchedule() {
        if (day == null || startTime == null || endTime == null) return "";
        return day + " " + startTime + "-" + endTime;
    }

    @Override
    public String toString() {
        return "Course{courseCode='" + courseCode + "', courseName='" + courseName
                + "', units=" + units + ", day='" + day + "', startTime='" + startTime
                + "', endTime='" + endTime + "', prerequisites=" + prerequisites + "}";
    }
}

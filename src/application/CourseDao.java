package application;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDao {

    private final Connection conn;

    public CourseDao() {
        this.conn = DBconnection.getInstance().getConnection();
    }

    public boolean insertCourse(Course c) {
        String sqlCourse = "INSERT INTO courses (course_code, course_name, units, day, start_time, end_time) "
                         + "VALUES (?, ?, ?, ?, ?, ?)";
        try {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sqlCourse)) {
                ps.setString(1, c.getCourseCode());
                ps.setString(2, c.getCourseName());
                ps.setInt   (3, c.getUnits());
                ps.setString(4, c.getDay());
                ps.setString(5, c.getStartTime());
                ps.setString(6, c.getEndTime());
                ps.executeUpdate();
            }

            for (String prereq : c.getPrerequisites()) {
                insertPrerequisite(c.getCourseCode(), prereq);
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("[CourseDao] insertCourse error: " + e.getMessage());
            try { conn.rollback(); } catch (SQLException ex) { }
            return false;
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) { }
        }
    }

    public boolean insertPrerequisite(String courseCode, String prereqCode) {
        String sql = "INSERT IGNORE INTO prerequisites (course_code, prereq_code) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, courseCode);
            ps.setString(2, prereqCode);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CourseDao] insertPrerequisite error: " + e.getMessage());
            return false;
        }
    }

    public boolean deletePrerequisite(String courseCode, String prereqCode) {
        String sql = "DELETE FROM prerequisites WHERE course_code = ? AND prereq_code = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, courseCode);
            ps.setString(2, prereqCode);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CourseDao] deletePrerequisite error: " + e.getMessage());
            return false;
        }
    }

    public Course getCourse(String courseCode) {
        String sql = "SELECT * FROM courses WHERE course_code = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, courseCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Course c = mapRow(rs);
                    c.setPrerequisites(getPrerequisitesFor(courseCode));
                    return c;
                }
            }
        } catch (SQLException e) {
            System.err.println("[CourseDao] getCourse error: " + e.getMessage());
        }
        return null;
    }

    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses ORDER BY course_code";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Course c = mapRow(rs);
                c.setPrerequisites(getPrerequisitesFor(c.getCourseCode()));
                courses.add(c);
            }
        } catch (SQLException e) {
            System.err.println("[CourseDao] getAllCourses error: " + e.getMessage());
        }
        return courses;
    }

    public List<String> getPrerequisitesFor(String courseCode) {
        List<String> prereqs = new ArrayList<>();
        String sql = "SELECT prereq_code FROM prerequisites WHERE course_code = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, courseCode);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) prereqs.add(rs.getString("prereq_code"));
            }
        } catch (SQLException e) {
            System.err.println("[CourseDao] getPrerequisitesFor error: " + e.getMessage());
        }
        return prereqs;
    }

    public boolean updateCourse(Course c) {
        String sql = "UPDATE courses "
                   + "SET course_name = ?, units = ?, day = ?, start_time = ?, end_time = ? "
                   + "WHERE course_code = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getCourseName());
            ps.setInt   (2, c.getUnits());
            ps.setString(3, c.getDay());
            ps.setString(4, c.getStartTime());
            ps.setString(5, c.getEndTime());
            ps.setString(6, c.getCourseCode());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CourseDao] updateCourse error: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteCourse(String courseCode) {
        String sql = "DELETE FROM courses WHERE course_code = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, courseCode);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CourseDao] deleteCourse error: " + e.getMessage());
            return false;
        }
    }

    private Course mapRow(ResultSet rs) throws SQLException {
        Time start = rs.getTime("start_time");
        Time end   = rs.getTime("end_time");
        return new Course(
            rs.getString("course_code"),
            rs.getString("course_name"),
            rs.getInt   ("units"),
            rs.getString("day"),
            start != null ? start.toString().substring(0, 5) : null,
            end   != null ? end.toString().substring(0, 5)   : null
        );
    }
}

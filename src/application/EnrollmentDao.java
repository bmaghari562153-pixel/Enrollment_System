package application;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDao {

    private final Connection conn;

    public EnrollmentDao() {
        this.conn = DBconnection.getInstance().getConnection();
    }

    public boolean enrollStudent(Enrollment e) {
        String sql = "INSERT INTO enrollments (student_id, course_code, status) "
                   + "VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, e.getStudentId());
            ps.setString(2, e.getCourseCode());
            ps.setString(3, e.getStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("[EnrollmentDao] enrollStudent error: " + ex.getMessage());
            return false;
        }
    }

    public List<Enrollment> getEnrollmentsByStudent(String studentId) {
        List<Enrollment> list = new ArrayList<>();
        String sql = "SELECT * FROM enrollments WHERE student_id = ? ORDER BY enrolled_at DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[EnrollmentDao] getEnrollmentsByStudent error: " + e.getMessage());
        }
        return list;
    }

    public List<Enrollment> getEnrollmentsByCourse(String courseCode) {
        List<Enrollment> list = new ArrayList<>();
        String sql = "SELECT * FROM enrollments WHERE course_code = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, courseCode);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[EnrollmentDao] getEnrollmentsByCourse error: " + e.getMessage());
        }
        return list;
    }

    public List<Enrollment> getAllEnrollments() {
        List<Enrollment> list = new ArrayList<>();
        String sql = "SELECT * FROM enrollments ORDER BY student_id, course_code";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[EnrollmentDao] getAllEnrollments error: " + e.getMessage());
        }
        return list;
    }

    
    public List<String> getCompletedCourseCodes(String studentId) {
        List<String> codes = new ArrayList<>();
        String sql = "SELECT course_code FROM enrollments "
                   + "WHERE student_id = ? AND status = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setString(2, Enrollment.STATUS_COMPLETED);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) codes.add(rs.getString("course_code"));
            }
        } catch (SQLException e) {
            System.err.println("[EnrollmentDao] getCompletedCourseCodes error: " + e.getMessage());
        }
        return codes;
    }

    public Enrollment getEnrollment(String studentId, String courseCode) {
        String sql = "SELECT * FROM enrollments WHERE student_id = ? AND course_code = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setString(2, courseCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("[EnrollmentDao] getEnrollment error: " + e.getMessage());
        }
        return null;
    }

    public boolean updateStatus(String studentId, String courseCode, String status) {
        String sql = "UPDATE enrollments SET status = ? "
                   + "WHERE student_id = ? AND course_code = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, studentId);
            ps.setString(3, courseCode);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[EnrollmentDao] updateStatus error: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteEnrollment(String studentId, String courseCode) {
        String sql = "DELETE FROM enrollments WHERE student_id = ? AND course_code = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setString(2, courseCode);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[EnrollmentDao] deleteEnrollment error: " + e.getMessage());
            return false;
        }
    }

    private Enrollment mapRow(ResultSet rs) throws SQLException {
        return new Enrollment(
            rs.getString("student_id"),
            rs.getString("course_code"),
            rs.getString("status")
        );
    }
}
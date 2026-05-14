package application;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDao {

    private final Connection conn;

    public StudentDao() {
        this.conn = DBconnection.getInstance().getConnection();
    }

    public boolean insertStudent(Student s) {
        String sql = "INSERT INTO students (student_id, first_name, last_name, program, year_level) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getStudentId());
            ps.setString(2, s.getFirstName());
            ps.setString(3, s.getLastName());
            ps.setString(4, s.getProgram());
            ps.setInt   (5, s.getYearLevel());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[StudentDao] insertStudent error: " + e.getMessage());
            return false;
        }
    }

    public Student getStudent(String studentId) {
        String sql = "SELECT * FROM students WHERE student_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("[StudentDao] getStudent error: " + e.getMessage());
        }
        return null;
    }

    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY last_name, first_name";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) students.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[StudentDao] getAllStudents error: " + e.getMessage());
        }
        return students;
    }

    public boolean updateStudent(Student s) {
        String sql = "UPDATE students "
                   + "SET first_name = ?, last_name = ?, program = ?, year_level = ? "
                   + "WHERE student_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getFirstName());
            ps.setString(2, s.getLastName());
            ps.setString(3, s.getProgram());
            ps.setInt   (4, s.getYearLevel());
            ps.setString(5, s.getStudentId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[StudentDao] updateStudent error: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteStudent(String studentId) {
        String sql = "DELETE FROM students WHERE student_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[StudentDao] deleteStudent error: " + e.getMessage());
            return false;
        }
    }

    public List<Student> searchByLastName(String lastName) {
        List<Student> results = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE last_name LIKE ? ORDER BY last_name, first_name";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + lastName + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) results.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[StudentDao] searchByLastName error: " + e.getMessage());
        }
        return results;
    }

    private Student mapRow(ResultSet rs) throws SQLException {
        return new Student(
            rs.getString("student_id"),
            rs.getString("first_name"),
            rs.getString("last_name"),
            rs.getString("program"),
            rs.getInt   ("year_level")
        );
    }
}
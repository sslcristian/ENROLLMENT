package data;

import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import model.Course;
import model.Enrollment;
import model.Student;

public class EnrollmentDAO {
    private Connection connection;

    public EnrollmentDAO(Connection connection) {
        this.connection = connection;
    }

    public void save(Enrollment enrollment) {
        String query = "INSERT INTO Enrollment (student_id, course_code, enrollment_date) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, enrollment.getStudentId());
            pstmt.setString(2, enrollment.getCourseCode());
            java.sql.Date sqlDate = java.sql.Date.valueOf(enrollment.getEnrollmentDate());
            pstmt.setDate(3, sqlDate);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("ENrollment inserted successfully.");
            } else {
                System.out.println("No rows affected, enrollment was not inserted.");
            }
        } catch (SQLException e) {
            System.err.println("Error inserting enrollment: " + e.getMessage());
            e.printStackTrace();  
        }
    }

    public ArrayList<Enrollment> fetch() {
	    ArrayList<Enrollment> enrollments = new ArrayList<>();
	    String query = "SELECT student_id, course_code, enrollment_date FROM Enrollment";
	    
	    try (Statement stmt = connection.createStatement();
	         ResultSet rs = stmt.executeQuery(query)) {
	        
	        while (rs.next()) {
	        	String studentId = rs.getString("student_id");
            	String courseCode = rs.getString("course_code");
            	LocalDate enrollmentDate = rs.getDate("enrollment_date").toLocalDate();
                Enrollment enrollment = new Enrollment(studentId, courseCode, enrollmentDate);
                enrollments.add(enrollment);
	        }
	    } catch (SQLException e) {
	        System.err.println("Error fetching enrollments: " + e.getMessage());
	        e.printStackTrace();  
	    }

	    return enrollments;
	}


    public void update(Enrollment enrollment) {
        String sql = "UPDATE Enrollment SET student_id=?, course_code=?, enrollment_date=? WHERE student_id=? AND course_code=?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
           
            stmt.setString(1, enrollment.getStudentId());
            stmt.setString(2, enrollment.getCourseCode());
            stmt.setDate(3, Date.valueOf(enrollment.getEnrollmentDate()));

          
            stmt.setString(4, enrollment.getStudentId());
            stmt.setString(5, enrollment.getCourseCode());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Enrollment updated successfully.");
            } else {
                System.out.println("No enrollment found for update.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void delete(String studentId, String courseCode) {
        String sql = "DELETE FROM Enrollment WHERE student_id=? AND course_code=?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            stmt.setString(2, courseCode);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Enrollment deleted successfully.");
            } else {
                System.out.println("No enrollment found with the given studentId and courseCode.");
            }
        } catch (SQLException e) {
            System.err.println("Error while deleting enrollment: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public ArrayList<Course> getCoursesByStudent(String studentId) {
        ArrayList<Course> courses = new ArrayList<>();
        String query = "SELECT c.code, c.name, c.credits " +
                       "FROM Course c " +
                       "JOIN Enrollment e ON c.code = e.course_code " +
                       "WHERE e.student_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, studentId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String code = rs.getString("code");
                String name = rs.getString("name");
                int credits = rs.getInt("credits");
                Course course = new Course(code, name, credits);
                courses.add(course);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return courses;
    }

    public ArrayList<Student> getStudentsByCourse(String courseCode) {
        ArrayList<Student> students = new ArrayList<>();
        String query = "SELECT s.id, s.name, s.email " +
                       "FROM Student s " +
                       "JOIN Enrollment e ON s.id = e.student_id " +
                       "WHERE e.course_code = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, courseCode);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                Student student = new Student(id, name, email);
                students.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return students;
    }

    public boolean authenticate(String studentId, String courseCode) {
        String sql = "SELECT COUNT(*) FROM Enrollment WHERE student_id=? AND course_code=?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            stmt.setString(2, courseCode);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    if (count > 0) {
                        System.out.println("The student is already enrolled in this course.");
                        return false;  
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return true; 
    }
    public boolean studentExists(String studentId) {
        String sql = "SELECT COUNT(*) FROM Student WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean courseExists(String courseCode) {
        String sql = "SELECT COUNT(*) FROM Course WHERE code = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, courseCode);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
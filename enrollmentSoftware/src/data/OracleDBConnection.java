package data;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

import model.Course;
import model.Enrollment;
import model.Student;

public class OracleDBConnection {
	private final String username="ajerez";
	private final String password="4j3r3z";
	private final String host = "192.168.254.215";
	private final String port = "1521";
	private final String service = "orcl";


    
    // Method to fetch courses from the database
    public ArrayList<Course> fetchCourses() throws SQLException {
        ArrayList<Course> courses = new ArrayList<>();
        String query = "SELECT code, name, credits  FROM Course";
        
        try (Connection conn = DriverManager.getConnection(getConnectionString(), username, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                String code = rs.getString("code");
                String name = rs.getString("name");
                int credits = rs.getInt("credits");
                Course course = new Course(code,name,credits);
                courses.add(course);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error fetching courses from the database.");
        }

        return courses;
    }
    
 // Method to fetch enrollments from the database
    public ArrayList<Enrollment> fetchEnrollments() throws SQLException {
        ArrayList<Enrollment> enrollments = new ArrayList<>();
        String query = "SELECT student_id, course_code, enrollment_date FROM Enrollment";
        
        try (Connection conn = DriverManager.getConnection(getConnectionString(), username, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
            	String studentId = rs.getString("student_id");
            	String courseCode = rs.getString("course_code");
            	LocalDate enrollmentDate = rs.getDate("enrollment_date").toLocalDate();
                Enrollment enrollment = new Enrollment(studentId, courseCode, enrollmentDate);
                enrollments.add(enrollment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error fetching enrollments from the database.");
        }

        return enrollments;
    }
    
    // Method to fetch students from the database
    public ArrayList<Student> fetchStudents() throws SQLException {
        ArrayList<Student> students = new ArrayList<>();
        String query = "SELECT id, name, email FROM Student";       
        try (Connection conn = DriverManager.getConnection(getConnectionString(), username, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
            	String id = rs.getString("id");
            	String name = rs.getString("name");
            	String email = rs.getString("email");
                Student student = new Student(id, name,email);
                students.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error fetching students from the database.");
        }

        return students;
    }
    
    // Method to insert a new book into the database
    public void insertCourse(Course course) throws SQLException {
        String query = "INSERT INTO Course (code, name, credits) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(getConnectionString(), username, password);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
        	

            pstmt.setString(1, course.getCode());
            pstmt.setString(2, course.getName());
            pstmt.setInt(3, course.getCredits());
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Course inserted successfully.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error inserting Course into the database.");
        }
    }
    
    public void insertEnrollment(Enrollment enrollment) throws SQLException {
        String query = "INSERT INTO Enrollment (student_id, course_code, enrollment_date) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(getConnectionString(), username, password);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
        	

            pstmt.setString(1, enrollment.getStudentId());
            pstmt.setString(2, enrollment.getCourseCode());
            java.sql.Date sqlDate = java.sql.Date.valueOf(enrollment.getEnrollmentDate());
            pstmt.setDate(3, sqlDate);

            

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Enrollment inserted successfully.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error inserting Enrollment into the database.");
        }
    }
 // Method to insert a new Student into the database
    public void insertStudent(Student student) throws SQLException {
        String query = "INSERT INTO Student (id, name,email) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(getConnectionString(), username, password);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, student.getId());
            pstmt.setString(2, student.getName());
            pstmt.setString(3, student.getEmail());
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Student inserted successfully.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error inserting Student into the database.");
        }
    }
    
	public String getConnectionString() {
		return String.format("jdbc:oracle:thin:@%s:%s:%s", this.host, this.port, this.service);
	}
	
	
}



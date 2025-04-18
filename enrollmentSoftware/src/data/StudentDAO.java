package data;

import java.sql.Connection;
import java.util.ArrayList;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import model.Student;

public class StudentDAO implements CRUD_Operation<Student,String>{
	
    private Connection connection;

    public StudentDAO(Connection connection) {
        this.connection = connection;
    }
    @Override
    public void save(Student student) {
        String query = "INSERT INTO Student (id, name, email) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, student.getId());
            pstmt.setString(2, student.getName());
            pstmt.setString(3, student.getEmail());
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Student inserted successfully.");
            } else {
                System.out.println("No rows affected, student was not inserted.");
            }
        } catch (SQLException e) {
            System.err.println("Error inserting student: " + e.getMessage());
            e.printStackTrace();  
        }
    }


	@Override
	public ArrayList<Student> fetch() {
	    ArrayList<Student> students = new ArrayList<>();
	    String query = "SELECT id, name, email FROM Student";
	    
	    try (Statement stmt = connection.createStatement();
	         ResultSet rs = stmt.executeQuery(query)) {
	        
	        while (rs.next()) {
	            String id = rs.getString("id");
	            String name = rs.getString("name");
	            String email = rs.getString("email");
	            Student student = new Student(id, name, email);
	            students.add(student);
	        }
	    } catch (SQLException e) {
	        System.err.println("Error fetching students: " + e.getMessage());
	        e.printStackTrace();  
	    }

	    return students;
	}

	@Override
	public void update(Student student) {
	    String sql = "UPDATE Student SET name=?, email=? WHERE id=?";
	    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
	        stmt.setString(1, student.getName());
	        stmt.setString(2, student.getEmail());
	        stmt.setString(3, student.getId());
	        int rowsAffected = stmt.executeUpdate();
	        if (rowsAffected > 0) {
	            System.out.println("Student updated successfully.");
	        } else {
	            System.out.println("No student found with ID: " + student.getId());
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	@Override
	public void delete(String id) {
	    String sql = "DELETE FROM Student WHERE id=?";
	    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
	        stmt.setString(1, id);
	        int rowsAffected = stmt.executeUpdate();
	        if (rowsAffected > 0) {
	            System.out.println("Student with code " + id + " has been deleted successfully.");
	        } else {
	            System.out.println("No Studen found with the code: " + id);
	        }
	    } catch (SQLException e) {
	        System.err.println("Error while deleting Student with code " + id);
	        e.printStackTrace();
	    }
	}
	@Override
	public boolean authenticate(String id) {
		String sql = "SELECT id FROM STUDENT WHERE id=?";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
		stmt.setString(1,  id);
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
		return rs.getString("id").equals(id);
		}
		} catch (SQLException e) {
		e.printStackTrace();}
		return false;}



	}

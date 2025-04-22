package data;

import java.sql.Connection;
import java.util.ArrayList;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import model.Course;;

public class CourseDAO implements CRUD_Operation<Course,String> {
    private Connection connection;

    public CourseDAO(Connection connection) {
        this.connection = connection;
    }
	@Override
	public void save(Course  course) {
		String query = "INSERT INTO Course (code, name, credits) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
        	

            pstmt.setString(1, course.getCode());
            pstmt.setString(2, course.getName());
            pstmt.setInt(3, course.getCredits());
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Course inserted successfully.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
		
	}

	public boolean exists(String code) {
	    String query = "SELECT COUNT(*) FROM course WHERE code = ?";
	    try (PreparedStatement stmt = connection.prepareStatement(query)) {
	        stmt.setString(1, code);
	        ResultSet rs = stmt.executeQuery();
	        if (rs.next()) {
	            return rs.getInt(1) > 0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}

	@Override
	public ArrayList<Course> fetch() {
        ArrayList<Course> courses = new ArrayList<>();
        String query = "SELECT code, name, credits FROM Course";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                String code = rs.getString("code");
                String name = rs.getString("name");
                 int credits = rs.getInt("credits");
                Course course = new Course(code, name,credits);
                courses.add(course);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return courses;
    }
	

	@Override
	public void update(Course course) {
		String sql = "UPDATE Course SET name=?, credits=? WHERE code=?";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
		stmt.setString(1, course.getName());
		stmt.setInt(2, course.getCredits());
		stmt.setString(3, course.getCode());
		stmt.executeUpdate();
		} catch (SQLException e) {
		e.printStackTrace();}
		
}

	
	@Override
	public void delete(String code) {
	    String sql = "DELETE FROM Course WHERE code=?";
	    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
	        stmt.setString(1, code);
	        int rowsAffected = stmt.executeUpdate();
	        if (rowsAffected > 0) {
	            System.out.println("Course with code " + code + " has been deleted successfully.");
	        } else {
	            System.out.println("No course found with the code: " + code);
	        }
	    } catch (SQLException e) {
	        System.err.println("Error while deleting course with code " + code);
	        e.printStackTrace();
	    }
	}
	@Override
	public boolean authenticate(String code) {
		String sql = "SELECT code FROM Course WHERE code=?";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
		stmt.setString(1,  code);
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
		return rs.getString("code").equals(code);
		}
		} catch (SQLException e) {
		e.printStackTrace();}
		return false;}



	}




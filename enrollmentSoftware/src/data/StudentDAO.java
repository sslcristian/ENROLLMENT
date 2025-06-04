package data;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.ArrayList;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import model.Student;

public class StudentDAO implements CRUD_Operation<Student, String> {
    
    private Connection connection;

    // Constructor que recibe la conexión a la base de datos
    public StudentDAO(Connection connection) {
        this.connection = connection;
    }

    // Implementación del método save para guardar un nuevo estudiante en la base de datos
    public void save(Student student) {
        String sql = "{ call insert_student(?, ?, ?) }";
        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setString(1, student.getId());
            stmt.setString(2, student.getName());
            stmt.setString(3, student.getEmail());
            stmt.execute();
            System.out.println("Student inserted successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Llamar procedimiento para actualizar
    public void update(Student student) {
        String sql = "{ call update_student(?, ?, ?) }";
        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setString(1, student.getId());
            stmt.setString(2, student.getName());
            stmt.setString(3, student.getEmail());
            stmt.execute();
            System.out.println("Student updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Llamar procedimiento para eliminar
    public void delete(String id) {
        String sql = "{ call delete_student(?) }";
        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setString(1, id);
            stmt.execute();
            System.out.println("Student deleted successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Llamar función para autenticar
    public boolean authenticate(String id) {
        String sql = "{ ? = call authenticate_student(?) }";
        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setString(2, id);
            stmt.execute();
            int exists = stmt.getInt(1);
            return exists > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

	@Override
	public ArrayList<Student> fetch() {
		// TODO Auto-generated method stub
		return null;
	}
}

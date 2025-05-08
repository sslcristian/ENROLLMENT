package data;

import java.sql.Connection;
import java.util.ArrayList;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import model.Student;

public class StudentDAO implements CRUD_Operation<Student, String> {
    
    private Connection connection;

    // Constructor que recibe la conexión a la base de datos
    public StudentDAO(Connection connection) {
        this.connection = connection;
    }

    // Implementación del método save para guardar un nuevo estudiante en la base de datos
    @Override
    public void save(Student student) {
        String query = "INSERT INTO Student (id, name, email) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            // Establece los valores de los parámetros para la consulta SQL
            pstmt.setString(1, student.getId());
            pstmt.setString(2, student.getName());
            pstmt.setString(3, student.getEmail());
            
            // Ejecuta la consulta SQL e imprime un mensaje según el resultado
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Student inserted successfully.");
            } else {
                System.out.println("No rows affected, student was not inserted.");
            }
        } catch (SQLException e) {
            System.err.println("Error inserting student: " + e.getMessage());
            e.printStackTrace();  // Imprime el error si ocurre una excepción
        }
    }

    // Implementación del método fetch para obtener todos los estudiantes de la base de datos
    @Override
    public ArrayList<Student> fetch() {
        ArrayList<Student> students = new ArrayList<>();
        String query = "SELECT id, name, email FROM Student";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            // Itera sobre los resultados y crea un objeto Student para cada registro
            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                Student student = new Student(id, name, email);
                students.add(student);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching students: " + e.getMessage());
            e.printStackTrace();  // Imprime el error si ocurre una excepción
        }

        return students;  // Retorna la lista de estudiantes
    }

    // Implementación del método update para actualizar la información de un estudiante
    @Override
    public void update(Student student) {
        String sql = "UPDATE Student SET name=?, email=? WHERE id=?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            // Establece los valores de los parámetros para la consulta SQL
            stmt.setString(1, student.getName());
            stmt.setString(2, student.getEmail());
            stmt.setString(3, student.getId());
            
            // Ejecuta la actualización e imprime un mensaje según el resultado
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Student updated successfully.");
            } else {
                System.out.println("No student found with ID: " + student.getId());
            }
        } catch (SQLException e) {
            e.printStackTrace();  // Imprime el error si ocurre una excepción
        }
    }

    // Implementación del método delete para eliminar un estudiante de la base de datos por su ID
    @Override
    public void delete(String id) {
        String sql = "DELETE FROM Student WHERE id=?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);  // Establece el valor del parámetro ID
            
            // Ejecuta la eliminación e imprime un mensaje según el resultado
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Student with code " + id + " has been deleted successfully.");
            } else {
                System.out.println("No student found with the code: " + id);
            }
        } catch (SQLException e) {
            System.err.println("Error while deleting student with code " + id);
            e.printStackTrace();  // Imprime el error si ocurre una excepción
        }
    }

    // Implementación del método authenticate para verificar si un estudiante existe por su ID
    @Override
    public boolean authenticate(String id) {
        String sql = "SELECT id FROM STUDENT WHERE id=?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);  // Establece el valor del parámetro ID
            ResultSet rs = stmt.executeQuery();
            
            // Si el resultado contiene el ID, significa que el estudiante existe
            if (rs.next()) {
                return rs.getString("id").equals(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();  // Imprime el error si ocurre una excepción
        }
        
        return false;  // Si no se encuentra el ID, retorna false
    }
}

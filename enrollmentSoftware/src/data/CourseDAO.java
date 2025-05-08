package data;

import java.sql.Connection;
import java.util.ArrayList;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import model.Course; // Importa la clase Course, que es el modelo de datos

// Esta clase maneja todas las operaciones CRUD (Crear, Leer, Actualizar, Eliminar) para la tabla Course.
public class CourseDAO implements CRUD_Operation<Course, String> {
    
    private Connection connection; // Conexión a la base de datos

    // Constructor que recibe la conexión a la base de datos
    public CourseDAO(Connection connection) {
        this.connection = connection;
    }

    // Método para guardar un nuevo curso en la base de datos (CREATE)
    @Override
    public void save(Course course) {
        String query = "INSERT INTO Course (code, name, credits) VALUES (?, ?, ?)"; // Consulta SQL para insertar un nuevo curso

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            // Establece los valores de los parámetros para la consulta SQL
            pstmt.setString(1, course.getCode());  // Código del curso
            pstmt.setString(2, course.getName());  // Nombre del curso
            pstmt.setInt(3, course.getCredits());  // Créditos del curso

            // Ejecuta la actualización en la base de datos
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Course inserted successfully."); // Imprime un mensaje si la inserción fue exitosa
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Manejo de errores de SQL
        }
    }

    // Método para verificar si un curso ya existe en la base de datos (se basa en el código del curso)
    public boolean exists(String code) {
        String query = "SELECT COUNT(*) FROM course WHERE code = ?"; // Consulta SQL para contar cuántos registros con el mismo código existen

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, code); // Establece el valor del código
            ResultSet rs = stmt.executeQuery(); // Ejecuta la consulta
            if (rs.next()) {
                return rs.getInt(1) > 0; // Si el contador es mayor que 0, el curso ya existe
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Manejo de errores de SQL
        }
        return false; // Si no hay coincidencias, devuelve false
    }

    // Método para obtener todos los cursos (READ)
    @Override
    public ArrayList<Course> fetch() {
        ArrayList<Course> courses = new ArrayList<>(); // Lista para almacenar los cursos
        String query = "SELECT code, name, credits FROM Course"; // Consulta SQL para seleccionar todos los cursos

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) { // Ejecuta la consulta

            // Recorre todos los resultados y los agrega a la lista de cursos
            while (rs.next()) {
                String code = rs.getString("code"); // Obtiene el código del curso
                String name = rs.getString("name"); // Obtiene el nombre del curso
                int credits = rs.getInt("credits"); // Obtiene los créditos del curso
                Course course = new Course(code, name, credits); // Crea un objeto Course
                courses.add(course); // Agrega el curso a la lista
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Manejo de errores de SQL
        }

        return courses; // Retorna la lista de cursos
    }

    // Método para actualizar los detalles de un curso existente (UPDATE)
    @Override
    public void update(Course course) {
        String sql = "UPDATE Course SET name=?, credits=? WHERE code=?"; // Consulta SQL para actualizar los campos de un curso

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            // Establece los valores de los parámetros para la consulta SQL
            stmt.setString(1, course.getName());
            stmt.setInt(2, course.getCredits());
            stmt.setString(3, course.getCode());

            // Ejecuta la actualización
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); // Manejo de errores de SQL
        }
    }

    // Método para eliminar un curso basado en su código (DELETE)
    @Override
    public void delete(String code) {
        String sql = "DELETE FROM Course WHERE code=?"; // Consulta SQL para eliminar un curso por código

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, code); // Establece el valor del código a eliminar
            int rowsAffected = stmt.executeUpdate(); // Ejecuta la eliminación

            // Si se eliminó algún curso, imprime un mensaje
            if (rowsAffected > 0) {
                System.out.println("Course with code " + code + " has been deleted successfully.");
            } else {
                System.out.println("No course found with the code: " + code); // Si no se encontró el curso, imprime un mensaje
            }
        } catch (SQLException e) {
            System.err.println("Error while deleting course with code " + code); // Si hubo un error en la eliminación, muestra un mensaje de error
            e.printStackTrace();
        }
    }

    // Método para autenticar si un curso con un código específico ya existe (puede ser usado para verificar la duplicidad)
    @Override
    public boolean authenticate(String code) {
        String sql = "SELECT code FROM Course WHERE code=?"; // Consulta SQL para verificar si el código del curso existe

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, code); // Establece el valor del código
            ResultSet rs = stmt.executeQuery(); // Ejecuta la consulta
            if (rs.next()) {
                return rs.getString("code").equals(code); // Si el código coincide, el curso existe
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Manejo de errores de SQL
        }
        return false; // Si no hay coincidencias, devuelve false
    }
}



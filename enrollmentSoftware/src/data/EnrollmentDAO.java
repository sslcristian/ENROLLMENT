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

    // Constructor que recibe la conexión a la base de datos
    public EnrollmentDAO(Connection connection) {
        this.connection = connection;
    }

    // Método para guardar una nueva inscripción en la base de datos
    public void save(Enrollment enrollment) {
        String query = "INSERT INTO Enrollment (student_id, course_code, enrollment_date) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            // Establece los valores de los parámetros para la consulta SQL
            pstmt.setString(1, enrollment.getStudentId());
            pstmt.setString(2, enrollment.getCourseCode());
            // Convierte la fecha a java.sql.Date y la establece como parámetro
            java.sql.Date sqlDate = java.sql.Date.valueOf(enrollment.getEnrollmentDate());
            pstmt.setDate(3, sqlDate);
            
            // Ejecuta la consulta SQL y obtiene el número de filas afectadas
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Enrollment inserted successfully."); // Si la inserción fue exitosa
            } else {
                System.out.println("No rows affected, enrollment was not inserted."); // Si no se afectaron filas
            }
        } catch (SQLException e) {
            System.err.println("Error inserting enrollment: " + e.getMessage());
            e.printStackTrace();  // Imprime el error si ocurre una excepción
        }
    }

    // Método para obtener todas las inscripciones de la base de datos
    public ArrayList<Enrollment> fetch() {
        ArrayList<Enrollment> enrollments = new ArrayList<>();
        String query = "SELECT student_id, course_code, enrollment_date FROM Enrollment";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            // Itera sobre los resultados y crea un objeto Enrollment para cada registro
            while (rs.next()) {
                String studentId = rs.getString("student_id");
                String courseCode = rs.getString("course_code");
                LocalDate enrollmentDate = rs.getDate("enrollment_date").toLocalDate();
                Enrollment enrollment = new Enrollment(studentId, courseCode, enrollmentDate);
                enrollments.add(enrollment);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching enrollments: " + e.getMessage());
            e.printStackTrace();  // Imprime el error si ocurre una excepción
        }

        return enrollments; // Retorna la lista de inscripciones
    }

    // Método para actualizar una inscripción en la base de datos
    public void update(Enrollment enrollment) {
        String sql = "UPDATE Enrollment SET student_id=?, course_code=?, enrollment_date=? WHERE student_id=? AND course_code=?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            // Establece los valores de los parámetros para la consulta SQL
            stmt.setString(1, enrollment.getStudentId());
            stmt.setString(2, enrollment.getCourseCode());
            stmt.setDate(3, Date.valueOf(enrollment.getEnrollmentDate()));

            // Establece la condición de actualización
            stmt.setString(4, enrollment.getStudentId());
            stmt.setString(5, enrollment.getCourseCode());

            // Ejecuta la actualización y obtiene el número de filas afectadas
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Enrollment updated successfully."); // Si la actualización fue exitosa
            } else {
                System.out.println("No enrollment found for update."); // Si no se encontró ninguna inscripción para actualizar
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Imprime el error si ocurre una excepción
        }
    }

    // Método para eliminar una inscripción de la base de datos
    public void delete(String studentId, String courseCode) {
        String sql = "DELETE FROM Enrollment WHERE student_id=? AND course_code=?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, studentId); // Establece el valor del studentId
            stmt.setString(2, courseCode); // Establece el valor del courseCode
            
            // Ejecuta la eliminación y obtiene el número de filas afectadas
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Enrollment deleted successfully."); // Si la eliminación fue exitosa
            } else {
                System.out.println("No enrollment found with the given studentId and courseCode."); // Si no se encontró la inscripción
            }
        } catch (SQLException e) {
            System.err.println("Error while deleting enrollment: " + e.getMessage());
            e.printStackTrace(); // Imprime el error si ocurre una excepción
        }
    }

    // Método para obtener los cursos en los que un estudiante está inscrito
    public ArrayList<Course> getCoursesByStudent(String studentId) {
        ArrayList<Course> courses = new ArrayList<>();
        String query = "SELECT c.code, c.name, c.credits " +
                       "FROM Course c " +
                       "JOIN Enrollment e ON c.code = e.course_code " +
                       "WHERE e.student_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, studentId); // Establece el valor del studentId
            ResultSet rs = stmt.executeQuery(); // Ejecuta la consulta
            
            // Itera sobre los resultados y agrega los cursos a la lista
            while (rs.next()) {
                String code = rs.getString("code");
                String name = rs.getString("name");
                int credits = rs.getInt("credits");
                Course course = new Course(code, name, credits);
                courses.add(course);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Imprime el error si ocurre una excepción
        }
        
        return courses; // Retorna la lista de cursos
    }

    // Método para obtener los estudiantes que están inscritos en un curso específico
    public ArrayList<Student> getStudentsByCourse(String courseCode) {
        ArrayList<Student> students = new ArrayList<>();
        String query = "SELECT s.id, s.name, s.email " +
                       "FROM Student s " +
                       "JOIN Enrollment e ON s.id = e.student_id " +
                       "WHERE e.course_code = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, courseCode); // Establece el valor del courseCode
            ResultSet rs = stmt.executeQuery(); // Ejecuta la consulta
            
            // Itera sobre los resultados y agrega los estudiantes a la lista
            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                Student student = new Student(id, name, email);
                students.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Imprime el error si ocurre una excepción
        }
        
        return students; // Retorna la lista de estudiantes
    }

    // Método para verificar si un estudiante está inscrito en un curso
    public boolean authenticate(String studentId, String courseCode) {
        String sql = "SELECT COUNT(*) FROM Enrollment WHERE student_id=? AND course_code=?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, studentId); // Establece el valor del studentId
            stmt.setString(2, courseCode); // Establece el valor del courseCode
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1); // Obtiene el número de registros
                    if (count > 0) {
                        System.out.println("The student is already enrolled in this course.");
                        return false;  // Si el estudiante ya está inscrito, devuelve false
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Imprime el error si ocurre una excepción
        }
        
        return true; // Si no está inscrito, devuelve true
    }

    // Método para verificar si un estudiante existe en la base de datos
    public boolean studentExists(String studentId) {
        String sql = "SELECT COUNT(*) FROM Student WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, studentId); // Establece el valor del studentId
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Si el número de registros es mayor que 0, el estudiante existe
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Imprime el error si ocurre una excepción
        }
        return false; // Si no existe, retorna false
    }

    // Método para verificar si un curso existe en la base de datos
    public boolean courseExists(String courseCode) {
        String sql = "SELECT COUNT(*) FROM Course WHERE code = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, courseCode); // Establece el valor del courseCode
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Si el número de registros es mayor que 0, el curso existe
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Imprime el error si ocurre una excepción
        }
        return false; // Si no existe, retorna false
    }
}
package controller;

import java.sql.Connection;
import java.time.LocalDate;

import data.CourseDAO;
import data.DBConnection;
import data.EnrollmentDAO;
import data.StudentDAO;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Enrollment;
import model.Student;
import model.Course;

public class EnrollmentsController {
	
    @FXML private ComboBox<String> studentComboBox;
    @FXML private ComboBox<String> courseComboBox;
    @FXML private DatePicker enrollmentDatePicker;
    @FXML private TableView<Enrollment> enrollmentTable;
    @FXML private TableColumn<Enrollment, String> studentIdColumn;
    @FXML private TableColumn<Enrollment, String> courseCodeColumn;
    @FXML private TableColumn<Enrollment, LocalDate> dateColumn;
    
    private Connection connection = DBConnection.getInstance().getConnection();
    private EnrollmentDAO enrollmentDAO = new EnrollmentDAO(connection);
    private StudentDAO studentDAO = new StudentDAO(connection);
    private CourseDAO courseDAO = new CourseDAO(connection);
    
    @FXML
    public void initialize() {
        studentIdColumn.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        courseCodeColumn.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("enrollmentDate"));

        enrollmentDatePicker.setValue(LocalDate.now());
        loadComboBoxes();
        loadEnrollments();
    }
    
    private void loadComboBoxes() {
        studentComboBox.getItems().setAll(
            studentDAO.fetch().stream().map(Student::getId).toList()
        );
        courseComboBox.getItems().setAll(
            courseDAO.fetch().stream().map(Course::getCode).toList()
        );
    }
    
    private void loadEnrollments() {
        enrollmentTable.getItems().setAll(enrollmentDAO.fetch());
    }

}

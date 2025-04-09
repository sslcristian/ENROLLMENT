package controller;

import java.sql.Connection;

import data.CourseDAO;
import data.DBConnection;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Course;
public class CoursesController {
	
	
    @FXML private TextField codeField;
    @FXML private TextField nameField;
    @FXML private TextField creditsField;
    @FXML private TableView<Course> courseTable;
    @FXML private TableColumn<Course, String> codeColumn;
    @FXML private TableColumn<Course, String> titleColumn;
    
    
    @FXML private TableColumn<Course, Integer> creditsColumn;
    
    private Connection connection = DBConnection.getInstance().getConnection();
    private CourseDAO courseDAO = new CourseDAO(connection);
    
    @FXML
    public void initialize() {
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        creditsColumn.setCellValueFactory(new PropertyValueFactory<>("credits"));
        loadCourses();
    }

    private void loadCourses() {
        courseTable.getItems().setAll(courseDAO.fetch());
    }
    
       
  
    
       
    private void clearFields() {
        codeField.clear();
        nameField.clear();
        creditsField.clear();
    }

}

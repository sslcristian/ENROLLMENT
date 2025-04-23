package controller;

import application.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class MainMenuController {

    @FXML
    private Button btnCourse;

    @FXML
    private Button btnEnrollment;

    @FXML
    private Button btnStudent;

    @FXML
    private VBox root;

   
    private void loadScene(String scenePath) {
        try {
            Main.loadScene(scenePath); 
        } catch (Exception e) {
            System.out.println("Error al cargar la escena: " + scenePath);
            e.printStackTrace();  
        }
    }

   
    @FXML
    void goToStudent(ActionEvent event) {
        loadScene("/view/Students.fxml");  
    }

   
    @FXML
    void goToCourse(ActionEvent event) {
        loadScene("/view/Courses.fxml");  
    }

    
    @FXML
    void goToEnrollment(ActionEvent event) {
        loadScene("/view/Enrollments.fxml");  
    }
}

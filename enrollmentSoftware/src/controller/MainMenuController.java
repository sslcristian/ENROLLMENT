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

    @FXML
    void goToCourse(ActionEvent event) {
    	Main.loadScene("/view/Courses.fxml");
    }

    @FXML
    void goToEnrollment(ActionEvent event) {
    	Main.loadScene("/view/Enrollments.fxml");
    }

    @FXML
    void goToStudent(ActionEvent event) {
    	Main.loadScene("/view/Students.fxml");
    }

}
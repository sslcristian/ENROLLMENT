package controller;

import application.Main;

public class MainMenuController {
	
    public void goToStudents() {
        Main.loadView("Students.fxml");
    }

    public void goToCourses() {
        Main.loadView("Courses.fxml");
    }

    public void goToEnrollment() {
        Main.loadView("Enrollments.fxml");
    }

}

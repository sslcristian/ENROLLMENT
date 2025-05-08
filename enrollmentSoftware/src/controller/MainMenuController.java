package controller; // Define el paquete donde se encuentra esta clase

import application.Main; // Importa la clase principal para cambiar escenas
import javafx.event.ActionEvent; // Para manejar eventos de botones
import javafx.fxml.FXML; // Para enlazar elementos del archivo FXML
import javafx.scene.control.Button; // Botón de la interfaz
import javafx.scene.layout.VBox; // Contenedor vertical (layout)

public class MainMenuController {

    // Enlaza el botón para ir a la sección de Cursos
    @FXML
    private Button btnCourse;

    // Enlaza el botón para ir a la sección de Inscripciones
    @FXML
    private Button btnEnrollment;

    // Enlaza el botón para ir a la sección de Estudiantes
    @FXML
    private Button btnStudent;

    // Enlaza el VBox principal del menú (puede usarse para efectos visuales o control de layout)
    @FXML
    private VBox root;

    /**
     * Método privado reutilizable que cambia la escena actual a otra
     * según la ruta del archivo FXML que se le pase.
     */
    private void loadScene(String scenePath) {
        try {
            Main.loadScene(scenePath); // Llama al método estático para cambiar la escena
        } catch (Exception e) {
            // Si ocurre un error, se muestra en consola
            System.out.println("Error al cargar la escena: " + scenePath);
            e.printStackTrace();  
        }
    }

    /**
     * Acción cuando se presiona el botón "Estudiantes".
     * Navega a la vista Students.fxml.
     */
    @FXML
    void goToStudent(ActionEvent event) {
        loadScene("/view/Students.fxml");  
    }

    /**
     * Acción cuando se presiona el botón "Cursos".
     * Navega a la vista Courses.fxml.
     */
    @FXML
    void goToCourse(ActionEvent event) {
        loadScene("/view/Courses.fxml");  
    }

    /**
     * Acción cuando se presiona el botón "Inscripciones".
     * Navega a la vista Enrollments.fxml.
     */
    @FXML
    void goToEnrollment(ActionEvent event) {
        loadScene("/view/Enrollments.fxml");  
    }
}

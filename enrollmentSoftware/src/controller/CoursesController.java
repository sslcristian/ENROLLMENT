package controller;

// Importaciones necesarias
import application.Main;
import data.CourseDAO;
import data.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Course;
import model.FXUtils;
import javafx.scene.input.KeyCode;
import java.sql.Connection;

public class CoursesController {

    // Elementos visuales conectados al archivo FXML
    @FXML private TableView<Course> courseTable;             // Tabla que muestra los cursos
    @FXML private TableColumn<Course, String> codeColumn;    // Columna de código
    @FXML private TableColumn<Course, String> nameColumn;    // Columna de nombre
    @FXML private TableColumn<Course, Integer> creditsColumn; // Columna de créditos

    @FXML private TextField codeField;     // Campo de texto para código del curso
    @FXML private TextField nameField;     // Campo de texto para nombre del curso
    @FXML private TextField creditsField;  // Campo de texto para créditos del curso

    @FXML private Button btnAdd;           // Botón para agregar curso
    @FXML private Button btnUpdate;        // Botón para actualizar curso
    @FXML private Button btnDelete;        // Botón para eliminar curso
    @FXML private Button btnFetch;         // Botón para refrescar lista de cursos
    @FXML private Button btnBackToMenu;    // Botón para volver al menú principal

    // Variables de conexión y acceso a datos
    private final Connection connection = DBConnection.getInstance().getConnection(); // Obtiene la conexión a BD
    private final CourseDAO courseDAO = new CourseDAO(connection);                    // DAO para operar sobre cursos
    private final ObservableList<Course> courseList = FXCollections.observableArrayList(); // Lista observable para tabla

    @FXML
    public void initialize() {
        // Asocia las columnas de la tabla con propiedades del modelo Course
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        creditsColumn.setCellValueFactory(new PropertyValueFactory<>("credits"));

        // Carga la lista de cursos al iniciar
        fetchCourses();

        // Evento: cuando se selecciona un curso en la tabla, carga sus datos en los campos
        courseTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                codeField.setText(newSel.getCode());
                nameField.setText(newSel.getName());
                creditsField.setText(String.valueOf(newSel.getCredits()));
                codeField.setDisable(true); // No se puede cambiar el código en modo edición
            }
        });

        // Evento: doble clic en una fila limpia la selección y los campos
        courseTable.setRowFactory(tv -> {
            TableRow<Course> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 2) {
                    FXUtils.clearSelectionAndFieldsC(courseTable, codeField, nameField, creditsField);
                }
            });
            return row;
        });

        // Evento: al presionar ESC se limpian los campos y la selección
        courseTable.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                FXUtils.clearSelectionAndFieldsC(courseTable, codeField, nameField, creditsField);
            }
        });
    }

    @FXML
    public void addCourse(ActionEvent event) {
        // Obtener los valores ingresados
        String code = codeField.getText().trim();
        String name = nameField.getText().trim();
        String creditsText = creditsField.getText().trim();

        // Validar campos vacíos
        if (code.isEmpty() || name.isEmpty() || creditsText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Campos vacíos", "Complete todos los campos.");
            return;
        }

        // Validar que los créditos sean un número entero
        if (!creditsText.matches("\\d+")) {
            showAlert(Alert.AlertType.ERROR, "Créditos inválidos", "Los créditos deben ser un número entero.");
            return;
        }

        // Validar que el código no esté duplicado
        if (courseDAO.exists(code)) {
            showAlert(Alert.AlertType.WARNING, "Curso duplicado", "Ya existe un curso con ese código.");
            return;
        }

        // Crear y guardar el curso
        int credits = Integer.parseInt(creditsText);
        Course course = new Course(code, name, credits);
        courseDAO.save(course);

        // Actualizar la tabla y limpiar los campos
        fetchCourses();
        clearFields();
    }

    @FXML
    public void updateCourse(ActionEvent event) {
        // Obtener el curso seleccionado
        Course selected = courseTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Sin selección", "Seleccione un curso de la tabla.");
            return;
        }

        // Obtener nuevos valores
        String name = nameField.getText().trim();
        String creditsText = creditsField.getText().trim();

        // Validar campos
        if (name.isEmpty() || creditsText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Campos vacíos", "Complete todos los campos.");
            return;
        }

        if (!creditsText.matches("\\d+")) {
            showAlert(Alert.AlertType.ERROR, "Créditos inválidos", "Los créditos deben ser un número entero.");
            return;
        }

        // Actualizar los valores y guardar
        selected.setName(name);
        selected.setCredits(Integer.parseInt(creditsText));
        courseDAO.update(selected);

        // Refrescar tabla y limpiar campos
        fetchCourses();
        clearFields();
    }

    @FXML
    public void deleteCourse(ActionEvent event) {
        // Obtener el curso seleccionado
        Course selected = courseTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Sin selección", "Seleccione un curso para eliminar.");
            return;
        }

        // Eliminar el curso
        courseDAO.delete(selected.getCode());

        // Actualizar tabla y limpiar campos
        fetchCourses();
        clearFields();
    }

    @FXML
    public void fetchCourses() {
        // Obtener los cursos desde la BD y mostrarlos en la tabla
        courseList.setAll(courseDAO.fetch());
        courseTable.setItems(courseList);
    }

    // Limpia todos los campos del formulario
    private void clearFields() {
        codeField.clear();
        nameField.clear();
        creditsField.clear();
        codeField.setDisable(false); // Volver a habilitar campo de código
        courseTable.getSelectionModel().clearSelection(); // Quitar selección en tabla
    }

    // Muestra un mensaje de alerta
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void goBackToMenu(ActionEvent event) {
        // Cambia la escena al menú principal
        Main.loadScene("/view/MainMenu.fxml");
    }
}

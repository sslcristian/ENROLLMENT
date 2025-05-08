package controller; // Define el paquete al que pertenece esta clase

// Importaciones necesarias para manejo de base de datos y JavaFX
import java.sql.Connection;

import application.Main; // Para poder cambiar de escenas
import data.DBConnection; // Conexión a la base de datos
import data.StudentDAO; // Acceso a los datos de estudiantes
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.FXUtils; // Utilidades gráficas para limpiar campos
import model.Student; // Modelo de datos de Estudiante
import javafx.scene.input.KeyCode; // Para capturar tecla ESC

public class StudentsController {

    // Tabla donde se listan los estudiantes
    @FXML private TableView<Student> studentTable;
    
    // Columnas para mostrar ID, nombre y correo electrónico
    @FXML private TableColumn<Student, String> idColumn;
    @FXML private TableColumn<Student, String> nameColumn;
    @FXML private TableColumn<Student, String> emailColumn;

    // Campos de texto para ingresar/editar datos de un estudiante
    @FXML private TextField idField;
    @FXML private TextField nameField;
    @FXML private TextField emailField;

    // Botones para interactuar con los estudiantes
    @FXML private Button btnAdd;
    @FXML private Button btnUpdate;
    @FXML private Button btnDelete;
    @FXML private Button btnFetch;
    @FXML private Button btnBackToMenu;

    // Conexión a la base de datos, DAO y lista observable de estudiantes
    private final Connection connection = DBConnection.getInstance().getConnection();
    private final StudentDAO studentDAO = new StudentDAO(connection);
    private final ObservableList<Student> studentList = FXCollections.observableArrayList();

    // Método que se ejecuta automáticamente al iniciar el controlador
    @FXML
    public void initialize() {
        // Asocia cada columna con la propiedad del objeto Student
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Carga los estudiantes desde la base de datos
        fetchStudents();

        // Al seleccionar un estudiante de la tabla, llena los campos de texto
        studentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                idField.setText(newSelection.getId());
                nameField.setText(newSelection.getName());
                emailField.setText(newSelection.getEmail());
                idField.setDisable(true); // No se puede cambiar el ID
            }
        });

        // Permite limpiar la selección haciendo doble clic en una fila
        studentTable.setRowFactory(tv -> {
            TableRow<Student> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 2) {
                    FXUtils.clearSelectionAndFieldsS(studentTable, idField, nameField, emailField);  
                }
            });
            return row;
        });

        // Limpia los campos al presionar la tecla ESC
        studentTable.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                FXUtils.clearSelectionAndFieldsS(studentTable, idField, nameField, emailField);  
            }
        });
    }

    // Método para agregar un nuevo estudiante
    @FXML
    public void addStudent() {
        // Obtiene valores de los campos
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();

        // Valida que no haya campos vacíos
        if (id.isEmpty() || name.isEmpty() || email.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Campos vacíos", "Por favor, complete todos los campos.");
            return;
        }

        // Verifica si ya existe un estudiante con ese ID
        if (studentDAO.authenticate(id)) {
            showAlert(Alert.AlertType.WARNING, "Estudiante duplicado", "Ya existe un estudiante con ese ID.");
            return;
        }

        // Crea y guarda el nuevo estudiante
        Student student = new Student(id, name, email);
        studentDAO.save(student);
        fetchStudents(); // Actualiza la tabla
        clearFields(); // Limpia los campos
    }

    // Método para actualizar un estudiante existente
    @FXML
    public void updateStudent() {
        // Verifica que haya un estudiante seleccionado
        Student selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Sin selección", "Seleccione un estudiante de la tabla.");
            return;
        }

        // Obtiene nuevos valores
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();

        // Valida campos vacíos
        if (name.isEmpty() || email.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Campos vacíos", "Por favor, complete todos los campos.");
            return;
        }

        // Aplica cambios y actualiza en base de datos
        selected.setName(name);
        selected.setEmail(email);
        studentDAO.update(selected);
        fetchStudents();
        clearFields();
    }

    // Método para eliminar un estudiante
    @FXML
    public void deleteStudent() {
        // Verifica que haya selección
        Student selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Sin selección", "Seleccione un estudiante para eliminar.");
            return;
        }

        // Elimina por ID
        studentDAO.delete(selected.getId());
        fetchStudents();
        clearFields();
    }

    // Carga todos los estudiantes desde la base de datos a la tabla
    @FXML
    public void fetchStudents() {
        studentList.setAll(studentDAO.fetch());
        studentTable.setItems(studentList);
    }

    // Limpia los campos de texto y deselecciona la tabla
    private void clearFields() {
        idField.clear();
        nameField.clear();
        emailField.clear();
        idField.setDisable(false); // Habilita el campo ID para nuevo ingreso
        studentTable.getSelectionModel().clearSelection();
    }

    // Muestra una alerta en pantalla con el tipo, título y contenido especificado
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Botón para regresar al menú principal
    @FXML
    void goBackToMenu(ActionEvent event) {
        Main.loadScene("/view/MainMenu.fxml");
    }
}

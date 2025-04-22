package controller;

import java.sql.Connection;

import application.Main;
import data.DBConnection;
import data.StudentDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.FXUtils;
import model.Student;
import javafx.scene.input.KeyCode;

public class StudentsController {

    @FXML private TableView<Student> studentTable;
    @FXML private TableColumn<Student, String> idColumn;
    @FXML private TableColumn<Student, String> nameColumn;
    @FXML private TableColumn<Student, String> emailColumn;

    @FXML private TextField idField;
    @FXML private TextField nameField;
    @FXML private TextField emailField;

    @FXML private Button btnAdd;
    @FXML private Button btnUpdate;
    @FXML private Button btnDelete;
    @FXML private Button btnFetch;
    @FXML private Button btnBackToMenu;

    private final Connection connection = DBConnection.getInstance().getConnection();
    private final StudentDAO studentDAO = new StudentDAO(connection);
    private final ObservableList<Student> studentList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        fetchStudents();

        studentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                idField.setText(newSelection.getId());
                nameField.setText(newSelection.getName());
                emailField.setText(newSelection.getEmail());
                idField.setDisable(true);
            }
        });
     
        studentTable.setRowFactory(tv -> {
            TableRow<Student> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 2) {
                    FXUtils.clearSelectionAndFieldsS(studentTable, idField, nameField, emailField);  
                }
            });
            return row;
        });

      
        studentTable.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                FXUtils.clearSelectionAndFieldsS(studentTable, idField, nameField, emailField);  
            }
        });
        
    }

    @FXML
    public void addStudent() {
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();

        if (id.isEmpty() || name.isEmpty() || email.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Campos vacíos", "Por favor, complete todos los campos.");
            return;
        }

        if (studentDAO.authenticate(id)) {
            showAlert(Alert.AlertType.WARNING, "Estudiante duplicado", "Ya existe un estudiante con ese ID.");
            return;
        }

        Student student = new Student(id, name, email);
        studentDAO.save(student);
        fetchStudents();
        clearFields();
    }

    @FXML
    public void updateStudent() {
        Student selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Sin selección", "Seleccione un estudiante de la tabla.");
            return;
        }

        String name = nameField.getText().trim();
        String email = emailField.getText().trim();

        if (name.isEmpty() || email.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Campos vacíos", "Por favor, complete todos los campos.");
            return;
        }

        selected.setName(name);
        selected.setEmail(email);
        studentDAO.update(selected);
        fetchStudents();
        clearFields();
    }

    @FXML
    public void deleteStudent() {
        Student selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Sin selección", "Seleccione un estudiante para eliminar.");
            return;
        }

        studentDAO.delete(selected.getId());
        fetchStudents();
        clearFields();
    }

    @FXML
    public void fetchStudents() {
        studentList.setAll(studentDAO.fetch());
        studentTable.setItems(studentList);
    }

    private void clearFields() {
        idField.clear();
        nameField.clear();
        emailField.clear();
        idField.setDisable(false);
        studentTable.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    void goBackToMenu(ActionEvent event) {
        Main.loadScene("/view/MainMenu.fxml");
    }
} 



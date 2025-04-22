package controller;

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

    @FXML private TableView<Course> courseTable;
    @FXML private TableColumn<Course, String> codeColumn;
    @FXML private TableColumn<Course, String> nameColumn;
    @FXML private TableColumn<Course, Integer> creditsColumn;

    @FXML private TextField codeField;
    @FXML private TextField nameField;
    @FXML private TextField creditsField;

    @FXML private Button btnAdd;
    @FXML private Button btnUpdate;
    @FXML private Button btnDelete;
    @FXML private Button btnFetch;
    @FXML private Button btnBackToMenu;

    private final Connection connection = DBConnection.getInstance().getConnection();
    private final CourseDAO courseDAO = new CourseDAO(connection);
    private final ObservableList<Course> courseList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        creditsColumn.setCellValueFactory(new PropertyValueFactory<>("credits"));

        fetchCourses();

        courseTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                codeField.setText(newSel.getCode());
                nameField.setText(newSel.getName());
                creditsField.setText(String.valueOf(newSel.getCredits()));
                codeField.setDisable(true);
            }
        });
      
        courseTable.setRowFactory(tv -> {
            TableRow<Course> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 2) {
                    FXUtils.clearSelectionAndFieldsC(courseTable, codeField, nameField, creditsField); 
                }
            });
            return row;
        });

       
        courseTable.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                FXUtils.clearSelectionAndFieldsC(courseTable, codeField, nameField, creditsField);  
            }
        });
    }

    @FXML
    public void addCourse(ActionEvent event) {
        String code = codeField.getText().trim();
        String name = nameField.getText().trim();
        String creditsText = creditsField.getText().trim();

        if (code.isEmpty() || name.isEmpty() || creditsText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Campos vacíos", "Complete todos los campos.");
            return;
        }

        if (!creditsText.matches("\\d+")) {
            showAlert(Alert.AlertType.ERROR, "Créditos inválidos", "Los créditos deben ser un número entero.");
            return;
        }

        if (courseDAO.exists(code)) {
            showAlert(Alert.AlertType.WARNING, "Curso duplicado", "Ya existe un curso con ese código.");
            return;
        }

        int credits = Integer.parseInt(creditsText);
        Course course = new Course(code, name, credits);
        courseDAO.save(course);
        fetchCourses();
        clearFields();
    }

    @FXML
    public void updateCourse(ActionEvent event) {
        Course selected = courseTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Sin selección", "Seleccione un curso de la tabla.");
            return;
        }

        String name = nameField.getText().trim();
        String creditsText = creditsField.getText().trim();

        if (name.isEmpty() || creditsText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Campos vacíos", "Complete todos los campos.");
            return;
        }

        if (!creditsText.matches("\\d+")) {
            showAlert(Alert.AlertType.ERROR, "Créditos inválidos", "Los créditos deben ser un número entero.");
            return;
        }

        selected.setName(name);
        selected.setCredits(Integer.parseInt(creditsText));
        courseDAO.update(selected);
        fetchCourses();
        clearFields();
    }

    @FXML
    public void deleteCourse(ActionEvent event) {
        Course selected = courseTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Sin selección", "Seleccione un curso para eliminar.");
            return;
        }

        courseDAO.delete(selected.getCode());
        fetchCourses();
        clearFields();
    }

    @FXML
    public void fetchCourses() {
        courseList.setAll(courseDAO.fetch()); 
        courseTable.setItems(courseList); 
    }

    private void clearFields() {
        codeField.clear();
        nameField.clear();
        creditsField.clear();
        codeField.setDisable(false);
        courseTable.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void goBackToMenu(ActionEvent event) {
        Main.loadScene("/view/MainMenu.fxml");
    }
}

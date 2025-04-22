package controller;

import application.Main;
import data.DBConnection;
import data.EnrollmentDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Course;
import model.Enrollment;
import model.Student;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class EnrollmentsController {
	@FXML
    private Button btnAdd;

    @FXML
    private Button btnBackToMenu;

    @FXML
    private Button btnCoursesByStudent;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnFetch;

    @FXML
    private Button btnStudentsByCourse;

    @FXML
    private Button btnUpdate;

    

    @FXML private TextField studentIdField;
    @FXML private TextField courseCodeField;
    @FXML private DatePicker enrollmentDatePicker;

    @FXML private TableView<Enrollment> enrollmentTable;
    @FXML private TableColumn<Enrollment, String> studentIdColumn;
    @FXML private TableColumn<Enrollment, String> courseCodeColumn;
    @FXML private TableColumn<Enrollment, LocalDate> enrollmentDateColumn;

    private final Connection connection = DBConnection.getInstance().getConnection();
    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAO(connection);
    private final ObservableList<Enrollment> enrollmentList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        studentIdColumn.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        courseCodeColumn.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        enrollmentDateColumn.setCellValueFactory(new PropertyValueFactory<>("enrollmentDate"));

        fetchEnrollments();

        enrollmentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                studentIdField.setText(newSel.getStudentId());
                courseCodeField.setText(newSel.getCourseCode());
                enrollmentDatePicker.setValue(newSel.getEnrollmentDate());
                studentIdField.setDisable(true);
                courseCodeField.setDisable(true);
            }
        });
    }

    @FXML
    public void addEnrollment(ActionEvent event) {
        String studentId = studentIdField.getText().trim();
        String courseCode = courseCodeField.getText().trim();
        LocalDate date = enrollmentDatePicker.getValue();

        if (studentId.isEmpty() || courseCode.isEmpty() || date == null) {
            showAlert(Alert.AlertType.ERROR, "Campos vacíos", "Complete todos los campos.");
            return;
        }

        if (!enrollmentDAO.studentExists(studentId)) {
            showAlert(Alert.AlertType.WARNING, "Estudiante no encontrado", "El estudiante con ID " + studentId + " no existe.");
            return;
        }

        if (!enrollmentDAO.courseExists(courseCode)) {
            showAlert(Alert.AlertType.WARNING, "Curso no encontrado", "El curso con código " + courseCode + " no existe.");
            return;
        }

        if (!enrollmentDAO.authenticate(studentId, courseCode)) {
            showAlert(Alert.AlertType.WARNING, "Inscripción existente", "El estudiante ya está inscrito en este curso.");
            return;
        }

        Enrollment enrollment = new Enrollment(studentId, courseCode, date);
        enrollmentDAO.save(enrollment);
        fetchEnrollments();
        clearFields();
    }


    @FXML
    public void updateEnrollment(ActionEvent event) {
        Enrollment selected = enrollmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Sin selección", "Seleccione una inscripción para actualizar.");
            return;
        }

        LocalDate newDate = enrollmentDatePicker.getValue();
        if (newDate == null) {
            showAlert(Alert.AlertType.ERROR, "Fecha vacía", "Seleccione una fecha de inscripción.");
            return;
        }

        selected.setEnrollmentDate(newDate);
        enrollmentDAO.update(selected);
        fetchEnrollments();
        clearFields();
    }

    @FXML
    public void deleteEnrollment(ActionEvent event) {
        Enrollment selected = enrollmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Sin selección", "Seleccione una inscripción para eliminar.");
            return;
        }

        enrollmentDAO.delete(selected.getStudentId(), selected.getCourseCode());
        fetchEnrollments();
        clearFields();
    }

    @FXML
    public void fetchEnrollments(ActionEvent event) {
        fetchEnrollments();
    }

    private void fetchEnrollments() {
        enrollmentList.setAll(enrollmentDAO.fetch());
        enrollmentTable.setItems(enrollmentList);
    }

    private void clearFields() {
        studentIdField.clear();
        courseCodeField.clear();
        enrollmentDatePicker.setValue(null);
        studentIdField.setDisable(false);
        courseCodeField.setDisable(false);
        enrollmentTable.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void showCoursesByStudent(ActionEvent event) {
        String studentId = studentIdField.getText().trim();
        if (studentId.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "ID requerido", "Ingrese el ID del estudiante.");
            return;
        }

        List<Course> courses = enrollmentDAO.getCoursesByStudent(studentId);
        if (courses.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "Sin resultados", "No se encontraron cursos para este estudiante.");
        } else {
            String message = courses.stream()
                    .map(c -> c.getCode() + " - " + c.getName() + " (" + c.getCredits() + " créditos)")
                    .collect(Collectors.joining("\n"));
            showAlert(Alert.AlertType.INFORMATION, "Cursos del Estudiante", message);
        }
    }

    @FXML
    public void showStudentsByCourse(ActionEvent event) {
        String courseCode = courseCodeField.getText().trim();
        if (courseCode.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Código requerido", "Ingrese el código del curso.");
            return;
        }

        List<Student> students = enrollmentDAO.getStudentsByCourse(courseCode);
        if (students.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "Sin resultados", "No se encontraron estudiantes para este curso.");
        } else {
            String message = students.stream()
                    .map(s -> s.getId() + " - " + s.getName() + " (" + s.getEmail() + ")")
                    .collect(Collectors.joining("\n"));
            showAlert(Alert.AlertType.INFORMATION, "Estudiantes del Curso", message);
        }
    }

    @FXML
    public void goBackToMenu(ActionEvent event) {
        Main.loadScene("/view/MainMenu.fxml");
    }
}


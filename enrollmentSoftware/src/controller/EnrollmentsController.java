package controller;

// Importaciones necesarias
import application.Main;
import javafx.scene.input.KeyCode;
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
import model.FXUtils;
import model.Student;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class EnrollmentsController {

    // Botones de la interfaz
    @FXML private Button btnAdd;
    @FXML private Button btnBackToMenu;
    @FXML private Button btnCoursesByStudent;
    @FXML private Button btnDelete;
    @FXML private Button btnFetch;
    @FXML private Button btnStudentsByCourse;
    @FXML private Button btnUpdate;

    // Campos de texto y selector de fecha
    @FXML private TextField studentIdField;
    @FXML private TextField courseCodeField;
    @FXML private DatePicker enrollmentDatePicker;

    // Tabla y columnas para mostrar las inscripciones
    @FXML private TableView<Enrollment> enrollmentTable;
    @FXML private TableColumn<Enrollment, String> studentIdColumn;
    @FXML private TableColumn<Enrollment, String> courseCodeColumn;
    @FXML private TableColumn<Enrollment, LocalDate> enrollmentDateColumn;

    // Conexión a la base de datos y acceso a datos
    private final Connection connection = DBConnection.getInstance().getConnection();
    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAO(connection);
    private final ObservableList<Enrollment> enrollmentList = FXCollections.observableArrayList();

    // Inicializa la tabla y configura los eventos
    @FXML
    public void initialize() {
        // Enlaza columnas con propiedades del objeto Enrollment
        studentIdColumn.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        courseCodeColumn.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        enrollmentDateColumn.setCellValueFactory(new PropertyValueFactory<>("enrollmentDate"));

        // Carga las inscripciones existentes
        fetchEnrollments();

        // Cuando el usuario selecciona una fila, se llenan los campos de texto
        enrollmentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                studentIdField.setText(newSel.getStudentId());
                courseCodeField.setText(newSel.getCourseCode());
                enrollmentDatePicker.setValue(newSel.getEnrollmentDate());
                studentIdField.setDisable(true);     // El ID y código no se pueden modificar
                courseCodeField.setDisable(true);
            }
        });

        // Limpia los campos si el usuario hace doble clic sobre una fila
        enrollmentTable.setRowFactory(tv -> {
            TableRow<Enrollment> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 2) {
                    FXUtils.clearSelectionAndFieldsE(enrollmentTable, studentIdField, courseCodeField, enrollmentDatePicker);
                }
            });
            return row;
        });

        // Limpia los campos si el usuario presiona la tecla ESC
        enrollmentTable.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                FXUtils.clearSelectionAndFieldsE(enrollmentTable, studentIdField, courseCodeField, enrollmentDatePicker);
            }
        });
    }

    // Agrega una nueva inscripción
    @FXML
    public void addEnrollment(ActionEvent event) {
        String studentId = studentIdField.getText().trim();
        String courseCode = courseCodeField.getText().trim();
        LocalDate date = enrollmentDatePicker.getValue();

        // Validación: campos vacíos
        if (studentId.isEmpty() || courseCode.isEmpty() || date == null) {
            showAlert(Alert.AlertType.ERROR, "Campos vacíos", "Complete todos los campos.");
            return;
        }

        // Validación: existencia del estudiante y curso
        if (!enrollmentDAO.studentExists(studentId)) {
            showAlert(Alert.AlertType.WARNING, "Estudiante no encontrado", "El estudiante con ID " + studentId + " no existe.");
            return;
        }

        if (!enrollmentDAO.courseExists(courseCode)) {
            showAlert(Alert.AlertType.WARNING, "Curso no encontrado", "El curso con código " + courseCode + " no existe.");
            return;
        }

        // Validación: ya existe la inscripción
        if (!enrollmentDAO.authenticate(studentId, courseCode)) {
            showAlert(Alert.AlertType.WARNING, "Inscripción existente", "El estudiante ya está inscrito en este curso.");
            return;
        }

        // Guarda la inscripción y recarga la tabla
        Enrollment enrollment = new Enrollment(studentId, courseCode, date);
        enrollmentDAO.save(enrollment);
        fetchEnrollments();
        clearFields();
    }

    // Actualiza la fecha de una inscripción existente
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

        // Actualiza la fecha en el objeto y la base de datos
        selected.setEnrollmentDate(newDate);
        enrollmentDAO.update(selected);
        fetchEnrollments();
        clearFields();
    }

    // Elimina una inscripción seleccionada
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

    // Botón que recarga los datos
    @FXML
    public void fetchEnrollments(ActionEvent event) {
        fetchEnrollments();
    }

    // Método para cargar las inscripciones desde la base de datos
    private void fetchEnrollments() {
        enrollmentList.setAll(enrollmentDAO.fetch());
        enrollmentTable.setItems(enrollmentList);
    }

    // Limpia los campos de texto y restablece estado
    private void clearFields() {
        studentIdField.clear();
        courseCodeField.clear();
        enrollmentDatePicker.setValue(null);
        studentIdField.setDisable(false);
        courseCodeField.setDisable(false);
        enrollmentTable.getSelectionModel().clearSelection();
    }

    // Muestra un cuadro de diálogo con mensaje personalizado
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Muestra los cursos en los que está inscrito un estudiante
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

    // Muestra los estudiantes inscritos en un curso
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

    // Regresa al menú principal
    @FXML
    public void goBackToMenu(ActionEvent event) {
        Main.loadScene("/view/MainMenu.fxml");
    }
}

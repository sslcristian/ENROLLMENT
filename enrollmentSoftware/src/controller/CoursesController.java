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
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.util.converter.IntegerStringConverter;
import model.Course;
import model.FXUtils;

import java.sql.Connection;

public class CoursesController {

    // Elementos visuales conectados al archivo FXML
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

    // Variables de conexión y acceso a datos
    private final Connection connection = DBConnection.getInstance().getConnection();
    private final CourseDAO courseDAO = new CourseDAO(connection);
    private final ObservableList<Course> courseList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Asocia columnas con propiedades del modelo
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        creditsColumn.setCellValueFactory(new PropertyValueFactory<>("credits"));

        // Hacer editable el TableView
        courseTable.setEditable(true);

        // Configurar edición en las columnas
        // Código no editable (por eso no le ponemos CellFactory editable)
        // Nombre editable como texto simple
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        // Créditos editable, con conversor de String a Integer
        creditsColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        // Evento edición para actualizar el nombre en base de datos y lista
        nameColumn.setOnEditCommit(event -> {
            Course course = event.getRowValue();
            course.setName(event.getNewValue());
            courseDAO.update(course);
            fetchCourses(); // refrescar tabla para mostrar cambio confirmado
        });

        // Evento edición para actualizar créditos
        creditsColumn.setOnEditCommit(event -> {
            Course course = event.getRowValue();
            course.setCredits(event.getNewValue());
            courseDAO.update(course);
            fetchCourses();
        });

        // Cargar cursos al iniciar
        fetchCourses();

        // Cuando seleccionas fila, carga datos en campos para editar
        courseTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                codeField.setText(newSel.getCode());
                nameField.setText(newSel.getName());
                creditsField.setText(String.valueOf(newSel.getCredits()));
                codeField.setDisable(true); // Código no editable en edición
            }
        });

        // Doble clic para limpiar selección y campos
        courseTable.setRowFactory(tv -> {
            TableRow<Course> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 2) {
                    FXUtils.clearSelectionAndFieldsC(courseTable, codeField, nameField, creditsField);
                }
            });
            return row;
        });

        // Tecla ESC limpia selección y campos
        courseTable.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                FXUtils.clearSelectionAndFieldsC(courseTable, codeField, nameField, creditsField);
            }

            // Tecla DELETE elimina curso seleccionado con confirmación
            if (event.getCode() == KeyCode.DELETE) {
                Course selected = courseTable.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                        "¿Eliminar el curso " + selected.getName() + "?",
                        ButtonType.YES, ButtonType.NO);
                    confirm.showAndWait();

                    if (confirm.getResult() == ButtonType.YES) {
                        courseDAO.delete(selected.getCode());
                        fetchCourses();
                        clearFields();
                    }
                }
            }
        });
    }

    // Botón Agregar curso
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

    // Botón Actualizar curso (solo con selección en tabla)
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

    // Botón Eliminar curso (solo con selección en tabla)
    @FXML
    public void deleteCourse(ActionEvent event) {
        Course selected = courseTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Sin selección", "Seleccione un curso para eliminar.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Eliminar el curso " + selected.getName() + "?",
                ButtonType.YES, ButtonType.NO);
        confirm.showAndWait();

        if (confirm.getResult() == ButtonType.YES) {
            courseDAO.delete(selected.getCode());
            fetchCourses();
            clearFields();
        }
    }

    // Carga los cursos desde la BD y actualiza la tabla
    @FXML
    public void fetchCourses() {
        courseList.setAll(courseDAO.fetch());
        courseTable.setItems(courseList);
    }

    // Limpia campos y habilita el código
    private void clearFields() {
        codeField.clear();
        nameField.clear();
        creditsField.clear();
        codeField.setDisable(false);
        courseTable.getSelectionModel().clearSelection();
    }

    // Muestra alertas
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Botón para volver al menú principal
    @FXML
    public void goBackToMenu(ActionEvent event) {
        Main.loadScene("/view/MainMenu.fxml");
    }
}


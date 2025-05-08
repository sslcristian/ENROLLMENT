package application;

// Importaciones necesarias para JavaFX
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;  
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;

public class Main extends Application {

    // Atributos estáticos para mantener el diseño principal y el escenario principal
    private static BorderPane rootLayout;  
    private static Stage primaryStage;

    // Método principal de JavaFX que se ejecuta al iniciar la aplicación
    @Override
    public void start(Stage primaryStage) {
        try {
            // Asigna el escenario principal a la variable estática
            Main.primaryStage = primaryStage;

            // Carga el archivo FXML (interfaz gráfica) para el menú principal
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainMenu.fxml"));
            rootLayout = loader.load();  // Carga el layout en rootLayout

            // Crea la escena usando el layout cargado
            Scene scene = new Scene(rootLayout);

            // Aplica una hoja de estilos CSS a la escena
            scene.getStylesheets().add(getClass().getResource("/view/style.css").toExternalForm());

            // Carga el ícono de la ventana
            loadIcon();

            // Establece la escena y muestra la ventana
            primaryStage.setScene(scene);
            primaryStage.setTitle("Enrollment Software");  // Título de la ventana
            primaryStage.show();  // Muestra la interfaz
        } catch (Exception e) {
            e.printStackTrace();  // Imprime errores si los hay
        }
    }

    // Método para cargar un ícono personalizado en la ventana
    private void loadIcon() {
        try {
            // Intenta cargar la imagen desde los recursos
            Image icon = new Image(getClass().getResourceAsStream("/application/icons/UDI.png"));

            // Verifica si hubo un error al cargar
            if (icon.isError()) {
                System.out.println("Error: No se pudo cargar el icono.");
            } else {
                // Asigna el ícono al escenario
                primaryStage.getIcons().add(icon);
            }
        } catch (Exception e) {
            System.out.println("Error al cargar el icono: " + e.getMessage());
        }
    }

    // Método estático para cargar una vista FXML dentro del diseño principal
    public static void loadView(String fxmlFile) {
        try {
            // Carga la nueva vista
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxmlFile));
            Parent view = loader.load();

            // Reemplaza el contenido actual del rootLayout con la nueva vista
            rootLayout.getChildren().setAll(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método estático para cambiar completamente de escena
    public static void loadScene(String fxmlFile) {
        try {
            // Carga el archivo FXML como raíz de la nueva escena
            Parent root = FXMLLoader.load(Main.class.getResource(fxmlFile));
            Scene scene = primaryStage.getScene();

            if (scene == null) {
                // Si no hay escena aún, crea una nueva
                scene = new Scene(root, 600, 600);
                primaryStage.setScene(scene);
            } else {
                // Si ya hay una escena, cambia solo el contenido raíz
                scene.setRoot(root);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método principal estándar de Java que lanza la aplicación JavaFX
    public static void main(String[] args) {
        launch(args);  // Inicia la aplicación JavaFX
    }
}


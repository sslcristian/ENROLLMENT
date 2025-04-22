package application;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.fxml.FXMLLoader;

public class Main extends Application {
	private static VBox rootLayout;
	private static Stage primaryStage;

	@Override
	public void start(Stage primaryStage) {
		try {
			Main.primaryStage = primaryStage;

			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainMenu.fxml"));
			rootLayout = loader.load();

			Scene scene = new Scene(rootLayout);

	
			scene.getStylesheets().add(getClass().getResource("/view/style.css").toExternalForm());

			primaryStage.setScene(scene);
			primaryStage.setTitle("Enrollment Software");
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	
	public static void loadView(String fxmlFile) {
		try {
			FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxmlFile));
			Parent view = loader.load();
			rootLayout.getChildren().setAll(view); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public static void loadScene(String fxmlFile) {
		try {
			Parent root = FXMLLoader.load(Main.class.getResource(fxmlFile));
			Scene scene = primaryStage.getScene();
			if (scene == null) {
				scene = new Scene(root, 600, 600);
				primaryStage.setScene(scene);
			} else {
				scene.setRoot(root);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}

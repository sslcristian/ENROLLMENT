package model;

import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class FXUtils {
	public static <T> void clearSelectionAndFieldsE(TableView<T> table, TextField studentField, TextField courseField, DatePicker datePicker) {
        table.getSelectionModel().clearSelection();
        studentField.clear();
        courseField.clear();
        datePicker.setValue(null);

        studentField.setDisable(false);
        courseField.setDisable(false);
    }
	 public static <T> void clearSelectionAndFieldsS(TableView<T> table, TextField idField, TextField nameField, TextField emailField) {
	        table.getSelectionModel().clearSelection();  
	        idField.clear();  
	        nameField.clear();
	        emailField.clear();
	        idField.setDisable(false); 
	    }
	 public static <T> void clearSelectionAndFieldsC(TableView<T> table, TextField codeField, TextField nameField, TextField creditsField) {
		    table.getSelectionModel().clearSelection();  // 
		    codeField.clear();  
		    nameField.clear(); 
		    creditsField.clear(); 
		    codeField.setDisable(false);  
		}

}

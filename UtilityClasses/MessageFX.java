package UtilityClasses;

import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;

/*
    This class contains some useful dialog boxes,
    that are used to show confirmation or error messages to the user.
*/
public class MessageFX {

    //This message is shown when one of the javaFX components that use database tries to access the database without it being initialized first.
    public static String NO_DATABASE_MESSAGE = "There is no database set!\nYou can set the database with the method \"setDataBase\" on this component!";
    
   //Shows informations message to the user.
    public static void messageDialog(String infoMassage, String titleBar, String headerMassage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titleBar);
        alert.setHeaderText(headerMassage);
        alert.setContentText(infoMassage);
        alert.showAndWait();
    }
    
    //Shows error message to the user.
    public static void errorMassage(String infoMassage, String titleBar, String headerMassage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titleBar);
        alert.setHeaderText(headerMassage);
        alert.setContentText(infoMassage);
        alert.showAndWait();
    }
    
    //Shows input dialog box that lets the user input information.
    public static String inputDialog(String infoMassage, String titleBar, String headerMassage) {
        TextInputDialog textPane = new TextInputDialog();
        textPane.setTitle(titleBar);
        textPane.setHeaderText(headerMassage);
        textPane.setContentText(infoMassage);
        textPane.showAndWait();
        return textPane.getEditor().getText();
    }
    
}

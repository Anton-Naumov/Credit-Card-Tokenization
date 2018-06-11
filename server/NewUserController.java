package server;

import UtilityClasses.MessageFX;
import UtilityClasses.Permissions;
import UtilityClasses.TokenizerInterfaceImpl;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/*
    The NewUser reusable component controller.
    Has text fields for name and password, button for 
    registering user in the database and button to quit.
*/
public class NewUserController extends VBox {

    private TokenizerInterfaceImpl dataBase;

    //Loads the FXML file and sets its root and controller.
    public NewUserController() {
        FXMLLoader fxmlLoader = new FXMLLoader(
            getClass().getResource("/server/NewUser.fxml"));

        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
    
    //Initializes the database field.
    public void setTokenizer(TokenizerInterfaceImpl dataBase) {
        this.dataBase = dataBase;
    }
    
    @FXML
    private Label txtHeader;

    @FXML
    private TextField txtUserName;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private CheckBox chkRead;

    @FXML
    private CheckBox chkWrite;

    @FXML
    private Button btnRegister;

    @FXML
    private Button btnQuit;

    //Closes the component if the button Quit is clicked.
    @FXML
    void btnQuitClicked(ActionEvent event) {
        Stage stage = (Stage) btnQuit.getScene().getWindow();
        stage.close();
    }

    //Shows error massage if the database is not initialized.
    //Takes the input from the Username text field and password text field, and
    //passes them to the registerUser method of the database.
    //Shows error message if the method registerUser throws IOException.
    //Shows confirmation message if the user was registered successfuly.
    @FXML
    void btnRegisterClicked(ActionEvent event) {
        if (dataBase == null) {
            MessageFX.errorMassage(MessageFX.NO_DATABASE_MESSAGE, "Error!", null);
            return;
        }
        String name = txtUserName.getText();
        String password = txtPassword.getText();
        Permissions rights;
        if (chkRead.isSelected() && chkWrite.isSelected())
            rights = Permissions.REGISTER_TOKEN_AND_READ_CARD;
        else if (chkWrite.isSelected())
            rights = Permissions.REGISTER_TOKEN;
        else
            rights = Permissions.READ_CARD;
            
        
        try {
            dataBase.registerUser(name, password, rights);
            MessageFX.messageDialog("User successfuly registered!", "Registration massage.", null);
        } catch (IOException ex) {
            Logger.getLogger(NewUserController.class.getName()).log(Level.SEVERE, null, ex);
            MessageFX.errorMassage(ex.getMessage(), "Registration error!", null);
        }
       
        btnQuitClicked(event);
    }
    
    @FXML
    void initialize() {
        assert txtHeader != null : "fx:id=\"txtHeader\" was not injected: check your FXML file 'NewUser.fxml'.";
        assert txtUserName != null : "fx:id=\"txtUserName\" was not injected: check your FXML file 'NewUser.fxml'.";
        assert txtPassword != null : "fx:id=\"txtPassword\" was not injected: check your FXML file 'NewUser.fxml'.";
        assert chkRead != null : "fx:id=\"chkRead\" was not injected: check your FXML file 'NewUser.fxml'.";
        assert chkWrite != null : "fx:id=\"chkWrite\" was not injected: check your FXML file 'NewUser.fxml'.";
        assert btnRegister != null : "fx:id=\"btnRegister\" was not injected: check your FXML file 'NewUser.fxml'.";
        assert btnQuit != null : "fx:id=\"btnQuit\" was not injected: check your FXML file 'NewUser.fxml'.";

    }
}

package client;

import UtilityClasses.MessageFX;
import UtilityClasses.TokenizerInterface;
import java.io.IOException;
import java.rmi.RemoteException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/*
    The clints reusable LogIn component controller.
*/
public class LogInController extends VBox {

    private TokenizerInterface dataBaseInterface;

    //Loads the FXML file and sets its root and controller.
    public LogInController() {
        FXMLLoader fxmlLoader = new FXMLLoader(
            getClass().getResource("/client/LogIn.fxml"));

        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
    
    //Initializes the dataBaseInterface field.
    public void setTokenizerInterface(TokenizerInterface dataBaseInterface) {
        this.dataBaseInterface = dataBaseInterface;
    }
    
    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Button btnLogIn;

    @FXML
    private Button btnQuit;
    
    //Shows error massage if the database is not initialized.
    //Takes the input from the Username text field and password text field, and
    //passes them to the logIn method of the databaseInterface.
    //If that method throws RemoteException shows error message and quits the application.
    //If the LogIn was successful shows the ClientController component and hides the current one.
    @FXML
    void btnLogInClicked(ActionEvent event) throws IOException {
        if (dataBaseInterface == null) {
            MessageFX.errorMassage(MessageFX.NO_DATABASE_MESSAGE, "Error!", null);
            return;
        } 
        String name = txtUsername.getText();
        String password = txtPassword.getText();
        
        try {
            if (!dataBaseInterface.logIn(name, password)) {
                MessageFX.errorMassage("Wrong user information", "Log in error!", null);
                return;
            }
        } catch (RemoteException ex) {
            System.out.println("Log in button error!");
            MessageFX.errorMassage("The server was disconnected!", "Server error!", null);
            Platform.exit();
        }
                
        ClientController controller = new ClientController();
        controller.setTokenizerInterfaceAndUser(dataBaseInterface, name);
        
        Scene newScene = new Scene(controller);
        Stage newStage = new Stage();
        newStage.setTitle("Client");
        newStage.setScene(newScene);
        newStage.sizeToScene();
        newStage.resizableProperty().setValue(Boolean.FALSE);
        Stage logInStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        logInStage.hide();
        newStage.showAndWait();
        logInStage.show();
        txtUsername.setText("");
        txtPassword.setText("");
    }

    //Exits the application if the Quit button is clicked.
    @FXML
    void btnQuitClicked(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    void initialize() {
        assert txtUsername != null : "fx:id=\"txtUsername\" was not injected: check your FXML file 'LogIn.fxml'.";
        assert txtPassword != null : "fx:id=\"txtPassword\" was not injected: check your FXML file 'LogIn.fxml'.";
        assert btnLogIn != null : "fx:id=\"btnLogIn\" was not injected: check your FXML file 'LogIn.fxml'.";
        assert btnQuit != null : "fx:id=\"btnQuit\" was not injected: check your FXML file 'LogIn.fxml'.";
    }
}

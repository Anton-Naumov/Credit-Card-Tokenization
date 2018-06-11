package client;

import UtilityClasses.MessageFX;
import UtilityClasses.Permissions;
import UtilityClasses.TokenizerInterface;
import java.io.IOException;
import java.rmi.RemoteException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/*
    The Clients reusable component controller.
    Has a tab with text field and button for registering new credit card numbers, 
    a tab with text field and button for getting card number for given token and
    a log out button that returs to the LogInController component.
*/
public class ClientController extends VBox {

    private TokenizerInterface dataBaseInterface;

    //Loads the FXML file and sets its root and controller.
    public ClientController() {
        FXMLLoader fxmlLoader = new FXMLLoader(
            getClass().getResource("/client/Client.fxml"));

        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
 
    }
    
    //Initializes the field dataBaseInterface and sets the header label text
    //and disables one of the tabs if the loggedUser does't have permissions to use it
    public void setTokenizerInterfaceAndUser(TokenizerInterface dataBaseInterface, String userName) {
        this.dataBaseInterface = dataBaseInterface;
        lblHeader.setText(String.format("Welcome %s", userName));
        try {
            Permissions loggedUserPermissions = dataBaseInterface.getLoggedUserPermissions();
            if (null == loggedUserPermissions) {
            } else switch (loggedUserPermissions) {
                case REGISTER_TOKEN_AND_READ_CARD:
                    return;
                case READ_CARD:
                    tabPane.getTabs().remove(tabRegister);
                    break;                
                default:
                    tabPane.getTabs().remove(tabRead);
                    break;
            }
        } catch (RemoteException ex) {
            MessageFX.errorMassage("The server was disconnected!", "Server error!", null);
        }               
    }
    
    @FXML
    private Tab tabRegister;

    @FXML
    private Tab tabRead;

    @FXML
    private TabPane tabPane;

    
    @FXML
    private Label lblHeader;

    @FXML
    private TextField txtCardNumber;

    @FXML
    private Button btnRegisterCard;

    @FXML
    private TextField txtToken;

    @FXML
    private Button btnGetCardNumber;

    @FXML
    private Button btnLogOut;

    //Shows error massage if the database is not initialized.
    //Takes the input from the token text field and calls the dataBaseInterface
    //method getCardNumber with it.
    //Shows error message if RemoteException occurs and quits the application.
    //Shows error message if the dataBaseInterface method getCardNumber throws Exception.
    //Shows confirmation message with the credit card number if the operation was successful.
    @FXML
    void btnGetCardNumberClicked(ActionEvent event) {
        if (dataBaseInterface == null) {
            MessageFX.errorMassage(MessageFX.NO_DATABASE_MESSAGE, "Error!", null);
            return;
        }
        String token = txtToken.getText();
        String cardNumber = "";
        try {
            cardNumber = dataBaseInterface.getCardNumber(token);
        } catch (RemoteException ex){
            System.out.println("Remote Exception!");
            MessageFX.errorMassage("The server was disconnected!", "Server error!", null);
            Platform.exit();
            return;
        } catch (Exception ex) {         
            MessageFX.errorMassage(ex.getMessage(), "Error!", null);
            return;
        } finally {
            txtToken.setText("");
        }
        MessageFX.messageDialog(String.format("The card number is: %s", cardNumber), "Operation successful", null);        
    }

    //Closes the current stage
    @FXML
    void btnLogOutClicked(ActionEvent event) {
        Stage clientStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        clientStage.close();
    }

    //Shows error massage if the database is not initialized.
    //Takes the input from the card number text field and calls the dataBaseInterface
    //method registerCard with it.
    //Shows error message if RemoteException occurs and quits the application.
    //Shows error message if the dataBaseInterface method registerCard throws Exception.
    //Shows confirmation message with the credit card's token if the operation was successful.
    @FXML
    void btnRegisterCardClicked(ActionEvent event) {
        if (dataBaseInterface == null) {
            MessageFX.errorMassage(MessageFX.NO_DATABASE_MESSAGE, "Error!", null);
            return;
        }
        String cardNumber = txtCardNumber.getText();
        String token = "";
        try {
            token = dataBaseInterface.registerCard(cardNumber);
        } catch (RemoteException ex) {            
            MessageFX.errorMassage("The server was disconnected!", "Server error!", null);
            Platform.exit();
            return;
        } catch (Exception ex) {            
            MessageFX.errorMassage(ex.getMessage(), "Error!", null);            
            return;
        }
        finally {
            txtCardNumber.setText("");
        }
        MessageFX.messageDialog(String.format("The card token is: %s", token), "Registration successful", null);        
    }

    @FXML
    void initialize() {
        assert lblHeader != null : "fx:id=\"lblHeader\" was not injected: check your FXML file 'Client.fxml'.";
        assert txtCardNumber != null : "fx:id=\"txtCardNumber\" was not injected: check your FXML file 'Client.fxml'.";
        assert btnRegisterCard != null : "fx:id=\"btnRegisterCard\" was not injected: check your FXML file 'Client.fxml'.";
        assert txtToken != null : "fx:id=\"txtToken\" was not injected: check your FXML file 'Client.fxml'.";
        assert btnGetCardNumber != null : "fx:id=\"btnGetCardNumber\" was not injected: check your FXML file 'Client.fxml'.";
        assert btnLogOut != null : "fx:id=\"btnLogOut\" was not injected: check your FXML file 'Client.fxml'.";
        assert tabRead != null : "fx:id=\"tabRead\" was not injected: check your FXML file 'Client.fxml'.";
        assert tabRegister != null : "fx:id=\"tabRegister\" was not injected: check your FXML file 'Client.fxml'.";
        assert tabPane != null : "fx:id=\"tabPane\" was not injected: check your FXML file 'Client.fxml'.";
    }
}

package server;

import UtilityClasses.CardTokenPair;
import UtilityClasses.MessageFX;
import UtilityClasses.TokenizerInterfaceImpl;
import UtilityClasses.User;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/*
    The servers reusable component controller.
    Contains database field, and buttons to add/delete
    users and card number-token pairs.
    Displays the users registered in the database in a ListView control.
    Displays the credit card number-token pairs registered in the database in a ListView control.
*/
public class ServerController extends VBox {

    private TokenizerInterfaceImpl dataBase;

    //Loads the FXML file and sets its root and controller.
    public ServerController() {
        FXMLLoader fxmlLoader = new FXMLLoader(
            getClass().getResource("/server/Server.fxml"));

        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    //Returns the database object
    public TokenizerInterfaceImpl getTokenizer() {
        return dataBase;
    }
    
    //Initializes the database field and binds ListView controls properties to
    //the database properties.
    public void setTokenizerInterfaceImpl(TokenizerInterfaceImpl tokenizer) {
        this.dataBase = tokenizer;
        bindUsers(this.dataBase.usersPropertyProperty());
        bindCards(this.dataBase.cardsPropertyProperty());
    }
    
    public void bindUsers(ObjectProperty<ObservableList<User>> usersProperty) {
        livUsers.itemsProperty().bind(usersProperty);
    }
    
    public void bindCards(ObjectProperty<ObservableList<CardTokenPair>> cardsProperty) {
        livCardNumbers.itemsProperty().bind(cardsProperty);
    }
    
    @FXML
    private Button btnAddUser;

    @FXML
    private Button btnDeleteUser;
    
    @FXML
    private Button btnRegisterCard;

    @FXML
    private Button btnDeleteCard;

    @FXML
    private ListView<User> livUsers;

    @FXML
    private ListView<CardTokenPair> livCardNumbers;
    
    @FXML
    private Button btnSaveUsersInFile;

    @FXML
    private Button btnSaveCardsInFile;
    
    @FXML
    private Button btnSaveCardsInFileSortedByTokens;

    //Opens the NewUserController component to register a new user in the database.
    @FXML
    void btnAddUserClicked(ActionEvent event) throws IOException {
        NewUserController component = new NewUserController();
        component.setTokenizer(dataBase);
        Scene newUserScene = new Scene(component);
        Stage newUserStage = new Stage();
        newUserStage.setScene(newUserScene);
        btnAddUser.setDisable(true);
        newUserStage.resizableProperty().setValue(Boolean.FALSE);
        newUserStage.showAndWait();
        btnAddUser.setDisable(false);
    }

    //Takes the seleced in the ListView user and removes it from the database.
    //Shows error message if no user is selected.
    //Shows confirmation message if the user is registered.
    @FXML
    void btnDeleteUserClicked(ActionEvent event) throws IOException {
        User selectedUser = livUsers.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            MessageFX.errorMassage("Select user to delete!", "Delete user error", null);
        } else if (dataBase.deleteUser(selectedUser)) {
            MessageFX.messageDialog(String.format("User %s successfuly deleted.", selectedUser), "User Delete", null);
        } else {
            MessageFX.errorMassage(String.format("User %s not found!", selectedUser), "Delete error", null);
        }
    }
    
    //Shows error massage if the database is not initialized.
    //Gives the user a input dialog to enter the card number he wants to register.
    //Shows error message if RemoteException occurs.
    //Registeres the credit card number in the database.
    //Shows confirmation message if the card number is registered.
    @FXML
    void btnRegisterCardClicked(ActionEvent event) {
        if (dataBase == null) {
            MessageFX.errorMassage(MessageFX.NO_DATABASE_MESSAGE, "Error!", null);
            return;
        }        
        String cardNumber = MessageFX.inputDialog("Card number to register:", "Card registration!", null);
        if (cardNumber.equals("")) {
            return;
        }
        String userOwningTheCard = MessageFX.inputDialog("The name of the user to set as owner of the card:", "Input owner of the card!", null);
        if (userOwningTheCard.equals("")) {
            return;
        }
        String token = "";
        try {          
            token = dataBase.serverRegisterCard(cardNumber, userOwningTheCard);
        } catch (Exception ex) {
            MessageFX.errorMassage(ex.getMessage(), "Error!", null);            
            return;
        }
        MessageFX.messageDialog(String.format("The card token is: %s", token), "Registration successful!", null);  
    }
    
    //Takes the seleced in the ListView card number-token pair and removes it from the database.
    //Shows error message if no card number-token pair is selected.
    //Shows confirmation message if the card number-token pair is registered.
    @FXML
    void btnDeleteCardClicked(ActionEvent event) throws RemoteException {
        CardTokenPair selectedCard = livCardNumbers.getSelectionModel().getSelectedItem();
        if (selectedCard == null) {
            MessageFX.errorMassage("Select card to delete!", "Delete card error", null);
        } else if (dataBase.deleteCard(selectedCard)) {  
            MessageFX.messageDialog(String.format("Card %s successfuly deleted.", selectedCard), "Card Delete", null);
        } else {
            MessageFX.errorMassage(String.format("Card %s not found!", selectedCard), "Delete error", null);
        }
    }

    //Shows error massage if the database is not initialized.
    //Gives the user a input dialog box for him to enter the file he wants to save the credit card numbers in.
    //Shows error message if the database method throws IOException.
    //Shows confimation message if the file is created successfuly.
    @FXML
    void btnSaveCardsInFileClicked(ActionEvent event) {
        if (dataBase == null) {
            MessageFX.errorMassage(MessageFX.NO_DATABASE_MESSAGE, "Error!", null);
            return;
        }
        String fileName = MessageFX.inputDialog("File name:", "Save cards in file!", "Write the name of the file in wich you want to save the cards.");
        try {
            dataBase.saveCardsInFileSortedByCard(fileName);
        } catch (IOException ex) {
            Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
            MessageFX.errorMassage("Saving card numbers in file error.", "File error!", null);
            return;
        }
        MessageFX.messageDialog(String.format("File \"%s\" successfuly created.", fileName), "Card numbers saved!", null);
    }
    
    //Shows error massage if the database is not initialized.
    //Gives the user a input dialog box for him to enter the file he wants to save the credit card numbers in.
    //Shows error message if the database method throws IOException.
    //Shows confimation message if the file is created successfuly.

    @FXML
    void btnSaveCardsInFileSortedByTokensClicked(ActionEvent event) {
        if (dataBase == null) {
            MessageFX.errorMassage(MessageFX.NO_DATABASE_MESSAGE, "Error!", null);
            return;
        }
        String fileName = MessageFX.inputDialog("File name:", "Save cards in file!", "Write the name of the file in wich you want to save the cards.");
        try {
            dataBase.saveCardsInFileSortedByToken(fileName);
        } catch (IOException ex) {
            Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
            MessageFX.errorMassage("Saving card numbers in file error.", "File error!", null);
            return;
        }
        MessageFX.messageDialog(String.format("File \"%s\" successfuly created.", fileName), "Card numbers saved!", null);
    }

    //Shows error massage if the database is not initialized.
    //Gives the user a input dialog box for him to enter the file he wants to save the credit card numbers in.
    //Shows error message if the database method throws IOException.
    //Shows confimation message if the file is created successfuly.
    @FXML
    void btnSaveUsersInFileClicked(ActionEvent event) {
        if (dataBase == null) {
            MessageFX.errorMassage(MessageFX.NO_DATABASE_MESSAGE, "Error!", null);
            return;
        }
        String fileName = MessageFX.inputDialog("File name:", "Save registered users in file!", "Write the name of the file in wich you want to save the users.");
        try {
            dataBase.saveUsersInFile(fileName);
        } catch (IOException ex) {
            Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
            MessageFX.errorMassage("Saving registered users in file error.", "File error!", null);
            return;
        }
        MessageFX.messageDialog(String.format("File \"%s\" successfuly created", fileName), "Registered users saved!", null);
    }
    
    @FXML
    void initialize() {
        assert btnAddUser != null : "fx:id=\"btnAddUser\" was not injected: check your FXML file 'Server.fxml'.";
        assert btnDeleteUser != null : "fx:id=\"btnDeleteUser\" was not injected: check your FXML file 'Server.fxml'.";
        assert btnRegisterCard != null : "fx:id=\"btnRegisterCard\" was not injected: check your FXML file 'Server.fxml'.";
        assert btnDeleteCard != null : "fx:id=\"btnDeleteCard\" was not injected: check your FXML file 'Server.fxml'.";      
        assert livUsers != null : "fx:id=\"livUsers\" was not injected: check your FXML file 'Server.fxml'.";
        assert livCardNumbers != null : "fx:id=\"livCardNumbers\" was not injected: check your FXML file 'Server.fxml'.";
        assert btnSaveUsersInFile != null : "fx:id=\"btnSaveUsersInFile\" was not injected: check your FXML file 'Server.fxml'.";
        assert btnSaveCardsInFile != null : "fx:id=\"btnSaveCardsInFile\" was not injected: check your FXML file 'Server.fxml'.";
        assert btnSaveCardsInFileSortedByTokens != null : "fx:id=\"btnSaveCardsInFileSortedByTokens\" was not injected: check your FXML file 'Server.fxml'.";

    }
    
    public void saveAndClose() {
        dataBase.saveInformation();
        Platform.exit();
    }
}

package Client;

import UtilityClasses.TokenizerInterface;
import client.LogInController;
import javafx.fxml.FXML;

public class ClientCompController {

    public void setDataBase(TokenizerInterface dataBase) {
        LogIn.setTokenizerInterface(dataBase);
    }
    
    @FXML
    private LogInController LogIn;

    @FXML
    void initialize() {
        assert LogIn != null : "fx:id=\"LogIn\" was not injected: check your FXML file 'ClientComp.fxml'.";

    }
}


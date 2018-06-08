package Server;

import UtilityClasses.TokenizerInterfaceImpl;
import javafx.fxml.FXML;
import server.ServerController;

public class ServerCompController {

    public void setDataBase(TokenizerInterfaceImpl dataBase) {
        ServerNode.setTokenizerInterfaceImpl(dataBase);
    }
    
    public void saveInformation() {
        ServerNode.saveAndClose();
    }
    
    @FXML
    private ServerController ServerNode;

    @FXML
    void initialize() {
        assert ServerNode != null : "fx:id=\"ServerNode\" was not injected: check your FXML file 'ServerComp.fxml'.";

    }
}

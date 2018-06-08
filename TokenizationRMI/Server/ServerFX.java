package Server;

import UtilityClasses.TokenizerInterface;
import UtilityClasses.TokenizerInterfaceImpl;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ServerFX extends Application {
    
    /*
        Loads the fxml file.
        Creates dataBase file(TokenizerInterafaceImpl) and sets the controllers dataBase.
        Registeres the database object (TokenizerInterafaceImpl) on port 1099 with name "TokenizerInterfaceImpl".
    */
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ServerComp.fxml"));
        Parent root = loader.load();
        ServerCompController serverController = loader.getController();
        TokenizerInterfaceImpl tokenizer = new TokenizerInterfaceImpl();
        serverController.setDataBase(tokenizer);        
        TokenizerInterface tokenizerInterface = tokenizer;
        
        Registry registry = LocateRegistry.createRegistry(1099);
        registry.rebind("TokenizerInterfaceImpl", tokenizerInterface);
        
        Scene scene = new Scene(root);
        
        primaryStage.sizeToScene();
        primaryStage.resizableProperty().setValue(Boolean.FALSE);
        primaryStage.setTitle("Server");       
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> {
            serverController.saveInformation();
            System.exit(0);                            
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}

package Client;

import UtilityClasses.MessageFX;
import UtilityClasses.TokenizerInterface;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientFX extends Application {
    
    /*
        Loades the fxml file.
        Looks for the databaseInterface on port 1099 with name "TokenizerInterfaceImpl".
        Reades the dataBaseInterface from the registy and sets the controllers dataBase.
    */
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ClientComp.fxml"));
        Parent root = loader.load();
        ClientCompController logInController = loader.getController();
        
        try {
            Registry registry = LocateRegistry.getRegistry(1099);
            TokenizerInterface newTokenizerInterface = (TokenizerInterface)registry.lookup("TokenizerInterfaceImpl");
            logInController.setDataBase(newTokenizerInterface);
        } catch (NotBoundException ex) {
            System.out.println("Registry not found!");
            MessageFX.errorMassage("The selected registry not found!", "Registry error!", null);
            Platform.exit();
        }
        
        Scene scene = new Scene(root);
        
        primaryStage.sizeToScene();
        primaryStage.resizableProperty().setValue(Boolean.FALSE);
        primaryStage.setTitle("Client Log in!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}

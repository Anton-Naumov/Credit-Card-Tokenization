package UtilityClasses;

import java.rmi.Remote;
import java.rmi.RemoteException;

/*
    This class contains the methods that the client can invoke remotely after
    reading the object from the registry.
*/
public interface TokenizerInterface extends Remote {

    boolean logIn(String name, String password) throws RemoteException;
    String registerCard(String cardNumber) throws RemoteException, Exception;
    String getCardNumber(String token) throws RemoteException , Exception;
    Permissions getLoggedUserPermissions() throws RemoteException;
}

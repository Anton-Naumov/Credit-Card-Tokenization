package UtilityClasses;

import com.thoughtworks.xstream.XStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Formatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/*
    This class is the "database" for the project.
    It contains list of the users that are registered and a list of
    registered credit card number - token pairs.
    The class has methods for validating, adding, deleting, serializing and
    deserializing users and card numbers.
    It also has users property and cards property that can be binded to a JavaFX
    component so it is updated every time there is a change in the registered users or cards.
*/

public class TokenizerInterfaceImpl extends UnicastRemoteObject implements TokenizerInterface {
       
    private static final XStream xstream = new XStream();
    // The name of the file that the users are serialized in and deserialized from.
    private static final String USERS_FILE = "usersDataBase.xml"; 
    // The name of the file that the credit card numbers are serialized in and deserialized from.
    private static final String CARDS_FILE = "cardsDataBase.xml";
    
    private User loggedUser; // null if no user is logged in
    private final List<User> users;
    private final List<CardTokenPair> cards;
    private final ObjectProperty<ObservableList<User>> usersProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<ObservableList<CardTokenPair>> cardsProperty = new SimpleObjectProperty<>();
    
    //The constructor initializes the lists of users and cards with the
    //information from the files (USERS_FILE and CARDS_FILE) if the files exist
    //and if they dont't it creates them and makes empty lists of users and cards.
    public TokenizerInterfaceImpl() throws RemoteException {     
        File usersFile = new File(USERS_FILE);
        File cardsFile = new File(CARDS_FILE);
        
        try {        
            if (!usersFile.exists()) {           
                usersFile.createNewFile();            
            }
            if (!cardsFile.exists()) {
                cardsFile.createNewFile();
            }
        } catch (IOException ex) {
            Logger.getLogger(TokenizerInterfaceImpl.class.getName()).log(Level.SEVERE, null, ex);         
        } finally {
            if (usersFile.length() != 0) // if the file is not empty it reads it and initializes the users list
                users = (ArrayList<User>)xstream.fromXML(usersFile);
            else 
                users = new ArrayList<>(); // if it is empty it creates empty list
            if (cardsFile.length() != 0) // if the file is not empty it reads it and initializes the card numbers list
                cards = (ArrayList<CardTokenPair>)xstream.fromXML(cardsFile);
            else
                cards = new ArrayList<>(); // if it is empty it creates empty list
            updateUsersProperty(); // updates the users property
            updateCardsProperty(); // updates the card numbers propery
        }
    }
    
    //This method saves the loaded users in the file USERS_FILE and
    //the credit card numbers in the file CARDS_FILE.
    public void saveInformation() {
        try {
            try (Formatter usersFile = new Formatter(new File(USERS_FILE))) {
                usersFile.format("%s", xstream.toXML(users));
            }
            try (Formatter cardsFile = new Formatter(new File(CARDS_FILE))) {
                cardsFile.format("%s", xstream.toXML(cards));
            }
         } catch (FileNotFoundException ex) {
            Logger.getLogger(TokenizerInterfaceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //This metod validates the given user. If it is valid adds it in the list,
    //and if it isn't throws IOException with message.
    public void registerUser(String name, String password, Permissions permissions) throws IOException {
        //The name should start with a small or capital letter and then have one or more digits or letters.
        if (name.matches("[a-zA-Z][0-9a-zA-Z ]*") == false) {
            throw new IOException("The name is invalid!");
        }
        //The password should consist of one or more digits, small or capital letters.
        if (password.matches("[0-9a-zA-Z]+") == false) {
            throw new IOException("The password is invalid!");
        }
        //Checks if the given users name has already been registered.
        if (users.stream().anyMatch(user -> user.getName().equals(name))) {
            throw new IOException("The user has already been registered!");
        }
        System.out.println(permissions);
        User registeredUser = new User(name, password, permissions);
        users.add(registeredUser);
        System.out.println(registeredUser);
        updateUsersProperty();    
    }
    
    //This method returns true if the given users name is registered and deletes it,
    //and false if he wasn't found among the registered users.
    //The method also deletes all cards that are assosiated with the deleted user.
    public boolean deleteUser(User user) {
        if (users.stream().anyMatch(currUser -> user.equals(user))) {
            users.remove(user);
            cards.stream().filter((currPair) -> (currPair.getCardOwner().equals(user))).forEachOrdered((currPair) -> {
                cards.remove(currPair);
            });
            updateUsersProperty();
            return true;
        }
        return false;
    }
    
    //This method returns true if the given card number is registered and deletes it,
    //and false if it wasn't found among the registered card numbers.
    public boolean deleteCard(CardTokenPair cardNumber) {
        if (cards.stream().anyMatch(card -> card.equals(cardNumber))) {
            cards.remove(cardNumber);
            updateCardsProperty();
            return true;
        }
        return false;
    }
    
    //This method saves the registered card number - token pairs sorted by
    //the card numebers in the given file.
    public void saveCardsInFileSortedByCard(String fileName) throws IOException {
        Files.write(Paths.get(fileName), cards.stream()
                                               .sorted(Comparator.comparing(CardTokenPair::getCard))
                                               .map(CardTokenPair::toString)
                                               .collect(Collectors.toList()), Charset.defaultCharset());
    }
    //This method saves the registered card number - token pairs sorted by
    //the tokens in the given file.
    public void saveCardsInFileSortedByToken(String fileName) throws IOException {
        Files.write(Paths.get(fileName), cards.stream()
                                               .sorted(Comparator.comparing(CardTokenPair::getToken))
                                               .map(CardTokenPair::toString)
                                               .collect(Collectors.toList()), Charset.defaultCharset());
    }
    
    //This method saves the registered users sorted by their names in the given file.
    public void saveUsersInFile(String fileName) throws IOException {
        Files.write(Paths.get(fileName), users.stream()
                                              .map(User::toString)
                                              .collect(Collectors.toList()), Charset.defaultCharset());
    }
    
    //Checks if the given token is in the list of registered card - token pairs.
    public boolean isTokenInDataBase(String token) {
        return cards.stream().map(CardTokenPair::getToken).anyMatch(currToken -> currToken.equals(token));
    }
    
    //Implemented abstract methods
    
    //This method ckecks if the given user is registered and if he is it initializes the loggedUser field with that user.
    @Override
    public boolean logIn(String name, String password) throws RemoteException {
        if (users.stream().anyMatch(user -> user.getName().equals(name) && user.getPassword().equals(password)) == false) {
            return false;
        }        
        loggedUser = users.stream().filter(user -> user.getName().equals(name) && user.getPassword().equals(password)).findFirst().get();
        
        return true;
    }
    
    //Method that registeres cards by given cardNumber and the name of the owner of the card.
    public String serverRegisterCard(String cardNumber, String ownersName) throws Exception {
        if (CardTokenPair.isCardNumberValid(cardNumber) == false) {
            throw new Exception("The card number is invalid!");
        }
        if (!users.stream().anyMatch((user) -> user.getName().equals(ownersName))) {
            throw new Exception("No user with that name found on the server.");
        }
        String token = CardTokenPair.tokenize(cardNumber);
        //this cycle makes sure that the new token hasn't already been given to another card among the registered
        while (isTokenInDataBase(token)) {
            token = CardTokenPair.tokenize(cardNumber);
        }
        cards.add(new CardTokenPair(cardNumber, token, users.stream().filter((user) -> user.getName().equals(ownersName)).findFirst().get()));
        updateCardsProperty();
        return token;
    }
    
    //Ckecks if the given card number is correct, and if the logged user has permissions
    //to register card numbers, creates token for the card number, registers it and returns it's token.
    @Override
    public String registerCard(String cardNumber) throws RemoteException, Exception {
        if (CardTokenPair.isCardNumberValid(cardNumber) == false) {
            throw new Exception("The card number is invalid!");
        }
        if (loggedUser != null && loggedUser.getPermissions()== Permissions.READ_CARD) {
            throw new Exception("You don't have permission to register cards!");
        }
        String token = CardTokenPair.tokenize(cardNumber);
        //this cycle makes sure that the new token hasn't already been given to another card among the registered
        while (isTokenInDataBase(token)) {
            token = CardTokenPair.tokenize(cardNumber);
        }
        cards.add(new CardTokenPair(cardNumber, token, loggedUser));
        updateCardsProperty();
        return token;
    }
    
    //Throws Exception if the given token is not valid, if it is not registered
    //or if the logged user does not have permissions to read card numbers and 
    //returns the card number coresponding to the token if no exception was thrown.
    @Override
    public String getCardNumber(String token) throws RemoteException, Exception {
        if (token.matches("\\d{16}") == false) {
            throw new Exception("The token is invalid!");
        }
        if (!cards.stream().map(CardTokenPair::getToken).anyMatch(tToken -> tToken.equals(token))) {
            throw new Exception("There is no card with this token!");
        }
        if (loggedUser.getPermissions()== Permissions.REGISTER_TOKEN) {
            throw new Exception("You don't have permission to access card numbers!");
        }
        CardTokenPair resultCard = cards.stream().filter(pair -> pair.getToken().equals(token)).findFirst().get();
        if (resultCard.getCardOwner().equals(loggedUser) == false) {
            throw new Exception("You can't access that card number.");
        }
        
        return resultCard.getCard();
    }
    
    @Override
    public Permissions getLoggedUserPermissions() throws RemoteException {
        if (loggedUser == null) {
            return null;
        }
        return loggedUser.getPermissions();
    }

    
    //Methods for getting and setting the users and card number - token pairs properties.
    public ObservableList<CardTokenPair> getCardsProperty() {
        return cardsProperty.get();
    }

    public void setCardsProperty(ObservableList<CardTokenPair> value) {
        cardsProperty.set(value);
    }

    public ObjectProperty cardsPropertyProperty() {
        return cardsProperty;
    }
    
    public ObservableList<User> getUsersProperty() {
        return usersProperty.get();
    }

    public void setUsersProperty(ObservableList<User> value) {
        usersProperty.set(value);
    }

    public ObjectProperty usersPropertyProperty() {
        return usersProperty;
    }
    
    //Updates the users property with the users list sorted by their name.
    private void updateUsersProperty() {
        setUsersProperty(FXCollections.observableArrayList(users.stream().sorted(Comparator.comparing(User::getName)).collect(Collectors.toList())));
    }
    
    //Updates the card number - token pairs property with the cards list sorted by their card card number.
    private void updateCardsProperty() {
        setCardsProperty(FXCollections.observableArrayList(cards.stream().sorted(Comparator.comparing(CardTokenPair::getCard)).collect(Collectors.toList())));
    }
    
    //Gets the logged user
    public User getLoggedUser() {
        return loggedUser;
    }
    
    //Sets the logged user
    public void setLoggedUser(User user) {
        loggedUser = user;
    }

}

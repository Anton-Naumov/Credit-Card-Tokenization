package UtilityClasses;

import java.util.Random;

/*
    This class contains credit card number and it's coresponding token,
    and also has static methods for tokenizing and checking if a card
    number is valid.
*/
public class CardTokenPair {

    private String card;
    private String token;
    private final User cardOwner;

    public CardTokenPair(String card, String token, User cardOwner) {
        setCard(card);
        setToken(token);
        this.cardOwner = cardOwner;
    }

    public User getCardOwner() {
        return cardOwner;
    }

    public String getCard() {
        return card;
    }
    
    public final void setCard(String card) {
        if (card == null)
            this.card = "";
        else
            this.card = card;
    }

    public String getToken() {
        return token;
    }

    public final void setToken(String token) {
        if (token == null)
            this.token = "";
        else
            this.token = token;
    }

    @Override
    public String toString() {
        return String.format("card: %s, token: %s, owner: %s",
                             getCard(), getToken(), getCardOwner().getName());
    }    
    
    
    
    public static final int SIZE_OF_CARD = 16;
    public static final int SIZE_OF_TOKEN = 16;
    private static final Random rand = new Random();
    
    //This method returns random digit between start and end different than the digits in exclude.
    //For this method to work the numbers that are excluded should be in increasing order.
    public static int getRandomWithExclusion(int start, int end, int... exclude) {
        int random = start + rand.nextInt(end - start + 1 - exclude.length);
        
        for (int ex : exclude) {
            if (random < ex) {
                break;
            }
            random++;
        }
        
        return random;
    }
    
    //This method takes credit card number and returns token that does't have 3, 4, 5 or 6
    //as a first digit, every digit in the token exept the last four is different
    //then the digit on the same position in the card number and the last four digits of the
    //token are the same as the last four digits in the credit card number.
    public static String tokenize(String cardNumber) {
        StringBuilder token = new StringBuilder(SIZE_OF_TOKEN);
        
        int currIdx = 0;
        token.append(getRandomWithExclusion(0, 9, 3, 4, 5, 6, cardNumber.charAt(currIdx) - '0')); // creates the fisrtDigit of token
        ++currIdx;
        
        while (currIdx < SIZE_OF_TOKEN - 4) { // creates the digits between the first and last four digits of the token
            token.append(getRandomWithExclusion(0, 9, cardNumber.charAt(currIdx) - '0'));
            ++currIdx;
        }
        while (currIdx < SIZE_OF_TOKEN) { // creates the last four digits of the token
            token.append(cardNumber.charAt(currIdx) - '0');
            ++currIdx;
        }
        if (String.valueOf(token).chars().map(letter -> letter - '0').sum() % 10 == 0) { // if the sum of all digits mod 10 is 0 change the 12th digit
            int digitInToken = token.charAt(11) - '0';
            int digitInCard = cardNumber.charAt(11) - '0';
            token.setCharAt(11, (char)(getRandomWithExclusion(0, 9, Integer.min(digitInToken, digitInCard), Integer.max(digitInToken, digitInCard)) + '0'));   
        }                    
            
        return token.toString();
    }

    //This method returns true if the given credit card number is valid according to the Luhn formula.
    public static boolean isCardNumberValid(String cardNumber) {
        if (cardNumber == null || cardNumber.length() != SIZE_OF_CARD || 
            !(cardNumber.charAt(0) - '0' >= 3 && cardNumber.charAt(0) - '0' <= 6)){
            return false;
        }
        char[] temporaryStr = cardNumber.toCharArray();
        int saveDigit;
        for (int i = 0; i < SIZE_OF_CARD; i = i + 2) {
            
            saveDigit = (temporaryStr[i] - '0') * 2;
            temporaryStr[i] = (char)('0' + ((saveDigit >= 10) ? (saveDigit - 9) : saveDigit));         
        }
        return String.valueOf(temporaryStr).chars().map(letter -> letter - '0').sum() % 10 == 0;
    }
    
}

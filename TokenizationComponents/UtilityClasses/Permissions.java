package UtilityClasses;

/*
    This class determines what are the user's permissions.
*/
public enum Permissions {
    REGISTER_TOKEN("Can register tokens."),
    READ_CARD("Can read cards."),
    REGISTER_TOKEN_AND_READ_CARD("Can register tokens and read cards");
    
    private final String permission;

    private Permissions(String permission) {
        this.permission = permission;
    }

    @Override
    public String toString() {
        return String.format("%s", permission);
    }
}

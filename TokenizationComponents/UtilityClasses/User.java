package UtilityClasses;

/*
    This class describes user by his name, password and permissions.
*/
public class User {

    private String name;
    private String password;
    private Permissions permissions;

    public User(String name, String password, Permissions permissions) {
        setName(name);
        setPassword(password);
        setPermissions(permissions);
    }
    
    public String getName() {
        return name;
    }

    public final void setName(String name) {
        if (name == null)
            this.name = "";
        else
            this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public final void setPassword(String password) {
        if (password == null)
            this.password = "";
        else
            this.password = password;
    }

    public Permissions getPermissions() {
        return permissions;
    }

    public final void setPermissions(Permissions permissions) {
        this.permissions = permissions;
    }

    @Override
    public String toString() {
        return String.format("name: %s, password: %s, permissions: %s",
                             getName(), getPassword(), getPermissions());
    }
    
}

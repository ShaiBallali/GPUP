package users;

import javafx.beans.property.SimpleStringProperty;

public class TableUser {
    private final SimpleStringProperty username;
    private final SimpleStringProperty userType;

    public TableUser(String username, String userType){
        this.username = new SimpleStringProperty(username);
        this.userType = new SimpleStringProperty(userType);
    }


    public String getUsername()
    {
        return username.get();
    }
    public String getUserType() { return userType.get(); }
}

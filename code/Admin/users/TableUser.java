package users;

import javafx.beans.property.SimpleStringProperty;

public class TableUser {
    private SimpleStringProperty username;
    private SimpleStringProperty userType;

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

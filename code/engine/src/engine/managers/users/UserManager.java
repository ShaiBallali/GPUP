package engine.managers.users;

import dto.dtoServer.users.User;
import dto.dtoServer.users.UsersList;
import java.util.*;

public class UserManager {
    private final Map<String,String> userName2userType;

    public UserManager() {
        this.userName2userType = new HashMap<>();
    }

    public synchronized void addUser(String username, String userType) {
        this.userName2userType.put(username, userType);}

    public synchronized UsersList getUsers() {
        UsersList usersList = new UsersList(userName2userType.size());

        for (Map.Entry<String,String> entry : userName2userType.entrySet()){
            String userName = entry.getKey();
            String userType = entry.getValue();
            User user = new User(userName, userType);
            usersList.addUser(user);
        }
        return usersList;
    }

    public boolean isUserExists(String username) {
        return this.userName2userType.containsKey(username);
    }
}

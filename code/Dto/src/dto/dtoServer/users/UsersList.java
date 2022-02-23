package dto.dtoServer.users;

public class UsersList {

    public User[] Users;
    int logSize;

    public UsersList(int size) {
        logSize = 0;
        Users = new User[size];
    }

    public void addUser (User userDetails) {
        Users[logSize++] = userDetails;
    }

    public User[] getUsers () {
        return this.Users;
    }

    public int getLogSize() {
        return logSize;
    }


}

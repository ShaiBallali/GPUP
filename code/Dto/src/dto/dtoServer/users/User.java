package dto.dtoServer.users;

public class User {
    private String name;
    private String type;
   // int numOfThreadIfWorker;

    public User(String name, String type) {
        this.name = name;
        this.type = type;
       // this.numOfThreadIfWorker = numOfThreadIfWorker;
    }

    public String getName(){
        return name;
    }

    public String getType(){
        return type;
    }
}

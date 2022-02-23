package dto.dtoServer.graphAction;

import dto.dtoServer.users.User;

import java.util.ArrayList;
import java.util.List;

public class CirclePath { // DTO for option 6 (detecting a circle)
    private String[] path;
    private int logSize;

    public CirclePath(List<String> path) {
        this.path = new String[path.size()];
        this.logSize = 0;
        for (String name : path) {
            this.path[logSize++] = name;
        }
    }

    public List<String> getPath () {
        List<String> newPath = new ArrayList<>();
        for(int i = 0; i< logSize; ++i){
            newPath.add(path[i]);
        }
        return newPath;
    }

}

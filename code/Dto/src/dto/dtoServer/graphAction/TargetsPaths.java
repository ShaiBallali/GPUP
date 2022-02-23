package dto.dtoServer.graphAction;

import java.util.ArrayList;
import java.util.List;

public class TargetsPaths {
    private String [][] allPaths;
    private int logicSize;

    public TargetsPaths (List<List<String>> allPaths) {
        this.logicSize = 0;
        int numOfPath = allPaths.size();

        if (numOfPath != 0) {
            this.allPaths = new String[numOfPath][];
            for (List<String> path : allPaths) {
                int i = 0;
                int pathLength = path.size();
                this.allPaths[logicSize] = new String[pathLength];
                for (String name : path) {
                    this.allPaths[logicSize][i++] = name;
                }
                logicSize++;
            }
        }
    }

    public List<List<String>> getAllPaths () {

            if (logicSize!=0) {
                List<List<String>> result = new ArrayList<>();

                for (String[] path : this.allPaths) {
                    List<String> pathList = new ArrayList<>();
                    for (String name : path) {
                        pathList.add(name);
                    }
                    result.add(pathList);
                }
                return result;
            }
            else
                return null;
    }
}

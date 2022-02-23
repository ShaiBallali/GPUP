package engine.graph;
import dto.dtoServer.graph.GraphDetails;
import dto.dtoServer.graph.TargetDetails;
import dto.dtoServer.graph.TaskPrice;
import dto.dtoServer.graphAction.TargetsPaths;
import dto.dtoServer.graphAction.WhatIfDetails;
import dto.enums.Position;
import dto.enums.DependencyType;
import engine.graph.target.Target;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Graph implements Cloneable {

    public String                    createdBy;
    public String                    name;
    public Map<String, Target>       graphData;  // Name -> target
    public Map <String, SerialSet>   serialSets; // Name -> Serial sets
    public Map <String, Integer>     task2price;
    public Map<String, Set<String>>  name2dependsOn;  // Name -> depends on
    public Map <String, Set<String>> name2dependsOnFromLastRunning;

    // ----------------------------------------------------------- //

    public Graph (String name,Map<String, Target> graphData , Map<String, Set<String>> name2dependsOn, Map <String, SerialSet> serialSets, Map <String, Integer> task2price) {
    this.name = name;
    this.graphData = new HashMap<>();
    this.name2dependsOn = new HashMap<>();
    this.serialSets = new HashMap<>();
    this.name2dependsOnFromLastRunning = new HashMap<>();
    this.task2price = new HashMap<>();

    this.graphData.putAll(graphData);
    this.name2dependsOn.putAll(name2dependsOn);
    this.serialSets.putAll(serialSets);
    this.task2price.putAll(task2price);
    }

    public GraphDetails getGraphDetails () {
        int logSize;

        TaskPrice[] taskPrices = new TaskPrice[task2price.size()];
        logSize = 0;
        for (Map.Entry<String, Integer> entry : task2price.entrySet()) {
            String name = entry.getKey();
            int price = entry.getValue();
            taskPrices[logSize++] = new TaskPrice(name,price);
        }

        Map <Position, Integer> positionCounter = positionCount(); // Position -> count

        TargetDetails[] targetDetails = new TargetDetails[graphData.size()];
        String[] targetsNames = new String[graphData.size()];
        logSize = 0;
        for ( Map.Entry<String, Target> entry : graphData.entrySet()) {
            String targetName = entry.getKey();
            Target target = entry.getValue();

            targetsNames[logSize] = targetName;

            Set<String> allDependsOnNames = new HashSet<>();
            Set<String> allRequiredFor = new HashSet<>();
            getWhatIfDependsOn(targetName, allDependsOnNames);
            allDependsOnNames.remove(targetName);
            getWhatIfRequiredFor(targetName, allRequiredFor);
            allRequiredFor.remove(targetName);

            targetDetails[logSize] = new TargetDetails(
                    targetName,
                    getPosition(targetName).toString(),
                    getTargetGeneralInfo(targetName),
                    allDependsOnNames.size(),
                    target.getNumOfDirectDependOn(),
                    allRequiredFor.size(),
                    target.getNumOfDirectRequiredFor(),
                    target.getMySerialSets().size()
            );

            logSize++;
        }
        return new GraphDetails(this.name, this.createdBy, targetCount(), positionCounter.get(Position.ROOT) ,positionCounter.get(Position.MIDDLE), positionCounter.get(Position.LEAF),positionCounter.get(Position.INDEPENDENT), taskPrices, targetDetails, targetsNames);
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public int targetCount() {
        return graphData.size();
    }                 // Return amount of targets

    // return num of appearances of each position
    public Map<Position, Integer> positionCount () {                      // Position -> count
        PositionCounter positionCounter = new PositionCounter();

        for (Map.Entry<String, Target> entry : graphData.entrySet()) {

            Position position = calcPositionOfTarget(entry.getValue().getNumOfDirectDependOn(),entry.getValue().getNumOfDirectRequiredFor());   // Check target position (is it a leaf? a middle?...)

            switch (position) {
                case INDEPENDENT:
                    positionCounter.increment(Position.INDEPENDENT);      // Incrementing the counter of the position
                    break;
                case LEAF:
                    positionCounter.increment(Position.LEAF);
                    break;
                case ROOT:
                    positionCounter.increment(Position.ROOT);
                    break;
                case MIDDLE:
                    positionCounter.increment(Position.MIDDLE);
                    break;
            }
        }
        return positionCounter.getPositionCounter();
    }

    public Position calcPositionOfTarget (int numOfDependOn, int numOfRequiredFor) {

        if ( numOfDependOn == 0 && numOfRequiredFor == 0 ) { // Checking which position the target is in
            return Position.INDEPENDENT ;
        }
        else if ( numOfDependOn == 0 ) {
            return Position.LEAF;
        }
        else if ( numOfRequiredFor == 0 ) {
            return Position.ROOT;
        }
        else {
            return Position.MIDDLE;
        }
    }

    public boolean isInGraph ( String targetName ) {        // Check if a certain target is in graph
        for (Map.Entry<String, Target> entry : graphData.entrySet()) {
            Target target = entry.getValue();

            if (targetName.equals(target.getName())) {
                return true;
                }
            }
        return false;
    };

    public Position getPosition (String targetName) {
        return calcPositionOfTarget(graphData.get(targetName).getNumOfDirectDependOn(), graphData.get(targetName).getNumOfDirectRequiredFor());
    }

    public Set<String> getTargetRequiredFor(String targetName) {
        return graphData.get(targetName).getRequiredFor();
    }

    public String getTargetGeneralInfo ( String targetName ) {
        return graphData.get(targetName).getGeneralInfo();
    }

    private List<List<String>> findAllPaths (String srcName, String dstName ) {
        List<List<String>> allPaths = new ArrayList<>();
        Set<String> visited         = new LinkedHashSet<>();

        allPaths.add(new ArrayList<String>());

        findAllPathsRec(graphData.get(srcName), graphData.get(dstName), visited, allPaths);

        allPaths.remove(allPaths.size()-1);

        return allPaths;
    }

    // Recursive method to find all paths between 2 targets
    private void findAllPathsRec (Target src, Target des, Set<String> visited, List<List<String>> allPaths){
        if (src.equals(des)) {
            update(visited, allPaths.get(allPaths.size()-1));

            allPaths.get(allPaths.size()-1).add(src.getName());

            allPaths.add(new ArrayList<String>());

            return;
        }
        visited.add(src.getName());

        for(String targetName: src.getDependsOn()){
            if(!visited.contains(targetName)){
                findAllPathsRec(graphData.get(targetName), des, visited, allPaths);
            }
        }

        visited.remove(src.getName());
    }

    // The function adds all the targets from visited set to path list
    private void update( Set<String> visited, List<String > path){
        for (String targetName :visited)
            path.add(targetName);
    }

    public WhatIfDetails findWhatIfTargets (String targetName, DependencyType dependencyType) {
        if (!isInGraph(targetName)) {
            throw new NoSuchElementException("No target named " + targetName + " was found.");             // Trying to get details from a target not in graph
        }

        Set <String> whatIfTargets = new HashSet<>();

        if ( dependencyType == DependencyType.REQUIRED_FOR ){
           getWhatIfRequiredFor(targetName, whatIfTargets);
        } else {
            getWhatIfDependsOn(targetName, whatIfTargets);
        }
        whatIfTargets.remove(targetName);

        return new WhatIfDetails(whatIfTargets);
    }

    public void getWhatIfDependsOn(String targetName, Set<String> allDependsOnNames) {
        Set<String> dependsOn = graphData.get(targetName).getDependsOn();

        if (allDependsOnNames.contains(targetName)) {
            return;
        }
        allDependsOnNames.add(targetName);
        for (String name : dependsOn) {
            getWhatIfDependsOn(name, allDependsOnNames);
        }
    }

    public void getWhatIfRequiredFor(String targetName, Set<String> allRequiredFor) {
        Set<String> requiredFor = graphData.get(targetName).getRequiredFor();

        if (allRequiredFor.contains(targetName)) {
            return;
        }
        allRequiredFor.add(targetName);
        for (String name : requiredFor) {
            getWhatIfRequiredFor(name, allRequiredFor);
        }
    }

    // Find paths between two targets (depends on the dependency given)
    public TargetsPaths findPaths (String src, String dst, DependencyType dependencyType) throws NoSuchElementException, IllegalArgumentException {
        if (!isInGraph(src) && !isInGraph(dst) ) {
            // In case both source and destination are not in graph
            throw new NoSuchElementException("Src named " + src + " and " + "dst named " + dst + " does not exist in graph.");
        }
        // In case source is not in graph
        if (!isInGraph(src)) {
            throw new NoSuchElementException("Source named " + src + " does not exist in graph.");
        }
        // In case destination is not in graph
        if (!isInGraph(dst)) {
            throw new NoSuchElementException("Destination named " + dst + " does not exist in graph.");
        }
        // In case source equals destination
        if (src.equals(dst)) {
            throw new IllegalArgumentException("Source and destination must be different.");
        }
        // Different dependency types only reverses the order
        if (dependencyType.equals(DependencyType.REQUIRED_FOR)) {
            String temp = dst;
            dst = src;
            src = temp;
        }

        List<List<String>> paths = findAllPaths(src, dst);

        if (dependencyType.equals(DependencyType.REQUIRED_FOR)) {
            for (List<String> list : paths) {
                Collections.reverse(list);
            }
        }

        return new TargetsPaths(paths);
    }

    // Check if a certain target is in a circle
    public dto.dtoServer.graphAction.CirclePath detectCircle (String targetName) throws NoSuchElementException {

        if (!isInGraph(targetName)) {
            // In case no such target name exists
            throw new NoSuchElementException(targetName + " does not exist in graph.");
        }

        List <String> path      = new ArrayList<>(); // Creating the path (if exists)
        Set<String> dependsOn   = graphData.get(targetName).getDependsOn();

        for (String name: dependsOn) { // First iteration must be out of the recursion...
            detectCircleHelper(targetName, graphData.get(name), path, new HashSet<String>());
            if (path.size() != 0) {
                path.add(targetName);
                return new dto.dtoServer.graphAction.CirclePath(path);
            }
        }
        return new dto.dtoServer.graphAction.CirclePath(path);
    }

    // Recursive method to detect a circle
    private void detectCircleHelper (String dstName, Target src, List<String> path, Set<String> visited) {
        // If the current target has no dependsOn - it means there is no circle
        if(src.getDependsOn().isEmpty()) {
            return;
        }

        // If we meet the same name, we just closed a circle.
        else if (dstName.equals(src.getName())){
            path.add(src.getName());
        }
        else {
            for (String name: src.getDependsOn()) {

                if (visited.contains(name)) return;
                else visited.add(name);

                detectCircleHelper(dstName, graphData.get(name), path, visited);

                if (path.size() != 0) {
                    path.add(src.getName());
                    return;
                }
            }
        }
    }

    public void createGraphviz(String path, String fileName) {
        try {
            path = path + "/" + fileName;
            FileWriter graphFile = new FileWriter(path + ".viz");
            writeToFile(graphFile);
            graphFile.close();
            String[] command = {"cmd.exe", "/c", "dot -Tpng " + path + ".viz" + " -o " + path + ".png"};
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.start();
        } catch (IOException e) {
        }
    }

    private void writeToFile(FileWriter file) throws IOException {
        int count = 0;
        file.write("digraph {\n");
        for (Map.Entry<String, Set<String>> entry : name2dependsOn.entrySet() ) {
            String targetName = entry.getKey();
            Set<String> dependsOnList = entry.getValue();
            if(!dependsOnList.isEmpty()){
                file.write(targetName);
                file.write(" -> {");
                for(String targetDep: dependsOnList){
                    count++;
                    file.write(targetDep);
                    if(count < dependsOnList.size())
                        file.write(", ");
                }
                count = 0;
                file.write("}\n");
            }
        }
        for (Map.Entry<String, Set<String>> entry : name2dependsOn.entrySet() ) {
            String targetName = entry.getKey();
            Set<String> dependsOnList = entry.getValue();
            if (graphData.get(targetName).getRequiredFor().isEmpty() && dependsOnList.isEmpty()) {
                file.write(targetName);
                file.write(" -> {}");
            }
        }
        file.write("}");
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Graph graph = (Graph) o;
        return Objects.equals(graphData, graph.graphData) && Objects.equals(name2dependsOn, graph.name2dependsOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(graphData, name2dependsOn);
    }
}


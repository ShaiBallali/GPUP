package graphs;

import dto.dtoServer.graph.TargetDetails;
import dto.dtoServer.graph.TaskPrice;
import dto.graph.TargetDetailsJavaFX;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import java.util.HashSet;
import java.util.Set;

public class TableGraph {

    private final SimpleStringProperty name;
    private final SimpleStringProperty createdBy;
    private final SimpleIntegerProperty targetAmount;
    private final SimpleIntegerProperty rootCount;
    private final SimpleIntegerProperty middleCount;
    private final SimpleIntegerProperty leafCount;
    private final SimpleIntegerProperty independentsCount;
    private final TaskPrice[] taskPrices;
    private final TargetDetails[] targetDetails;
    private final String[] targetNames;

    public TableGraph(String name, String createdBy, int targetAmount, int rootCount, int middleCount, int leafCount,
                      int independentCount, TaskPrice[] taskPrices, TargetDetails[] targetDetails, String[] targetNames){
        this.name = new SimpleStringProperty(name);
        this.createdBy = new SimpleStringProperty(createdBy);
        this.targetAmount = new SimpleIntegerProperty(targetAmount);
        this.rootCount = new SimpleIntegerProperty(rootCount);
        this.middleCount = new SimpleIntegerProperty(middleCount);
        this.leafCount = new SimpleIntegerProperty(leafCount);
        this.independentsCount = new SimpleIntegerProperty(independentCount);
        this.taskPrices = taskPrices;
        this.targetDetails = targetDetails;
        this.targetNames = targetNames;
    }

    public String getGraphName(){
        return name.get();
    }
    public String getCreatedBy(){return createdBy.get();}
    public int getTargetAmount() {return targetAmount.get();}
    public int getRootCount() {return rootCount.get();}
    public int getMiddleCount() {return middleCount.get();}
    public int getLeafCount() {return leafCount.get();}
    public int getIndependentsCount() {return independentsCount.get();}
    public TaskPrice[] getTaskPrices() {return taskPrices;}
    public TargetDetails[] getTargetDetails() {return targetDetails;}
    public String[] getTargetNames() {return targetNames;}
    public Set<TargetDetailsJavaFX> getTargetDetailsTable() {
        Set<TargetDetailsJavaFX> targetDetailsTableList = new HashSet<>();
        for (TargetDetails targetDetails1 : targetDetails){
            TargetDetailsJavaFX curr = new TargetDetailsJavaFX(targetDetails1.getName(), targetDetails1.getPosition(), targetDetails1.getGeneralInfo(),
                    targetDetails1.getAllDependsOn(), targetDetails1.getDirectDependsOn(), targetDetails1.getDirectRequiredFor(),
                    targetDetails1.getAllRequiredFor());
            targetDetailsTableList.add(curr);
        }
        return targetDetailsTableList;
    }




}

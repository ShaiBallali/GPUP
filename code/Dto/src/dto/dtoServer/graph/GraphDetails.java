package dto.dtoServer.graph;

import dto.graph.TargetDetailsJavaFX;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GraphDetails {
    public String name;
    public String createdBy;
    public int targetAmount;
    public int rootCount;
    public int middleCount;
    public int leafCount;
    public int independentsCount;
    private String[] targetsNames;
    public TaskPrice[] taskPrices;
    public TargetDetails[] targetDetails;


    public GraphDetails(String name, String createdBy, int targetAmount, int rootCount, int middleCount, int leafCount, int independentsCount, TaskPrice[] taskPrices, TargetDetails[] targetDetails, String[] targetsNames ) {
        this.name = name;
        this.createdBy = createdBy;
        this.targetAmount = targetAmount;
        this.rootCount = rootCount;
        this.middleCount = middleCount;
        this.leafCount = leafCount;
        this.independentsCount = independentsCount;
        this.taskPrices = taskPrices;
        this.targetDetails = targetDetails;
        this.targetsNames = targetsNames;
    }

    public String getName() {
        return name;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Integer getTargetAmount() {
        return targetAmount;
    }

    public Integer getRootCount () {
        return rootCount;
    }

    public Integer getLeafCount () {
        return leafCount;
    }

    public Integer getMiddleCount () {
        return middleCount;
    }

    public Integer getIndependentCount () {
        return independentsCount;
    }

    public TaskPrice[] getTaskPrices() {
        return taskPrices;
    }

    public TargetDetails[] getTargetDetails() {return targetDetails;}

    public String[] getTargetsNames() {return targetsNames;}


}







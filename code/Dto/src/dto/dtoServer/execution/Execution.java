package dto.dtoServer.execution;

import dto.enums.ExecutionStatus;
import dto.enums.TaskName;

public class Execution {
    private String name;
    private String createdBy;
    private String graphName;
    private TaskName taskName;
    private int targetAmount;
    private int rootCount;
    private int middleCount;
    private int leafCount;
    private int independentsCount;
    private int pricePerTarget;
    private int totalPrice;
    private int totalWorkers;
    private ExecutionStatus executionStatus;

    public Execution(String name, String createdBy, String graphName, TaskName taskName, int targetAmount, int rootCount, int middleCount, int leafCount, int independentsCount,int pricePerTarget, int totalPrice, int totalWorkers, ExecutionStatus executionStatus) {
        this.name = name;
        this.createdBy = createdBy;
        this.graphName = graphName;
        this.taskName = taskName;
        this.targetAmount = targetAmount;
        this.rootCount = rootCount;
        this.middleCount = middleCount;
        this.leafCount = leafCount;
        this.independentsCount = independentsCount;
        this.pricePerTarget = pricePerTarget;
        this.totalPrice = totalPrice;
        this.totalWorkers = totalWorkers;
        this.executionStatus = executionStatus;
    }

    public String getName() {
        return name;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getGraphName() {
        return graphName;
    }

    public TaskName getTaskName() {
        return taskName;
    }

    public int getTargetAmount() {
        return targetAmount;
    }

    public int getRootCount() {
        return rootCount;
    }

    public int getMiddleCount() {
        return middleCount;
    }

    public int getLeafCount() {
        return leafCount;
    }

    public int getIndependentsCount() {
        return independentsCount;
    }

    public int getPricePerTarget() {
        return pricePerTarget;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public int getTotalWorkers() {
        return totalWorkers;
    }

    public ExecutionStatus getExecutionStatus() {
        return executionStatus;
    }
}

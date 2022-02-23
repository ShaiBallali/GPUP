package dto.dtoServer.execution;

import dto.enums.ExecutionStatus;
import dto.enums.TaskName;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.Objects;

public class TableClientExecution {

    private SimpleStringProperty name;
    private SimpleStringProperty createdBy;
    private SimpleStringProperty taskType;
    private SimpleIntegerProperty targetAmount;
    private SimpleIntegerProperty rootCount;
    private SimpleIntegerProperty middleCount;
    private SimpleIntegerProperty leafCount;
    private SimpleIntegerProperty independentCount;
    private SimpleIntegerProperty totalPrice;
    private SimpleIntegerProperty totalWorkers;
    private SimpleStringProperty status;
    private SimpleBooleanProperty isRegistered;

    public TableClientExecution(String name, String createdBy, TaskName taskType, int targetAmount, int rootCount, int middleCount, int leafCount, int independentCount, int totalPrice,
                                int totalWorkers, ExecutionStatus status, boolean isRegistered) {
        this.name = new SimpleStringProperty(name);
        this.createdBy = new SimpleStringProperty(createdBy);
        this.taskType = new SimpleStringProperty(taskType.toString());
        this.targetAmount = new SimpleIntegerProperty(targetAmount);
        this.rootCount = new SimpleIntegerProperty(rootCount);
        this.middleCount = new SimpleIntegerProperty(middleCount);
        this.leafCount = new SimpleIntegerProperty(leafCount);
        this.independentCount = new SimpleIntegerProperty(independentCount);
        this.totalPrice = new SimpleIntegerProperty(totalPrice);
        this.totalWorkers = new SimpleIntegerProperty(totalWorkers);
        this.status = new SimpleStringProperty(status.toString());
        this.isRegistered = new SimpleBooleanProperty(isRegistered);
    }

    public String getExecutionName(){
        return name.get();
    }

    public String getCreatedBy() {
        return createdBy.get();
    }

    public String getTaskType() {
        return taskType.get();
    }

    public int getTargetAmount() {
        return targetAmount.get();
    }

    public int getMiddleCount() {
        return middleCount.get();
    }

    public int getRootCount() {
        return rootCount.get();
    }

    public int getLeafCount() {
        return leafCount.get();
    }

    public int getIndependentCount() {
        return independentCount.get();
    }

    public int getTotalPrice() {
        return (totalPrice.get()/targetAmount.get());
    }

    public int getTotalWorkers() {
        return totalWorkers.get();
    }

    public String getName() {
        return name.get();
    }

    public String getStatus() {
        return status.get();
    }

    public boolean getIsRegistered() { return isRegistered.get(); }

}

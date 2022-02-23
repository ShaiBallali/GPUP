package dto.dtoServer.execution;

import dto.enums.ExecutionStatus;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.HashMap;
import java.util.Map;

public class TableExecution {

    private SimpleStringProperty name;
    private SimpleStringProperty createdBy;
    private SimpleStringProperty graphName;
    private SimpleIntegerProperty targetAmount;
    private SimpleIntegerProperty rootCount;
    private SimpleIntegerProperty middleCount;
    private SimpleIntegerProperty leafCount;
    private SimpleIntegerProperty independentCount;
    private SimpleIntegerProperty totalPrice;
    private SimpleIntegerProperty totalWorkers;
    private SimpleStringProperty status;
    private SimpleBooleanProperty isPlayed;
    private SimpleBooleanProperty isPaused;
    private SimpleBooleanProperty isStopped;


    public TableExecution(String name, String createdBy, String graphName, int targetAmount, int rootCount, int middleCount, int leafCount, int independentCount, int totalPrice,
                          int totalWorkers, ExecutionStatus status) {
        this.name = new SimpleStringProperty(name);
        this.createdBy = new SimpleStringProperty(createdBy);
        this.graphName = new SimpleStringProperty(graphName);
        this.targetAmount = new SimpleIntegerProperty(targetAmount);
        this.rootCount = new SimpleIntegerProperty(rootCount);
        this.middleCount = new SimpleIntegerProperty(middleCount);
        this.leafCount = new SimpleIntegerProperty(leafCount);
        this.independentCount = new SimpleIntegerProperty(independentCount);
        this.totalPrice = new SimpleIntegerProperty(totalPrice);
        this.totalWorkers = new SimpleIntegerProperty(totalWorkers);
        this.status = new SimpleStringProperty(status.toString());

        isPlayed = new SimpleBooleanProperty(false);
        isPaused = new SimpleBooleanProperty(false);
        isStopped = new SimpleBooleanProperty(false);


    }

    public void setIsPaused(boolean isPaused) {
        this.isPaused.set(isPaused);
    }

    public void setIsPlayed(boolean isPlayed) {
        this.isPlayed.set(isPlayed);
    }

    public void setIsStopped(boolean isStopped) {
        this.isStopped.set(isStopped);
    }

    public boolean getIsPaused(){
        return isPaused.get();
    }
    public boolean getIsStopped(){
        return isStopped.get();
    }
    public boolean getIsPlayed(){
        return isPlayed.get();
    }

    public String getExecutionName(){
        return name.get();
    }

    public String getCreatedBy() {
        return createdBy.get();
    }

    public String getGraphName() {
        return graphName.get();
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
        return totalPrice.get();
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
}

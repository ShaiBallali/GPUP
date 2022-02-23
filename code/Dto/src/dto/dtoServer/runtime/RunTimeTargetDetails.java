package dto.dtoServer.runtime;

import dto.enums.RunResult;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.Objects;


// position - LEAF, INDEPENDENT, MIDDLE, ROOT
// state - FROZEN, SKIPPED, WAITING, IN_PROCESS, FINISHED
// runResult - SUCCESS, SUCCESS_WITH_WARNING, FAILURE, SKIPPED, WITHOUT

public class RunTimeTargetDetails {
    private  String name, position, serialSetsNames, state, runResult;
    private  String DependentsThatBlock, DependentsThatFailed;
    private  long startWaitingTime, totalWaitingTime, startProcessingTime, totalProcessingTime, endProcessingTime, totalProcessTime;
    private  boolean isFinishedUpdating;

    public RunTimeTargetDetails (String name) {
        this.name = name;
        this.position = "";
        this.serialSetsNames = "";
        this.state = "";
        this.runResult = "";
        this.DependentsThatBlock = "";
        this.DependentsThatFailed = "";
        this.startWaitingTime = 0;
        this.startProcessingTime = 0;
        this.endProcessingTime = 0;
        this.totalProcessTime = 0;
        this.totalWaitingTime = 0;
        this.totalProcessingTime = 0;
        this.isFinishedUpdating = false;
    }

    public synchronized void setIsFinishedUpdating(boolean isFinishedUpdating) {
        this.isFinishedUpdating = isFinishedUpdating;
    }

    public synchronized void init(String position, String serialSetsNames, String state, String runResult, String dependentsThatBlock) {
        this.position = position;
        this.serialSetsNames = serialSetsNames;
        this.state = state;
        this.runResult = runResult;
        this.DependentsThatBlock = dependentsThatBlock;
    }

    // setters:
    public void setName(String name) {
        this.name = name;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setSerialSetsNames(String serialSetsNames) {
        this.serialSetsNames = serialSetsNames;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setRunResult(String runResult) {
        this.runResult = runResult;
    }

    public void setDependentsThatBlock(String dependentsThatBlock) {
        DependentsThatBlock = dependentsThatBlock;
    }

    public void setDependentsThatFailed(String dependentsThatFailed) {
        DependentsThatFailed = dependentsThatFailed;
    }

    public void setTotalWaitingTime(long totalWaitingTime) {
        this.totalWaitingTime = totalWaitingTime;
    }

    public void setStartProcessingTime(long startProcessingTime) {
        this.startProcessingTime = startProcessingTime;
    }

    public void setTotalProcessingTime(long totalProcessingTime) {
        this.totalProcessingTime = totalProcessingTime;
    }

    public void setEndProcessingTime(long endProcessingTime) {
        this.endProcessingTime = endProcessingTime;
    }

    public void setTotalProcessTime(long totalProcessTime) {
        this.totalProcessTime = totalProcessTime;
    }

    public void setFinishedUpdating(boolean finishedUpdating) {
        isFinishedUpdating = finishedUpdating;
    }

    // getters:
    public synchronized String getName() {
        return this.name;
    }

    public synchronized String getPosition() {
        return this.position;
    }

    public synchronized String getSerialSetsNames() {
        return serialSetsNames;
    }

    public synchronized String getState() {
        return state;
    }

    public synchronized String getRunResult() {
        return runResult;
    }

    public synchronized String getDependentsThatBlock() {
        return DependentsThatBlock;
    }

    public synchronized  String getDependentsThatFailed() {
        return DependentsThatFailed;
    }

    public synchronized void setStartWaitingTime(long startWaitingTime) {
        this.startWaitingTime = startWaitingTime;
    }

    public synchronized void addToDependentsThatFailed (String targetName) {
        if ( !this.DependentsThatFailed.contains(targetName))
        {
            String First = this.DependentsThatFailed.isEmpty()? "" : ", ";
            this.DependentsThatFailed=  this.DependentsThatFailed.concat( First + targetName);
        }
    }

    public synchronized long getTotalProcessTime () {
        return totalProcessTime;
    }

    public synchronized boolean getIsFinishedUpdating() {
        return isFinishedUpdating;
    }

    public synchronized long getTotalWaitingTime () {
        this.totalWaitingTime = System.currentTimeMillis() - startWaitingTime;
        return totalWaitingTime;
    }

    public synchronized long getTotalProcessingTime () {
        totalProcessingTime= System.currentTimeMillis() - startProcessingTime;
        return totalProcessingTime;
    }

    public synchronized long getStartWaitingTime() {
        return startWaitingTime;
    }

    public synchronized long getStartProcessingTime() {
        return startProcessingTime;
    }

    public synchronized long getEndProcessingTime() {
        return endProcessingTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RunTimeTargetDetails)) return false;
        RunTimeTargetDetails that = (RunTimeTargetDetails) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

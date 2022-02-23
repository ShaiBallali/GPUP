package dto.dtoServer.runtime;

import dto.enums.ExecutionStatus;

import java.util.*;

public class RunTimeTaskDetails {
    private RunTimeTargetDetails [] runTimeTargetsDetails;
    private int logSize;
    private int sumOfTargets;
    private int sumOfCompletedTargets;
    private ExecutionStatus executionStatus;
    private int currRunningTargets;
    private int waitingTargets;
    private String message;

    public RunTimeTaskDetails (Map<String, Set<String>> name2dependsOn) {
        this.runTimeTargetsDetails = new RunTimeTargetDetails[name2dependsOn.size()];
        this.logSize = 0;
        this.sumOfTargets = 0;
        this.sumOfCompletedTargets = 0;
        this.message = "";
        this.executionStatus = ExecutionStatus.NEW;
        this.currRunningTargets = 0;
        this.waitingTargets = 0;

        name2dependsOn.forEach( (name, dependence) -> {
            runTimeTargetsDetails[logSize++] = new RunTimeTargetDetails(name);
        });
    }

    public synchronized void setMessage(Map<String ,String> message) {
        for (Map.Entry<String, String> entry : message.entrySet()) {
            this.message = this.message.concat("\n" + entry.getKey() + ":\n" + entry.getValue());
        }
    }

    public synchronized void setSumOfCompletedTargets ( int sumOfCompletedTargets) {
        this.sumOfCompletedTargets = sumOfCompletedTargets;
    }
    
    public synchronized void incrementSumOfCompletedTargetsBy1 () {
        this.sumOfCompletedTargets++;
    }

    public synchronized void setSumOfTargets ( int sumOfTargets ) {
        this.sumOfTargets = sumOfTargets;
    }

    public synchronized RunTimeTargetDetails getTargetByName (String name) {
        for (RunTimeTargetDetails runTimeTargetDetails : this.runTimeTargetsDetails) {
            if (runTimeTargetDetails.getName().equals(name)){
                return runTimeTargetDetails;
            }
        }
        return null;
    }

    public synchronized void initRunTimeTargetDetails(String name, String position, String serialSetsNames, String state, String runResult, String DependentsThatBlock){
        getTargetByName(name).init(position,serialSetsNames, state, runResult, DependentsThatBlock);
    }

    public synchronized Map<String, RunTimeTargetDetails> getRunTimeTargetsDetails () {
        Map<String, RunTimeTargetDetails > result = new HashMap<>();
        for (RunTimeTargetDetails runTimeTargetDetails : this.runTimeTargetsDetails) {
            result.put(runTimeTargetDetails.getName(), runTimeTargetDetails);
        }
        return result;
    }

    public RunTimeTargetDetails[] getRuntimeTargetDetails(){
        return runTimeTargetsDetails;
    }

    public synchronized String getMessage() {
        return this.message;
    }

    public synchronized int getSumOfTargets() {
        return this.sumOfTargets;
    }

    public synchronized int getSumOfCompletedTargets() {
        return this.sumOfCompletedTargets;
    }

    public synchronized ExecutionStatus getExecutionStatus() {
        return executionStatus;
    }

    public synchronized void setExecutionStatus(ExecutionStatus executionStatus) {
        this.executionStatus = executionStatus;
    }

    public void increaseCurrRunningBy1 () {
        this.currRunningTargets++;
    }

    public void decreaseCurrRunningBy1 () {
        this.currRunningTargets--;
    }

    public void increaseWaitingTargetsBy1 () {
        this.waitingTargets++;
    }

    public void decreaseWaitingTargetsBy1 () {
        this.waitingTargets--;
    }

    public int getCurrRunningTargets() {
        return currRunningTargets;
    }

    public int getWaitingTargets() {
        return waitingTargets;
    }
}

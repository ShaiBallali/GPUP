package engine;

import dto.dtoServer.worker.server2worker.BasicExecutionDetails;
import dto.enums.TaskName;

public class RegisteredExecutions {
    private String name;
    private TaskName taskName;
    private int price;
    private int numOfTargetIAlreadyPerformHere;

    // for simulation:
    private int targetProcessingTime;
    private boolean isRandomTime;
    private float successRate;
    private float warningRate;

    // for compilation:
    private String srcPath;
    private String dstPath;

    public RegisteredExecutions(BasicExecutionDetails basicExecutionDetails) {
        this.name = basicExecutionDetails.getName();
        this.taskName = basicExecutionDetails.getTaskName();
        this.price = basicExecutionDetails.getPrice();
        this.numOfTargetIAlreadyPerformHere = 0;
        this.targetProcessingTime = basicExecutionDetails.getTargetProcessingTime();
        this.isRandomTime = basicExecutionDetails.isRandomTime();
        this.successRate = basicExecutionDetails.getSuccessRate();
        this.warningRate = basicExecutionDetails.getWarningRate();
        this.srcPath = basicExecutionDetails.getSrcPath();
        this.dstPath = basicExecutionDetails.getDstPath();
    }

    public String getName() {
        return name;
    }

    public TaskName getTaskName() {
        return taskName;
    }

    public int getTargetProcessingTime() {
        return targetProcessingTime;
    }

    public boolean isRandomTime() {
        return isRandomTime;
    }

    public float getSuccessRate() {
        return successRate;
    }

    public float getWarningRate() {
        return warningRate;
    }

    public String getSrcPath() {
        return srcPath;
    }

    public String getDstPath() {
        return dstPath;
    }

    public int getPrice() {
        return price;
    }

    public void incrementTargetIAlreadyPerformBy1 () {
        this.numOfTargetIAlreadyPerformHere++;
    }

    public int getNumOfTargetIAlreadyPerformHere() {
        return numOfTargetIAlreadyPerformHere;
    }

    public int getTotalPriceFromThisExecution() {
        return this.numOfTargetIAlreadyPerformHere * this.price;
    }
}

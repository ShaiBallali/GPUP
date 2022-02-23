package dto.dtoServer.worker.server2worker;

import dto.enums.TaskName;

public class BasicExecutionDetails {
    private String name;
    private TaskName taskName;
    private int price;

    // for simulation:
    private int targetProcessingTime;
    private boolean isRandomTime;
    private float successRate;
    private float warningRate;

    // for compilation:
    private String srcPath;
    private String dstPath;

    public BasicExecutionDetails(String name, TaskName taskName, int price, int targetProcessingTime, boolean isRandomTime, float successRate, float warningRate, String srcPath, String dstPath) {
        this.name = name;
        this.taskName = taskName;
        this.price = price;
        this.targetProcessingTime = targetProcessingTime;
        this.isRandomTime = isRandomTime;
        this.successRate = successRate;
        this.warningRate = warningRate;
        this.srcPath = srcPath;
        this.dstPath = dstPath;
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

    public  int getPrice () {
        return this.price;
    }
}

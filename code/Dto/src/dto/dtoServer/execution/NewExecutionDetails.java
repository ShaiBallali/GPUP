package dto.dtoServer.execution;

import dto.enums.TaskName;
import jdk.nashorn.internal.objects.annotations.Setter;

import java.util.HashSet;
import java.util.Set;

public class NewExecutionDetails {
    // general details
    private String executionName;
    private String createdBy;
    private String graphName;
    private TaskName taskName;
    private String[] targetsToPerform;
    private int logSize;

    // for simulation:
    private int targetProcessingTime;
    private boolean isRandomTime;
    private float successRate;
    private float warningRate;

    // for compilation:
    private String srcPath;
    private String dstPath;


    public NewExecutionDetails(String executionName, String graphName, TaskName taskName) {
        this.executionName =executionName;
        this.createdBy = null;
        this.graphName = graphName;
        this.taskName = taskName;
    }

    public void setCreatedBy (String name) {
        this.createdBy = name;
    }

    public void initSimulationDetails(int targetProcessingTime, boolean isRandomTime, float successRate, float warningRate) {
        this.targetProcessingTime = targetProcessingTime;
        this.isRandomTime = isRandomTime;
        this.successRate = successRate;
        this.warningRate = warningRate;
    }

    public void initCompilationDetails(String srcPath, String dstPath) {
        this.srcPath = srcPath;
        this.dstPath = dstPath;
    }

    public void initTargetsToPerform(Set<String> targetsToPerform){
        this.targetsToPerform = new String[targetsToPerform.size()];
        this.logSize = 0;
        for (String targetToPerform : targetsToPerform) {
            this.targetsToPerform[logSize++] = targetToPerform;
        }
    }

    public String getExecutionName() {
        return executionName;
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

    public Set<String> getTargetsToPerform() {
        Set<String> result = new HashSet<>();
        for (String target : targetsToPerform) {
            result.add(target);
        }
        return result;
    }

    public int getTargetProcessingTime() {
        return targetProcessingTime;
    }

    public boolean getIsRandomTime() {
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
}

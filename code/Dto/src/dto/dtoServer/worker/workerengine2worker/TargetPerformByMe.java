package dto.dtoServer.worker.workerengine2worker;

import dto.enums.RunResult;
import dto.enums.TargetState;
import dto.enums.TaskName;

import java.util.Objects;

public class TargetPerformByMe {
    private String executionName;
    private TaskName taskName;
    private String targetName;
    private TargetState targetState;
    private RunResult runResult;
    private String logs;
    private int credit;

    public TargetPerformByMe(String executionName, TaskName taskName, String targetName, TargetState targetState, RunResult result, int credit) {
        this.executionName = executionName;
        this.taskName = taskName;
        this.targetName = targetName;
        this.targetState = targetState;
        this.runResult = result;
        this.logs = "";
        this.credit = credit;
    }

    public void setTargetState(TargetState targetState) {
        this.targetState = targetState;
    }

    public void setRunResult(RunResult runResult) {
        this.runResult = runResult;
    }

    public void setLogs(String logs) {
        this.logs = logs;
    }

    public String getExecutionName() {
        return executionName;
    }

    public TaskName getTaskName() {
        return taskName;
    }

    public String getTargetName() {
        return targetName;
    }

    public TargetState getTargetState() {
        return targetState;
    }

    public RunResult getRunResult() {
        return runResult;
    }

    public String getLogs() {
        return logs;
    }

    public int getCredit() {
        return credit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TargetPerformByMe)) return false;
        TargetPerformByMe that = (TargetPerformByMe) o;
        return credit == that.credit && Objects.equals(executionName, that.executionName) && taskName == that.taskName && Objects.equals(targetName, that.targetName) && targetState == that.targetState && runResult == that.runResult && Objects.equals(logs, that.logs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(executionName, taskName, targetName, targetState, runResult, logs, credit);
    }
}

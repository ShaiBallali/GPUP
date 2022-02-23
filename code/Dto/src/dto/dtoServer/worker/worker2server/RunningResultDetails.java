package dto.dtoServer.worker.worker2server;

import dto.enums.RunResult;

public class RunningResultDetails {
    private String targetName;
    private String executionName;
    private String logs;
    private RunResult runResult;

    public RunningResultDetails(String targetName, String executionName, String logs, RunResult runResult) {
        this.targetName = targetName;
        this.executionName = executionName;
        this.logs = logs;
        this.runResult = runResult;
    }

    public String getTargetName() {
        return targetName;
    }

    public String getLogs() {
        return logs;
    }

    public RunResult getRunResult() {
        return runResult;
    }

    public String getExecutionName() {
        return executionName;
    }
}

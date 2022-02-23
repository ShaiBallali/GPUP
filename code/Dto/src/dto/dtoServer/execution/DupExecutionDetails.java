package dto.dtoServer.execution;

import dto.enums.RunType;

public class DupExecutionDetails {
    private String srcExecutionName;
    private RunType runType;
    private String createdBy;

    public DupExecutionDetails(String srcExecutionName, RunType runType) {
        this.srcExecutionName = srcExecutionName;
        this.runType = runType;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getSrcExecutionName() {
        return srcExecutionName;
    }

    public RunType getRunType() {
        return runType;
    }

    public String getCreatedBy () {
        return this.createdBy;
    }
}




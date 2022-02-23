package dto.dtoServer.worker.server2worker;

import dto.enums.TaskName;

public class TargetDetails {
    private TaskName taskName;
    private String  executionName;
    private String name;
    private String generalInfo;

    public TargetDetails(TaskName taskName, String executionName, String name, String generalInfo) {
        this.taskName = taskName;
        this.executionName = executionName;
        this.name = name;
        this.generalInfo = generalInfo;
    }

    public TaskName getTaskName() {
        return taskName;
    }

    public String getExecutionName() {
        return executionName;
    }

    public String getName() {
        return name;
    }

    public String getGeneralInfo() {
        return generalInfo;
    }


}

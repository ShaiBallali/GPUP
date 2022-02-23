package dto.dtoServer.worker.workerengine2worker;

import dto.enums.RunResult;
import dto.enums.TargetState;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RunTimeTargetsDetails {
    private Set<TargetPerformByMe> targetsPerformByMe;

    public RunTimeTargetsDetails() {
        this.targetsPerformByMe = new HashSet<>();
    }

    public void addToTargetsPerformByMe (TargetPerformByMe targetPerformByMe) {
        this.targetsPerformByMe.add(targetPerformByMe);
    }

    public void updateTargetStatusToFinishRT (String targetName, String executionName, RunResult runResult) {    // RT = run time
        TargetPerformByMe target = getTarget(targetName, executionName);
        if (target != null ) {
            target.setTargetState(TargetState.FINISHED);
            target.setRunResult(runResult);
        }
        else {
            System.out.println("updateTargetStatusToFinishRT: error.");
        }
    }

    public TargetPerformByMe getTarget (String targetName, String executionName) {
        for (TargetPerformByMe target : targetsPerformByMe ) {
            if (target.getTargetName().equals(targetName) && target.getExecutionName().equals(executionName)) {
                return target;
            }
        }
        return null;
    }

    public void addLogRT (String targetName, String executionName, String logs) {       // RT = run time
        TargetPerformByMe target = getTarget(targetName, executionName);
        if (target != null ) {
            target.setLogs(logs);
        }
        else {
            System.out.println("updateTargetStatusToFinishRT: error.");
        }
    }

    public Set<TargetPerformByMe> getTargetsPerformByMe() {
        return targetsPerformByMe;
    }
}

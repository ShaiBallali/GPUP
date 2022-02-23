package dto.dtoServer.worker.server2worker;

public class ExecutableTargets {

    private TargetDetails[] targetsDetails;
    private int targetDetailsLogSize;

    private String[] stoppedExecutions;
    private int stoppedExecutionsLogSize;

    public ExecutableTargets (int maxNumOfTargets, int maxNumOfStopped) {
        this.targetsDetails = new TargetDetails[maxNumOfTargets];
        this.stoppedExecutions = new String[maxNumOfStopped];
        this.targetDetailsLogSize = 0;
        this.stoppedExecutionsLogSize = 0;
    }

    public void addToTargetsDetails (TargetDetails targetDetails) {
        targetsDetails[targetDetailsLogSize++] = targetDetails;
    }

    public TargetDetails[] getTargetsDetails() {
        return targetsDetails;
    }

    public int getTargetDetailsLogSize() {
        return targetDetailsLogSize;
    }


    public void addToStoppedExecutions (String executionName) {
        stoppedExecutions[stoppedExecutionsLogSize++] = executionName;
    }

    public String[] getStoppedExecutions() {
        return this.stoppedExecutions;
    }

    public int getStoppedExecutionsLogSize() {
        return stoppedExecutionsLogSize;
    }












}

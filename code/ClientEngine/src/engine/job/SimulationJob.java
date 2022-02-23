package engine.job;

import dto.dtoServer.worker.workerengine2worker.RunTimeTargetsDetails;
import dto.enums.RunResult;
import engine.RegisteredExecutions;
import java.util.Random;

public class SimulationJob extends GeneralJob{

    private final boolean isRandomTime;
    private final int targetProcessingTime;
    private final float successRate;
    private final float warningRate;

    public SimulationJob (RunTimeTargetsDetails runTimeTargetsDetails, String targetName, String generalInfo, RegisteredExecutions registeredExecutions) {
        super(runTimeTargetsDetails, targetName,generalInfo, registeredExecutions.getName());

        this.isRandomTime = registeredExecutions.isRandomTime();
        this.targetProcessingTime = registeredExecutions.getTargetProcessingTime();
        this.successRate = registeredExecutions.getSuccessRate();
        this.warningRate = registeredExecutions.getWarningRate();
    }

    @Override
    public void specificJob() {
        float runTime;

        if (isRandomTime) { // In case user wanted random processing time
            runTime = getRandNum() * targetProcessingTime;
        }
        else {
            runTime = targetProcessingTime;
        }

        this.logsManager.addToLogs("The target goes to sleep for: " + runTime + " ms.");
        this.logsManager.addToLogs("Begins to sleep.");

        this.runTimeTargetsDetails.addLogRT(this.targetName, this.executionName, this.logsManager.getLogs());

        try {
            Thread.sleep((long) runTime);
        } catch (InterruptedException ignored) {} // Nothing to do if Thread.sleep() fails

        this.logsManager.addToLogs("Finished sleeping.");

        this.runTimeTargetsDetails.addLogRT(this.targetName, this.executionName, this.logsManager.getLogs());

        if (getRandNum() <= successRate ) {
            if (getRandNum() <= warningRate) {
                this.runResult = RunResult.SUCCESS_WITH_WARNING;
            }
            else {
                this.runResult = RunResult.SUCCESS;
            }
        }
        else {
            this.runResult = RunResult.FAILURE;
        }
    }

    private float getRandNum() {
        Random random = new Random();
        return random.nextFloat();
    }
}

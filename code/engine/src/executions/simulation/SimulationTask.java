package executions.simulation;

import dto.dtoServer.worker.server2worker.BasicExecutionDetails;
import dto.enums.RunType;
import dto.enums.TaskName;
import engine.graph.Graph;
import executions.GeneralTask;

import java.nio.file.FileSystemException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class SimulationTask extends GeneralTask {

    private int targetProcessingTime;
    private boolean isRandomTime;
    private float successRate;
    private float warningRate;

    // ----------------------------------------------------------- //

    public SimulationTask(String name, String createdBy, TaskName taskName, Graph graph, List<Consumer <Map<String, String>>> outputData, String rootDirectory) {
        super(name, createdBy, taskName, graph, outputData, rootDirectory);
    }

    public SimulationTask (SimulationTask copyFrom, String createdBy, RunType runType) {
        super(copyFrom, createdBy, runType);
        this.targetProcessingTime = copyFrom.getTargetProcessingTime();
        this.isRandomTime = copyFrom.getIsRandomTime();
        this.successRate = copyFrom.getSuccessRate();
        this.warningRate = copyFrom.getWarningRate();
    }

    public void init(int TargetProcessingTime, boolean IsRandomTime, float successRate, float warningRate, Set<String> targetsToPerform) throws FileSystemException {
        this.targetProcessingTime = TargetProcessingTime;
        this.isRandomTime = IsRandomTime;
        this.successRate = successRate;
        this.warningRate = warningRate;
        generalInit(targetsToPerform);
    }

    public boolean getIsRandomTime() { return this.isRandomTime;}

    public int getTargetProcessingTime() { return this.targetProcessingTime;}

    public float getSuccessRate() { return this.successRate; }

    public float getWarningRate() { return this.warningRate; }

    public BasicExecutionDetails getBasicExecutionDetails () {
        return new BasicExecutionDetails(this.name, this.taskName, this.getPricePerTarget(), this.targetProcessingTime, this.isRandomTime, this.successRate, this.warningRate, null, null );
    }
}

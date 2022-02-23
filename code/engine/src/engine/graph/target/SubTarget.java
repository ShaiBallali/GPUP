package engine.graph.target;

import dto.enums.Position;
import dto.enums.RunResult;
import dto.enums.TargetState;
import engine.managers.LogsManager;

public class SubTarget {
    private String name;
    private Position position;
    private TargetState state;               // FROZEN | SKIPPED | WAITING | IN_PROCESS | FINISHED
    private RunResult runResult;             // SUCCESS | WARNING | FAILURE | SKIPPED
    private LogsManager logsManager;

    public SubTarget(String name, Position position, TargetState state, RunResult runResult) {
        this.name = name;
        this.position = position;
        this.state =  state;
        this.runResult = runResult;
        this.logsManager = new LogsManager(this.name);
    }

    public SubTarget (SubTarget subTarget) {
        this.name = subTarget.name;
        this.position = subTarget.position;
        this.state = subTarget.state;
        this.runResult = subTarget.runResult;
        this.logsManager = new LogsManager(this.name);
    }

    public TargetState getTargetState() {
        return this.state;
    }

    public RunResult getRunResult() {
        return this.runResult;
    }

    public void setState(TargetState state) {
        this.state = state;
    }

    public void setRunResult (RunResult runResult) {
        this.runResult = runResult;
    }

    public Position getPosition() {
        return position;
    }

    public void reset () {
        this.state = TargetState.FROZEN; // Initial state is frozen
        this.runResult = RunResult.WITHOUT;
    }

    public void addToLog (String log) {
        this.logsManager.addToLogs(log);
    }

    public String getLogs () {
        return this.logsManager.getLogs();
    }
}

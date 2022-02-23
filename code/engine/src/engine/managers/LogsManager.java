package engine.managers;

public class LogsManager {
    private String targetName;
    private String logs;

    public LogsManager(String targetName) {
        this.targetName = targetName;
        this.logs = "";
    }

    public void addToLogs (String log) {
        this.logs = this.logs.concat( log + "\n");
    }

    public String getTargetName() {
        return targetName;
    }

    public String getLogs() {
        return logs;
    }
}

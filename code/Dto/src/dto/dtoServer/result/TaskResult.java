package dto.dtoServer.result;

import dto.enums.RunResult;

import java.util.HashMap;
import java.util.Map;

public class TaskResult {
    private int allTargets;
    private int skipped;
    private int finished;
    private int success;
    private int successWithWarning;
    private int failed;

    public TaskResult (Map<RunResult, Integer> runResultCounter) {

        this.skipped = runResultCounter.get(RunResult.SKIPPED);
        this.success = runResultCounter.get(RunResult.SUCCESS);
        this.successWithWarning = runResultCounter.get(RunResult.SUCCESS_WITH_WARNING);
        this.failed = runResultCounter.get(RunResult.FAILURE);
        this.allTargets = this.skipped + this.failed + this.success + this.successWithWarning;
        this.finished = this.allTargets - this.skipped;
    }

    public Map<RunResult, Integer> getRunResultCounter() {
        Map <RunResult, Integer> runResultCounter = new HashMap<>();

        runResultCounter.put(RunResult.SKIPPED, this.skipped);
        runResultCounter.put(RunResult.SUCCESS, this.success);
        runResultCounter.put(RunResult.SUCCESS_WITH_WARNING, this.successWithWarning);
        runResultCounter.put(RunResult.FAILURE, this.failed);

        return runResultCounter;
    }

    public int getAllTargets() {
        return allTargets;
    }

    public int getFinished() {
        return finished;
    }
}

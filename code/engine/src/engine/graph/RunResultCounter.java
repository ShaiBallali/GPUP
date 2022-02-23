package engine.graph;

import dto.dtoServer.runtime.RunTimeTargetDetails;
import dto.enums.RunResult;

import java.util.HashMap;
import java.util.Map;

public class RunResultCounter {
    private Map<RunResult, Integer> counter = new HashMap<>(); // RunResult -> count

    public RunResultCounter () {                                      // CTOR
        counter.put(RunResult.SUCCESS, 0 );
        counter.put(RunResult.SKIPPED, 0 );
        counter.put(RunResult.FAILURE, 0 );
        counter.put(RunResult.SUCCESS_WITH_WARNING, 0 );
    }

    public synchronized void increment (String runResultString) {
        RunResult runResultEnum =  RunResult.valueOf(runResultString);
        int count = counter.get(runResultEnum);
        counter.put(runResultEnum, ++count );
    }

    public Map<RunResult, Integer> getRunResultCounter (Map <String, RunTimeTargetDetails> runTimeTargetsDetails) {
        runTimeTargetsDetails.forEach( (name , runTimeTargetDetails ) -> {
            increment(runTimeTargetDetails.getRunResult());
        });
        return counter;
    }
}
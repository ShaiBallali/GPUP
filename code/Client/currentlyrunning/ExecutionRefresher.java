package currentlyrunning;

import dto.dtoServer.worker.server2worker.RunTimeExecutionList;
import engine.ClientEngine;
import javafx.beans.property.BooleanProperty;
import java.util.TimerTask;
import java.util.function.Consumer;

public class ExecutionRefresher extends TimerTask {

    private final Consumer<RunTimeExecutionList> executionListConsumer;
    private final BooleanProperty shouldUpdate;
    private final ClientEngine clientEngine;

    public ExecutionRefresher(BooleanProperty shouldUpdate, Consumer<RunTimeExecutionList> executionListConsumer, ClientEngine clientEngine) { // TableGraph
        this.shouldUpdate = shouldUpdate;
        this.executionListConsumer = executionListConsumer;
        this.clientEngine = clientEngine;
    }

    @Override
    public void run() {

        if (!shouldUpdate.get()) {
            return;
        }
        RunTimeExecutionList runTimeTargetsDetails = clientEngine.currExecutionDetails(); //// can be null
        executionListConsumer.accept(runTimeTargetsDetails);
    }

}
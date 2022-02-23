package currentlyrunning;

import dto.dtoServer.worker.workerengine2worker.RunTimeTargetsDetails;
import engine.ClientEngine;
import javafx.beans.property.BooleanProperty;
import java.util.TimerTask;
import java.util.function.Consumer;

public class TargetsRefresher extends TimerTask {

    private final Consumer<RunTimeTargetsDetails> targetListConsumer;
    private final Consumer<String> msgConsumer;
    private final BooleanProperty shouldUpdate;
    private final ClientEngine clientEngine;



    public TargetsRefresher(BooleanProperty shouldUpdate, Consumer<RunTimeTargetsDetails> targetListConsumer, Consumer<String> msgConsumer, ClientEngine clientEngine) { // TableGraph
        this.shouldUpdate = shouldUpdate;
        this.targetListConsumer = targetListConsumer;
        this.msgConsumer = msgConsumer;
        this.clientEngine = clientEngine;
    }

    @Override
    public void run() {

        if (!shouldUpdate.get()) {
            return;
        }
        RunTimeTargetsDetails runTimeTargetsDetails = clientEngine.getRunTimeTargetsDetails();
        targetListConsumer.accept(runTimeTargetsDetails);
    }


}
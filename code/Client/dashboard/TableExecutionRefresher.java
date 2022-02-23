package dashboard;
import Util.Constants;
import Util.http.HttpClientUtil;
import dto.dtoServer.execution.Execution;
import dto.dtoServer.execution.ExecutionList;
import dto.dtoServer.execution.TableClientExecution;
import engine.ClientEngine;
import javafx.beans.property.BooleanProperty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

public class TableExecutionRefresher extends TimerTask {

    private final Consumer<List<TableClientExecution>> executionListConsumer;
    private final BooleanProperty shouldUpdate;
    private final ClientEngine clientEngine;
    private Object registeredExecutionsLock;


    public TableExecutionRefresher(BooleanProperty shouldUpdate, Consumer<List<TableClientExecution>> executionListConsumer, ClientEngine clientEngine, Object registeredExecutionsLock) { // TableGraph
        this.shouldUpdate = shouldUpdate;
        this.executionListConsumer = executionListConsumer;
        this.clientEngine = clientEngine;
        this.registeredExecutionsLock = registeredExecutionsLock;
    }

    @Override
    public void run() {

        if (!shouldUpdate.get()) {
            return;
        }

        HttpClientUtil.runAsync(Constants.EXECUTION_LIST, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonArrayOfExecutionNames = response.body().string();
                ExecutionList executionNames = Constants.GSON_INSTANCE.fromJson(jsonArrayOfExecutionNames, ExecutionList.class); // TaskList.class
                Execution[] executions = executionNames.getExecutions();

                executionListConsumer.accept(executionArrayToListOfTableExecutions(executions, executionNames.getLogSize()));
            }
        });
    }

    private List<TableClientExecution> executionArrayToListOfTableExecutions(Execution[] executions, int logSize) {
        List<TableClientExecution> executionsList = new ArrayList<>();
        for (int i = 0; i < logSize; ++i) {
            Execution currExecution = executions[i];
            synchronized (this.registeredExecutionsLock) {
                TableClientExecution currTableExecution = new TableClientExecution(currExecution.getName(), currExecution.getCreatedBy(), currExecution.getTaskName(), currExecution.getTargetAmount(),
                        currExecution.getRootCount(), currExecution.getMiddleCount(), currExecution.getLeafCount(),
                        currExecution.getIndependentsCount(), currExecution.getTotalPrice(), currExecution.getTotalWorkers(), currExecution.getExecutionStatus(), clientEngine.getName2RegisteredExecutions().containsKey(currExecution.getName().toLowerCase()));
                executionsList.add(currTableExecution);
            }
        }
        return executionsList;
    }
}
package dashboard;
import Util.Constants;
import Util.http.HttpClientUtil;
import dto.dtoServer.execution.Execution;
import dto.dtoServer.execution.ExecutionList;
import dto.dtoServer.execution.TableExecution;
import dto.dtoServer.graph.GraphDetails;
import dto.dtoServer.graph.GraphList;
import graphs.TableGraph;
import javafx.beans.property.BooleanProperty;
import main.AdminAppController;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

public class SelfExecutionRefresher extends TimerTask {

    private final Consumer<List<TableExecution>> executionListConsumer;
    private final BooleanProperty shouldUpdate;
    private final Consumer<String> msgConsumer;


    public SelfExecutionRefresher(BooleanProperty shouldUpdate, Consumer<List<TableExecution>> executionListConsumer, Consumer<String> msgConsumer) {
        this.shouldUpdate = shouldUpdate;
        this.executionListConsumer = executionListConsumer;
        this.msgConsumer = msgConsumer;
    }

    @Override
    public void run() {

        if (!shouldUpdate.get()) {
            return;
        }

        HttpClientUtil.runAsync(Constants.EXECUTION_LIST, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                msgConsumer.accept(e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonArrayOfExecutionNames= response.body().string();
                ExecutionList executionNames = Constants.GSON_INSTANCE.fromJson(jsonArrayOfExecutionNames, ExecutionList.class);
                List<TableExecution> executionList = executionNames.getExecution();
                executionListConsumer.accept(executionList);
            }
        });
    }
}


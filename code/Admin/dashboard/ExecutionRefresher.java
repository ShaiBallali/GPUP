package dashboard;
import Util.Constants;
import Util.http.HttpClientUtil;
import dto.dtoServer.execution.ExecutionList;
import dto.dtoServer.execution.TableExecution;
import javafx.beans.property.BooleanProperty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

public class ExecutionRefresher extends TimerTask {

    private final Consumer<List<TableExecution>> executionListConsumer; // TableGraph
    private final Consumer<String> msgConsumer;
    private final BooleanProperty shouldUpdate;



    public ExecutionRefresher(BooleanProperty shouldUpdate, Consumer<List<TableExecution>> executionListConsumer,  Consumer<String> msgConsumer) { // TableGraph
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
                executionListConsumer.accept(executionNames.getExecution());
            }
        });
    }


}
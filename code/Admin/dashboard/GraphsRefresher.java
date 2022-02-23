package dashboard;
import Util.Constants;
import Util.http.HttpClientUtil;
import dto.dtoServer.graph.GraphDetails;
import dto.dtoServer.graph.GraphList;
import graphs.TableGraph;
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

public class GraphsRefresher extends TimerTask {

    private final Consumer<List<TableGraph>> taskListConsumer;
    private final BooleanProperty shouldUpdate;
    private final Consumer<String> msgConsumer;


    public GraphsRefresher(BooleanProperty shouldUpdate, Consumer<List<TableGraph>> taskListConsumer, Consumer<String> msgConsumer) { // TableGraph
        this.shouldUpdate = shouldUpdate;
        this.taskListConsumer = taskListConsumer;
        this.msgConsumer = msgConsumer;
    }

    private List<TableGraph> graphListToListOfGraphs(GraphList graphNames){
        GraphDetails[] graphs = graphNames.getGraphDetails();
        List<TableGraph> tableGraphs = new ArrayList<>();
        for(int i = 0; i< graphNames.getLogSize(); ++i){
            GraphDetails currGraph = graphs[i];
            TableGraph curr = new TableGraph (currGraph.getName(), currGraph.getCreatedBy(), currGraph.getTargetAmount(), currGraph.getRootCount(),
                    currGraph.getMiddleCount(), currGraph.getLeafCount(), currGraph.getIndependentCount(), currGraph.getTaskPrices(), currGraph.getTargetDetails(), currGraph.getTargetsNames());
            tableGraphs.add(curr);
        }
        return tableGraphs;
    }

    @Override
    public void run() {

        if (!shouldUpdate.get()) {
            return;
        }

        HttpClientUtil.runAsync(Constants.GRAPH_LIST, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                msgConsumer.accept(e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonArrayOfGraphNames = response.body().string();
                GraphList graphNames = Constants.GSON_INSTANCE.fromJson(jsonArrayOfGraphNames, GraphList.class);
                taskListConsumer.accept(graphListToListOfGraphs(graphNames));
            }
        });
    }
}
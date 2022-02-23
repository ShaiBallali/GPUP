package midRunTable;

import Util.Constants;
import Util.http.HttpClientUtil;
import com.google.gson.stream.JsonReader;
import dto.dtoServer.runtime.RunTimeTaskDetails;
import javafx.beans.property.BooleanProperty;
import main.AdminAppController;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.io.StringReader;
import java.util.TimerTask;
import java.util.function.Consumer;

public class MidRunTableRefresher extends TimerTask {

    private final Consumer<RunTimeTaskDetails> runTimeTargetsListConsumer;
    private final BooleanProperty shouldUpdate;
    private final AdminAppController adminAppController;

    public MidRunTableRefresher(BooleanProperty shouldUpdate, Consumer<RunTimeTaskDetails> runTimeTaskConsumer, AdminAppController adminAppController) { // TableGraph
        this.shouldUpdate = shouldUpdate;
        this.runTimeTargetsListConsumer = runTimeTaskConsumer;
        this.adminAppController = adminAppController;
    }

    @Override
    public void run() {
        String finalUrl = HttpUrl
                .parse(Constants.RUN_TIME)
                .newBuilder()
                .addQueryParameter("executionName", adminAppController.getChosenExecution().getExecutionName())
                .build()
                .toString();
        if (!shouldUpdate.get()) {
            return;
        }

        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonArrayOfRuntimeTargets= response.body().string();
                JsonReader reader = new JsonReader(new StringReader(jsonArrayOfRuntimeTargets));
                reader.setLenient(true);
                RunTimeTaskDetails targets = Constants.GSON_INSTANCE.fromJson(reader, RunTimeTaskDetails.class);
                runTimeTargetsListConsumer.accept(targets);
            }
        });
    }
}

package engine.job;

import configuration.GsonConfig;
import configuration.HttpConfig;
import dto.dtoServer.worker.worker2server.RunningResultDetails;
import dto.dtoServer.worker.workerengine2worker.RunTimeTargetsDetails;
import dto.enums.RunResult;
import engine.managers.LogsManager;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public abstract class GeneralJob implements Runnable {
    protected String targetName;
    protected LogsManager logsManager;
    protected RunResult runResult;
    protected String generalInfo;
    protected String executionName;
    protected RunTimeTargetsDetails runTimeTargetsDetails;

    public GeneralJob(RunTimeTargetsDetails runTimeTargetsDetails, String targetName, String generalInfo, String executionName) {
        this.runTimeTargetsDetails = runTimeTargetsDetails;
        this.targetName = targetName;
        this.generalInfo = generalInfo;
        this.logsManager = new LogsManager(targetName);
        this.executionName = executionName;
    }

    @Override
    public void run() {
        specificJob();

        this.runTimeTargetsDetails.updateTargetStatusToFinishRT(this.targetName, this.executionName, this.runResult);

        updateServerOnRunResult();
    }

    public void updateServerOnRunResult() {
        RunningResultDetails runningResultDetails = new RunningResultDetails(this.targetName, this.executionName, this.logsManager.getLogs(), this.runResult);

        String runningResultDetailsJson = GsonConfig.gson.toJson(runningResultDetails);
        String body = "runningResultDetails=" + runningResultDetailsJson;
        Request request = new Request.Builder().url(HttpConfig.BASE_URL + "/target-finished")
                .post(RequestBody.create(body.getBytes()))
                .build();

        Call call = HttpConfig.HTTP_CLIENT.newCall(request);

        try {
            final Response response = call.execute();
            if (response.code() != 200) {
                throw new Exception(response.message());
            }
        } catch (Exception e) {
            System.out.println("error! message: " + e.getMessage());
        }
    }

    public abstract void specificJob();
}

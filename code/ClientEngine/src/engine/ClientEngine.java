package engine;

import configuration.GsonConfig;
import configuration.HttpConfig;
import dto.dtoServer.worker.server2worker.BasicExecutionDetails;

import dto.dtoServer.worker.server2worker.RunTimeExecutionDetails;
import dto.dtoServer.worker.server2worker.RunTimeExecutionList;
import dto.dtoServer.worker.workerengine2worker.RunTimeTargetsDetails;
import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import java.util.HashMap;
import java.util.Map;

public class ClientEngine {
    private String myName;
    private final int numOfThread;                                    // 1 to 5
    private TaskManager taskManager;
    private Map<String, RegisteredExecutions> name2RegisteredExecutions;
    private int myCredit;
    public String msg;
    private final Object registeredExecutionsLock;

    public ClientEngine(String myName, int numOfThread) {
        this.myName = myName;
        this.numOfThread = numOfThread;
        this.name2RegisteredExecutions = new HashMap<>();
        this.myCredit = 0;
        this.msg = "";
        this.registeredExecutionsLock = new Object();
        this.taskManager = new TaskManager(this);
        this.taskManager.start();
    }

    public void subscribeToExecution(String executionName) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(HttpConfig.BASE_URL + "/execution-subscribe").newBuilder();
        urlBuilder.addQueryParameter("executionName", executionName);
        String finalUrl = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(finalUrl)
                .build();

        Call call = HttpConfig.HTTP_CLIENT.newCall(request);

        try {
            final Response response = call.execute();
            if (response.code() != 200) {
                throw new Exception(response.message());
            }

            String basicExecutionDetailsJson = response.body().string();
            BasicExecutionDetails basicExecutionDetails = GsonConfig.gson.fromJson(basicExecutionDetailsJson, BasicExecutionDetails.class);
            synchronized(this.registeredExecutionsLock) {
                this.name2RegisteredExecutions.put(basicExecutionDetails.getName().toLowerCase(), new RegisteredExecutions(basicExecutionDetails));
            }
        } catch (Exception e) {
          this.msg = "error! message: " + e.getMessage();
        }
    }

    public void unsubscribeToExecution(String executionName) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(HttpConfig.BASE_URL + "/execution-unsubscribe").newBuilder();
        urlBuilder.addQueryParameter("executionName", executionName);
        String finalUrl = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(finalUrl)
                .build();

        Call call = HttpConfig.HTTP_CLIENT.newCall(request);

        try {
            final Response response = call.execute();
            if (response.code() != 200) {
                throw new Exception(response.message());
            }
            synchronized(this.registeredExecutionsLock) {
                this.name2RegisteredExecutions.remove(executionName.toLowerCase());
            }

        } catch (Exception e) {
            this.msg = "error! message: " + e.getMessage();
        }
    }

    public int getNumOfThread() {
        return numOfThread;
    }

    public int getNumOfBusyThreads() {
        return this.taskManager.getNumOfBusyThreads();
    }

    public Map<String, RegisteredExecutions> getName2RegisteredExecutions() {
        return name2RegisteredExecutions;
    }

    public RunTimeTargetsDetails getRunTimeTargetsDetails() {
        return this.taskManager.getRunTimeTargetsDetails();
    }

    public void addToMyCredit(int credit) {
        this.myCredit += credit;
    }

    public int getMyCredit() {
        return myCredit;
    }

    public String getMsg() {
        return msg;
    }

    public RunTimeExecutionList currExecutionDetails() {
        RunTimeExecutionList runTimeExecutionList = null;

        HttpUrl.Builder urlBuilder = HttpUrl.parse(HttpConfig.BASE_URL + "/execution-details-worker-servlet").newBuilder();
        String finalUrl = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(finalUrl)
                .build();

        Call call = HttpConfig.HTTP_CLIENT.newCall(request);

        try {
            final Response response = call.execute();
            if (response.code() != 200) {
                throw new Exception(response.message());
            }

            String runTimeExecutionListJson = response.body().string();
            runTimeExecutionList = GsonConfig.gson.fromJson(runTimeExecutionListJson, RunTimeExecutionList.class);

            RunTimeExecutionDetails[] runTimeExecutionsDetails = runTimeExecutionList.getRunTimeExecutionsDetails();

            for (RunTimeExecutionDetails runTimeExecutionDetails : runTimeExecutionsDetails) {
                if (runTimeExecutionDetails != null) {
                    String executionName = runTimeExecutionDetails.getName();
                    synchronized (this.registeredExecutionsLock) {
                        runTimeExecutionDetails.setNumOfTargetWorkerPerformHere(this.name2RegisteredExecutions.get(executionName).getNumOfTargetIAlreadyPerformHere());
                        runTimeExecutionDetails.setTotalPriceFromThisExecution(this.name2RegisteredExecutions.get(executionName).getTotalPriceFromThisExecution());
                    }
                }
            }

        } catch (Exception e) {
            this.msg = "error! message: " + e.getMessage();
            runTimeExecutionList = null;
        }

        return runTimeExecutionList;
    }

    public String getMyName() {
        return myName;
    }

    public Object getRegisteredExecutionsLock() {
        return registeredExecutionsLock;
    }
}

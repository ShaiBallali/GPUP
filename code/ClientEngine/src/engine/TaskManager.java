package engine;

import configuration.GsonConfig;
import configuration.HttpConfig;
import dto.dtoServer.worker.server2worker.ExecutableTargets;
import dto.dtoServer.worker.server2worker.TargetDetails;
import dto.dtoServer.worker.workerengine2worker.RunTimeTargetsDetails;
import dto.dtoServer.worker.workerengine2worker.TargetPerformByMe;
import dto.enums.RunResult;
import dto.enums.TargetState;
import engine.job.CompilationJob;
import engine.job.SimulationJob;
import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class TaskManager {
    private ClientEngine clientEngine;
    private ThreadPoolExecutor threadExecutor;
    private int numOfThread;
    private RunTimeTargetsDetails runTimeTargetsDetails;

    public TaskManager (ClientEngine clientEngine) {
        this.clientEngine  = clientEngine;
        this.numOfThread = clientEngine.getNumOfThread();
        this.threadExecutor = new ThreadPoolExecutor(this.numOfThread, this.numOfThread,60, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
        this.runTimeTargetsDetails = new RunTimeTargetsDetails();
    }

    public void start () {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(500);
                }
                catch(Exception ignored) {}

                if (!this.clientEngine.getName2RegisteredExecutions().isEmpty())
                {
                    // create and send request
                    HttpUrl.Builder urlBuilder = HttpUrl.parse(HttpConfig.BASE_URL + "/get-executable-targets").newBuilder();
                    urlBuilder.addQueryParameter("targetsAmount", String.valueOf(this.numOfThread - this.threadExecutor.getActiveCount()));
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

                        String executableTargetsJson = response.body().string();
                        ExecutableTargets executableTargets = GsonConfig.gson.fromJson(executableTargetsJson, ExecutableTargets.class);

                        // Go over tasks that have been stopped if they exist
                        String [] stoppedExecutions = executableTargets.getStoppedExecutions();
                        for ( String stoppedExecution : stoppedExecutions) {
                            if (stoppedExecution != null) {
                                clientEngine.unsubscribeToExecution(stoppedExecution);
                                this.clientEngine.msg = this.clientEngine.msg.concat("The execution named: " + stoppedExecution + " has been stopped by admin\n");
                            }
                        }

                        // Go over returned targets
                        TargetDetails targetsDetails[] = executableTargets.getTargetsDetails();
                        for (TargetDetails targetDetails : targetsDetails) {
                            if ( targetDetails != null ) {
                                RegisteredExecutions registeredExecutions = null;
                                synchronized(clientEngine.getRegisteredExecutionsLock()) {
                                    registeredExecutions = this.clientEngine.getName2RegisteredExecutions().get(targetDetails.getExecutionName());
                                }
                                clientEngine.addToMyCredit(registeredExecutions.getPrice());
                                synchronized(clientEngine.getRegisteredExecutionsLock()) {
                                    clientEngine.getName2RegisteredExecutions().get(targetDetails.getExecutionName()).incrementTargetIAlreadyPerformBy1();
                                }
                                this.runTimeTargetsDetails.addToTargetsPerformByMe(new TargetPerformByMe(targetDetails.getExecutionName(), targetDetails.getTaskName(), targetDetails.getName(), TargetState.IN_PROCESS, RunResult.WITHOUT, registeredExecutions.getPrice()));

                                switch (targetDetails.getTaskName()) {
                                    case SIMULATION:
                                        this.threadExecutor.execute(new SimulationJob(runTimeTargetsDetails, targetDetails.getName(), targetDetails.getGeneralInfo(), registeredExecutions));
                                        break;
                                    case COMPILATION:
                                        this.threadExecutor.execute(new CompilationJob(runTimeTargetsDetails, targetDetails.getName(), targetDetails.getGeneralInfo(), registeredExecutions));
                                        break;
                                }
                            }
                        }
                    } catch (Exception e) {
                        this.clientEngine.msg = e.getMessage();
                    }
                }
            }
        }).start();
    }

    public int getNumOfBusyThreads () {
        return this.threadExecutor.getActiveCount();
    }

    public RunTimeTargetsDetails getRunTimeTargetsDetails () {
        return this.runTimeTargetsDetails;
    }
}



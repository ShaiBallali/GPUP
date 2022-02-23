package engine.managers.users;

import dto.dtoServer.worker.server2worker.ExecutableTargets;
import dto.dtoServer.worker.server2worker.RunTimeExecutionList;
import engine.managers.MainManager;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class WorkerManager {
    private Map<String, WorkerData> name2WorkerData;

    public WorkerManager() {
        this.name2WorkerData = new HashMap<>();
    }

    public void addWorker(String workerName) {
        name2WorkerData.put(workerName, new WorkerData(workerName));
    }

    public void subscribeToExecution(String workerName, String executionName, MainManager mainManager) throws Exception {
        this.name2WorkerData.get(workerName).subscribeToExecution(executionName,mainManager);
    }

    public void unsubscribeToExecution(String workerName, String executionName, MainManager mainManager) throws Exception {
        this.name2WorkerData.get(workerName).unsubscribeFromExecution(executionName,mainManager);
    }

    public ExecutableTargets getTargets (String workerName,int amount, MainManager mainManager ) throws NoSuchElementException {
       return this.name2WorkerData.get(workerName).getTargets(amount,mainManager);
    }

    public void addToCredit (String workerName, int price) {
        this.name2WorkerData.get(workerName).addToCredit(price);
    }

    public RunTimeExecutionList getRunTimeExecutionList (String workerName, MainManager mainManager) {
        return this.name2WorkerData.get(workerName).getRunTimeExecutionList(mainManager);
    }

    public void pauseRegistration (String workerName, String executionName) {
        this.name2WorkerData.get(workerName).pauseRegistration(executionName);
    }

    public void resumeRegistration (String workerName, String executionName) {
        this.name2WorkerData.get(workerName).resumeRegistration(executionName);
    }
}



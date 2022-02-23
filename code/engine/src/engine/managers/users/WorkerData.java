package engine.managers.users;

import dto.dtoServer.worker.server2worker.ExecutableTargets;
import dto.dtoServer.worker.server2worker.RunTimeExecutionDetails;
import dto.dtoServer.worker.server2worker.RunTimeExecutionList;
import dto.dtoServer.worker.server2worker.TargetDetails;
import dto.enums.ExecutionStatus;
import engine.managers.MainManager;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

public class WorkerData {
    private String name;
    private Set<String> executions;
    private Set<String> availableRegistration;
    private int credit;

    public WorkerData (String name) {
        this.name = name;
        this.credit = 0;
        this.executions = new HashSet<>();
        this.availableRegistration = new HashSet<>();
    }

    // return data of execution - Servlet
    public void subscribeToExecution(String executionName, MainManager mainManager) throws Exception {
        mainManager.addWorkerToExecution(executionName, this.name);
        this.executions.add(executionName);
        this.availableRegistration.add(executionName);
    }

    public void unsubscribeFromExecution(String executionName, MainManager mainManager) throws Exception {
        this.executions.remove(executionName);
        this.availableRegistration.remove(executionName);
        mainManager.removeWorkerFromExecution(executionName, this.name);
    }

    public ExecutableTargets getTargets (int amount, MainManager mainManager) throws NoSuchElementException {
        ExecutableTargets executableTargets = new ExecutableTargets(amount, this.executions.size());
        int numOfTargets = 0;

        // check if there are execution in executions set that already stopped
        for (String execution : executions) {
            if (mainManager.didExecutionStop(execution)) {
                executableTargets.addToStoppedExecutions(execution);
            }
        }

        // bring executable targets
        for (int i = 0 ; i < amount ; i++) {
            for (String execution : executions) {
                if (this.availableRegistration.contains(execution)) {
                    TargetDetails targetDetails = mainManager.getExecutableTarget(execution);
                    if (targetDetails != null) {
                        executableTargets.addToTargetsDetails(targetDetails);
                        numOfTargets++;
                        if (numOfTargets == amount) {
                            return executableTargets;
                        }
                    }
                }
            }
        }
        return executableTargets;
    }

    public void addToCredit(int price) {
        this.credit += price;
    }

    public RunTimeExecutionList getRunTimeExecutionList (MainManager mainManager) {
        RunTimeExecutionList runTimeExecutionList = new RunTimeExecutionList(0);

        for ( String execution : executions ) {
            if (mainManager.getExecutionStatus(execution) == ExecutionStatus.PLAYING) {
                int workerAmount = mainManager.getNunOfWorkers(execution);
                int numOfTargets = mainManager.getNumOfTargets(execution);
                int numOfCompletedTarget = mainManager.getNumOfCompletedTarget(execution);
                boolean isPaused = this.availableRegistration.contains(execution) ? false : true;
                runTimeExecutionList.addToRunTimeExecutionsDetails(new RunTimeExecutionDetails(execution, workerAmount, numOfTargets, numOfCompletedTarget, isPaused));
            }
        }

        return runTimeExecutionList;
    }

    public void pauseRegistration (String executionName) {
        this.availableRegistration.remove(executionName);
    }

    void resumeRegistration (String executionName) {
        this.availableRegistration.add(executionName);
    }

    public int getCredit() {
        return credit;
    }
}

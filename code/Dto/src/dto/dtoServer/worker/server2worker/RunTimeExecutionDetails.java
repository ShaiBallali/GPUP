package dto.dtoServer.worker.server2worker;

public class RunTimeExecutionDetails {
    private String name;
    private int workerAmount;
    private int numOfTargets;
    private int numOfCompletedTargets;
    private int numOfTargetWorkerPerformHere;
    private int totalPriceFromThisExecution;
    private boolean isPaused;

    public RunTimeExecutionDetails(String name, int workerAmount, int numOfTargets, int numOfCompletedTargets, boolean isPaused) {
        this.name = name;
        this.workerAmount = workerAmount;
        this.numOfTargets = numOfTargets;
        this.numOfCompletedTargets = numOfCompletedTargets;
        this.numOfTargetWorkerPerformHere = 0;
        this.totalPriceFromThisExecution = 0;
        this.isPaused = isPaused;
    }

    public void setNumOfTargetWorkerPerformHere(int numOfTargetWorkerPerformHere) {
        this.numOfTargetWorkerPerformHere = numOfTargetWorkerPerformHere;
    }

    public void setTotalPriceFromThisExecution(int totalPriceFromThisExecution) {
        this.totalPriceFromThisExecution = totalPriceFromThisExecution;
    }

    public int getWorkerAmount() {
        return workerAmount;
    }

    public int getNumOfTargets() {
        return numOfTargets;
    }

    public int getNumOfCompletedTargets() {
        return numOfCompletedTargets;
    }

    public String getName() {
        return name;
    }

    public int getNumOfTargetWorkerPerformHere() {
        return numOfTargetWorkerPerformHere;
    }

    public int getTotalPriceFromThisExecution() {
        return totalPriceFromThisExecution;
    }

    public boolean getIsPaused() {
        return isPaused;
    }

    public double getProgress(){return (double)numOfCompletedTargets/numOfTargets;}

    public void setIsPaused(boolean paused) {
        isPaused = paused;
    }
}

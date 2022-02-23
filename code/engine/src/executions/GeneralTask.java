package executions;

import dto.dtoServer.execution.Execution;
import dto.dtoServer.runtime.RunTimeTargetDetails;
import dto.dtoServer.runtime.RunTimeTaskDetails;
import dto.dtoServer.worker.server2worker.TargetDetails;
import dto.dtoServer.worker.worker2server.RunningResultDetails;
import dto.enums.Position;
import dto.enums.RunResult;
import dto.enums.TargetState;

import dto.dtoServer.result.TaskResult;
import dto.enums.ExecutionStatus;
import dto.enums.RunType;
import dto.enums.TaskName;
import engine.graph.*;
import engine.graph.target.SubTarget;
import engine.graph.target.Target;

import javax.management.InstanceAlreadyExistsException;
import java.io.*;
import java.nio.file.FileSystemException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;

abstract public class GeneralTask {

    private boolean isOrigin;
    protected final String name;
    private int version;
    private final String createdBy;

    protected final TaskName taskName;
    private int pricePerTarget;
    private final Set<String> targetsToPerform;
    private int sumOfTargets;
    private int sumOfTargetToPerform;
    private int sumOfCompletedTargets;
    private SubGraph subGraph;

    private final Queue<Target> executable;                                             // Queue of runnable
    private final List<Consumer <Map<String, String>>> outputData;
    protected String filePath;
    private RunTimeTaskDetails runTimeTaskDetails;
    private Writer out;
    private String rootDirectory;
    private TaskResult taskResult;
    private Set<String> workers;
    private ExecutionStatus executionStatus;

    private final Object mainLocked;

    // ------------------------------------------------------- //

    public int getNunOfWorkers () {
       return this.workers.size();
    }

    public GeneralTask(String name, String createdBy, TaskName taskName, Graph graph ,List<Consumer <Map<String, String>>> outputData, String rootDirectory) {
        this.isOrigin = true;
        this.name = name.toLowerCase();
        this.version = 0;
        this.createdBy = createdBy;
        this.taskName = taskName;
        this.rootDirectory = rootDirectory;
        this.subGraph = new SubGraph(graph);
        this.executionStatus = ExecutionStatus.NEW;

        this.targetsToPerform = new HashSet<>();
        this.executable = new LinkedList<>();
        this.mainLocked = new Object();
        this.workers = new HashSet<>();

        Consumer <Map<String, String>> writeToFileConsumer = new Consumer<Map<String, String>>() {
            @Override
            public void accept(Map<String, String> s) {
                try {
                    for (Map.Entry<String, String> entry : s.entrySet())
                    {
                        openFile(entry.getKey());
                        if (out != null) {
                            out.write(entry.getKey() + ":\r\n");
                            out.write(entry.getValue() + "\r\n");
                        }
                        closeFile();
                    }
                } catch (IOException Ignored) {}
            }
        };

        this.outputData = outputData;
        this.outputData.add(writeToFileConsumer); // Add writeToFileConsumer to the output consumers list (previous one was to te screen)
    }

    public GeneralTask (GeneralTask copyFrom, String createdBy, RunType runType) {
        this.isOrigin = false;
        this.name = copyFrom.name + " " + ++copyFrom.version;
        this.version = 0;
        this.createdBy = createdBy;
        this.taskName = copyFrom.taskName;
        this.rootDirectory = copyFrom.rootDirectory;
        this.subGraph = new SubGraph(copyFrom.subGraph, runType);
        this.executionStatus = ExecutionStatus.NEW;
        this.targetsToPerform = new HashSet<>(copyFrom.targetsToPerform);
        this.outputData = copyFrom.outputData;
        this.pricePerTarget = copyFrom.pricePerTarget;

        this.executable = new LinkedList<>();
        this.mainLocked = new Object();
        this.workers = new HashSet<>();

        this.sumOfTargets = subGraph.sumOfTarget;
        this.sumOfTargetToPerform = subGraph.getSumOfTargetToPerform();
        this.sumOfCompletedTargets = this.sumOfTargets - this.sumOfTargetToPerform;

        resetRunTimeTaskDetails();
    }

    public void generalInit(Set<String> targetsToPerform) throws FileSystemException {
        this.targetsToPerform.addAll(targetsToPerform);

        this.subGraph.initialSubGraph(targetsToPerform);
        this.pricePerTarget = subGraph.originGraph.task2price.get(this.taskName.toString());

        this.sumOfTargets = this.subGraph.getSumOfTarget();
        this.sumOfTargetToPerform = this.subGraph.getSumOfTargetToPerform();
        this.sumOfCompletedTargets = 0;

        resetRunTimeTaskDetails();
    }

    public void resetRunTimeTaskDetails () {

        this.runTimeTaskDetails = new RunTimeTaskDetails(this.subGraph.name2dependsOn);
        this.runTimeTaskDetails.setSumOfCompletedTargets(this.sumOfCompletedTargets);
        this.runTimeTaskDetails.setSumOfTargets(this.sumOfTargets);

        subGraph.name2dependsOn.forEach( (name, dependency) -> {
            Target target = subGraph.getOriginTarget(name);
            SubTarget subTarget = subGraph.subGraphData.get(name);
            this.runTimeTaskDetails.initRunTimeTargetDetails(
                    name,
                    subGraph.getTargetPosition(name).toString(),
                    target.getMySerialSets().isEmpty() ? "" : target.getMySerialSets().toString(),
                    subTarget.getTargetState().toString(),

                    subTarget.getRunResult().toString(),
                    this.subGraph.name2dependsOn.get(name).size() == 0 ? "" :  subGraph.name2dependsOn.get(name).toString()
//                    subGraph.getTargetPosition(name) == Position.LEAF || subGraph.getTargetPosition(name) == Position.INDEPENDENT ? "" :  subGraph.name2dependsOn.get(name).toString()
            );
        } );
    }

    public ExecutionStatus getExecutionStatus() {
        return executionStatus;
    }

    public void addWorker(String workerName) throws InstanceAlreadyExistsException {
        synchronized (executionListLocked) {
            if (this.workers.contains(workerName)) {
                throw new InstanceAlreadyExistsException();
            }
            this.workers.add(workerName);
        }
    }

    public void removeWorker(String workerName) throws Exception {
        synchronized (executionListLocked) {
            if (!this.workers.contains(workerName)) {
                throw new InstanceAlreadyExistsException();
            }
            this.workers.remove(workerName);
        }
    }

    public void getStarted() throws Exception {
        if (this.executionStatus == ExecutionStatus.PLAYING ) {
            throw new Exception("The status is already PLAYING");
        }

        this.executionStatus = ExecutionStatus.PLAYING;
        this.runTimeTaskDetails.setExecutionStatus(ExecutionStatus.PLAYING);
        this.filePath = createFolder(this.taskName, rootDirectory);

        subGraph.getName2dependsOn().forEach(this::initExecutable);        // init executable queue
    }

    private void initExecutable (String targetName, Set<String> dependsOn) {
        if (dependsOn.isEmpty() && subGraph.getTargetState(targetName) == TargetState.FROZEN) {
            Target executableTarget = subGraph.getOriginTarget(targetName);

            executable.add(executableTarget);
            runTimeTaskDetails.increaseWaitingTargetsBy1();

            subGraph.setTargetState(targetName, TargetState.WAITING); // Waiting = ready to run
            this.runTimeTaskDetails.getTargetByName(targetName).setState(TargetState.WAITING.toString());
            this.runTimeTaskDetails.getTargetByName(targetName).setStartWaitingTime(System.currentTimeMillis());
        }
    }

    public TargetDetails getExecutableTarget () {
        TargetDetails targetDetails = null;
        Target target = null;
        if (!executable.isEmpty() && executionStatus == ExecutionStatus.PLAYING) {
            target = executable.remove();

            onTargetStartToRun(target.getName());

            targetDetails = new TargetDetails(this.taskName, this.name, target.getName(), target.getGeneralInfo());
        }
        return targetDetails;
    }

    public void onTargetStartToRun (String targetName) {
        RunTimeTargetDetails runTimeTargetDetails = runTimeTaskDetails.getTargetByName(targetName);
        runTimeTargetDetails.setState(TargetState.IN_PROCESS.toString());
        runTimeTargetDetails.setStartProcessingTime(System.currentTimeMillis());
        runTimeTaskDetails.decreaseWaitingTargetsBy1();
        runTimeTaskDetails.increaseCurrRunningBy1();

        this.subGraph.addLog(targetName, "started running...");

        String generalInfo = this.subGraph.originGraph.graphData.get(targetName).getGeneralInfo();

        if (generalInfo != null && !generalInfo.isEmpty())  this.subGraph.addLog(targetName, "general information: " + generalInfo + ".");

        this.subGraph.subGraphData.get(targetName).setState(TargetState.IN_PROCESS);
    }

    public void onTargetEndToRun (RunningResultDetails runningResultDetails) {
        String targetName = runningResultDetails.getTargetName().toUpperCase();
        RunResult runResult = runningResultDetails.getRunResult();
        this.subGraph.addLog(runningResultDetails.getTargetName(), runningResultDetails.getLogs());

        RunTimeTargetDetails runTimeTargetDetails = runTimeTaskDetails.getTargetByName(targetName);
        runTimeTargetDetails.setState(TargetState.FINISHED.toString());
        runTimeTargetDetails.setEndProcessingTime(System.currentTimeMillis());                            // Finish counting runtime
        runTimeTargetDetails.setRunResult(runResult.toString());
        runTimeTaskDetails.decreaseCurrRunningBy1();


        SubTarget target = subGraph.subGraphData.get(targetName);
        updateRunResult(target, runResult);
        updateTargetState(target, TargetState.FINISHED); // Nevertheless, the target has now finished running

        reportIntermediateState(targetName, runResult);

        if (runResult != RunResult.FAILURE) {
            // Removing the target that succeeded from all of its dependencies
            removeTargetFromDependencies(targetName);
        }
        else {
            Set<String> skippedTarget = new HashSet<>();
            detectSkippedTarget(targetName, skippedTarget, targetName);
            if (skippedTarget.size() != 0)  this.subGraph.addLog(targetName,"As a result of " + targetName + " failed, these targets: " + skippedTarget + " skipped.");
        }

        incrementSumOfCompletedTargetsBy1();

        Map <String, String> data = new HashMap<>();
        data.put(targetName, this.subGraph.getLogs(targetName));

        this.runTimeTaskDetails.setMessage(data);
        sendDataToOutput(data);
    }

    protected void reportIntermediateState(String targetName, RunResult runResult) {
        Set<String> freeTarget = new HashSet<>();

        this.subGraph.addLog(targetName, targetName + " has ended. Result: " + runResult.toString() + ".");

        getFreeTargets(freeTarget, targetName);

        if (freeTarget.size() != 0) {
            this.subGraph.addLog(targetName, "As a result of " + targetName + " finishing process, " + ": " + freeTarget + " does not depend on any target.");
        }
    }

    public void incrementSumOfCompletedTargetsBy1 () {
        synchronized (mainLocked) {
            this.sumOfCompletedTargets++;
            this.runTimeTaskDetails.incrementSumOfCompletedTargetsBy1();
            if (this.sumOfCompletedTargets == this.sumOfTargets) {
                createTaskResult();
                this.executionStatus = ExecutionStatus.FINISHED;
                this.runTimeTaskDetails.setExecutionStatus(ExecutionStatus.FINISHED);
            }
        }
    }

    private final Object name2dependsOnLocalLocked = new Object();
    public void removeTargetFromDependencies(String finishedTarget) {

        Set<String> requiredFor = subGraph.name2RequiredFor.get(finishedTarget);

        synchronized (name2dependsOnLocalLocked) {
            for (String targetName : requiredFor) {                             // Iterating through the targets' required for to remove it from their dependsOn
                Set<String> dependsOn = subGraph.name2dependsOn.get(targetName);

                dependsOn.remove(finishedTarget);
                runTimeTaskDetails.getTargetByName(targetName).setDependentsThatBlock(dependsOn.toString());

                addToExecutableIfReadyToRun(targetName, dependsOn);             // If after such removal a certain target now has no dependsOn, then he is ready to run.
            }
        }
    }

    public TaskName getTaskName () {
        return this.taskName;
    }

    private boolean addToExecutableIfReadyToRun(String targetName, Set<String> dependsOn) {
        boolean isAdded = false;

        if (dependsOn.isEmpty()) {
            Target executableTarget = subGraph.getOriginTarget(targetName);

            executable.add(executableTarget);
            runTimeTaskDetails.increaseWaitingTargetsBy1();
            isAdded = true;

            SubTarget executableSubTarget = subGraph.subGraphData.get(targetName);

            if (executableSubTarget.getTargetState() != TargetState.WAITING)
            {
                executableSubTarget.setState(TargetState.WAITING);             // Waiting = ready to run
                RunTimeTargetDetails runTimeTargetDetails =  runTimeTaskDetails.getTargetByName(targetName);
                runTimeTargetDetails.setState(TargetState.WAITING.toString());
                runTimeTargetDetails.setStartWaitingTime(System.currentTimeMillis());
            }
        }
        return isAdded;
    }

    public void updateTargetState(SubTarget target, TargetState targetState) {
        target.setState(targetState);
    }

    public void updateRunResult(SubTarget target, RunResult runResult) {
        target.setRunResult(runResult);
    }

    public RunTimeTaskDetails getRunTimeTaskDetails() {
        return this.runTimeTaskDetails;
    }

    // Recursion method to detect skipped targets mid-run
    private final Object detectSkippedTargetLocked = new Object();
    public void detectSkippedTarget(String targetName, Set<String> skippedTargets, String failedTargetName) {
        Set<String> name2RequiredFor = this.subGraph.name2RequiredFor.get(targetName);

        if (name2RequiredFor.size() == 0) {  // there are no skipped targets
            return;
        } else {
            name2RequiredFor.forEach((name) -> {              // Iterate through the target's requiredFor set (all of them are now skipped)
                synchronized (name2dependsOnLocalLocked) {
                    runTimeTaskDetails.getTargetByName(name).addToDependentsThatFailed(failedTargetName);

                    if (subGraph.getTargetRunResult(name) != RunResult.SKIPPED) {       // If he has not skipped yet
                        synchronized (detectSkippedTargetLocked) {
                            subGraph.setTargetState(name, TargetState.SKIPPED);
                            subGraph.setTargetRunResult(name, RunResult.SKIPPED);
                            runTimeTaskDetails.getTargetByName(name).setState(TargetState.SKIPPED.toString());
                            runTimeTaskDetails.getTargetByName(name).setRunResult(RunResult.SKIPPED.toString());
                        }
                        incrementSumOfCompletedTargetsBy1();
                    }
                    skippedTargets.add(name);
                    detectSkippedTarget(name, skippedTargets, failedTargetName);
                }
            });
        }
    }

    public String getExecutionName () {
        return this.name;
    }

    // Make the string be printed on the screen as well as to a file
    private Object sendDataToOutputLocked = new Object();
    public void sendDataToOutput(Map<String , String> data) {
        synchronized (sendDataToOutputLocked) {
            outputData.forEach((consumer) -> {
                consumer.accept(data);
            });
            runTimeTaskDetails.setMessage(data);
        }
    }

    public String createFolder(TaskName taskName, String rootDirectory) throws FileSystemException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH.mm.ss");
        String date = simpleDateFormat.format(new Date());
        String fileName = rootDirectory + "/" + taskName + " " + date; // Desired pattern

        File folder = new File(fileName);

        if (folder.mkdirs()) {                          // If a folder was created
            return folder.getAbsolutePath();
        } else {
            throw new FileSystemException("Folder was not created successfully.");
        }
    }

    public void openFile(String targetName) {
        try {
            out = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(filePath + "/" + targetName + ".log")));
        } catch (IOException e) {
        }
    }

    public void closeFile() {
        try {
            out.close();
        } catch (IOException ignored) {
        }
    }

    public void getFreeTargets (Set<String> freeTarget, String targetName) {
        synchronized (name2dependsOnLocalLocked) {
            // If the current target was the last "dependsOn" for another target - a target was freed
            subGraph.name2dependsOn.forEach((name, dependsOn) -> {
                if (dependsOn.contains(targetName) && dependsOn.size() == 1) {
                    freeTarget.add(name);
                }
            });
        }
    }

    Object executionListLocked = new Object();
    public Execution getExecutionDetails () {
        synchronized (executionListLocked) {
            return new Execution(this.name , this.createdBy, this.subGraph.originGraph.getName(), this.taskName, this.sumOfTargets, subGraph.positionCount.get(Position.ROOT), subGraph.positionCount.get(Position.MIDDLE),
                    subGraph.positionCount.get(Position.LEAF), subGraph.positionCount.get(Position.INDEPENDENT),this.pricePerTarget, this.pricePerTarget * this.sumOfTargets, this.workers.size(), this.executionStatus);
        }
    }

    public TaskResult getTaskResult () throws Exception {
        if (this.executionStatus != ExecutionStatus.FINISHED || this.taskResult == null) {
            throw new Exception("You can request a run summary only after the task is completed.");
        }

        return this.taskResult;
    }

    public void createTaskResult () {
        RunResultCounter runResultCounter = new RunResultCounter();
        Map<RunResult, Integer> resultCounter = runResultCounter.getRunResultCounter(runTimeTaskDetails.getRunTimeTargetsDetails());

         this.taskResult = new TaskResult(resultCounter);
    }

    public void setStatus (ExecutionStatus executionStatus) {
        this.executionStatus = executionStatus;
        this.runTimeTaskDetails.setExecutionStatus(executionStatus);

    }

    public int getPricePerTarget() {
        return pricePerTarget;
    }
}

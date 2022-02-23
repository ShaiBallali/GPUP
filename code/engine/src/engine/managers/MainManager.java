package engine.managers;

import dto.dtoServer.execution.DupExecutionDetails;
import dto.dtoServer.execution.ExecutionList;
import dto.dtoServer.execution.NewExecutionDetails;
import dto.dtoServer.graph.GraphList;
import dto.dtoServer.result.TaskResult;
import dto.dtoServer.runtime.RunTimeTaskDetails;
import dto.dtoServer.worker.server2worker.BasicExecutionDetails;
import dto.dtoServer.worker.server2worker.TargetDetails;
import dto.dtoServer.worker.worker2server.RunningResultDetails;
import dto.enums.ExecutionStatus;
import dto.enums.DependencyType;
import engine.graph.Graph;
import engine.ValidationChecks;
import executions.GeneralTask;
import executions.compilation.CompilationTask;
import executions.simulation.SimulationTask;
import jaxb.loadGraph.ConvertDescriptorToGraph;

import java.io.InputStream;
import java.util.*;
import java.util.function.Consumer;

public class MainManager {
    public Map<String, Graph> graphName2graph;
    public Map<String , GeneralTask> executionName2execution;
    public Map<String , RunTimeTaskDetails> executionName2RunTimeDetails;

    private GraphList graphList;
    private String rootDirectory;

    public MainManager(String rootDirectory) {
        this.graphName2graph = new HashMap<>();
        this.executionName2execution = new HashMap<>();
        this.executionName2RunTimeDetails = new HashMap<>();
        this.graphList = new GraphList(0);
        this.rootDirectory = rootDirectory;
    }

    public void loadGraph(String createdBy , InputStream inputStream) throws Exception {
        ConvertDescriptorToGraph convertor = new ConvertDescriptorToGraph(inputStream);
        Graph graph = convertor.getGraph();
        graph.setCreatedBy(createdBy);

        ValidationChecks.loadGraph(graphName2graph, graph.getName().toLowerCase());

        graphName2graph.put(graph.getName(), graph);
        addGraphToGraphList(graph);
    }

    public Object findPaths(String graphName, String src, String dst, DependencyType dependencyType) throws Exception {

        ValidationChecks.isGraphExist(graphName2graph, graphName.toLowerCase());

        return graphName2graph.get(graphName).findPaths(src.toUpperCase(), dst.toUpperCase(), dependencyType);
    }

    public Object detectCircle (String graphName, String targetName) throws Exception {
        ValidationChecks.isGraphExist(graphName2graph, graphName.toLowerCase());

        return graphName2graph.get(graphName).detectCircle(targetName.toUpperCase());
    }

    public Object findWhatIfTargets(String graphName, String targetName, DependencyType dependencyType) throws Exception {
        ValidationChecks.isGraphExist(graphName2graph, graphName.toLowerCase());

        return graphName2graph.get(graphName).findWhatIfTargets(targetName, dependencyType);
    }

    public void addWorkerToExecution (String executionName, String workerName) throws Exception {
        ValidationChecks.subScribeToExecution(executionName2execution, executionName.toLowerCase());

        executionName2execution.get(executionName).addWorker(workerName);
    }

    public void removeWorkerFromExecution (String executionName, String workerName) throws Exception {
        ValidationChecks.isExecutionExist(executionName2execution, executionName.toLowerCase());

        executionName2execution.get(executionName).removeWorker(workerName);
    }

    public Object getExecutionList () {
        ExecutionList executionList = new ExecutionList(executionName2execution.size());

        executionName2execution.forEach((String, execution) -> {
            executionList.addExecution(execution.getExecutionDetails());
        });

        return executionList;
    }

    Object graphListLocked = new Object();
    public void addGraphToGraphList (Graph graph) {
        synchronized (graphListLocked) {
            graphList.addGraph(graph.getGraphDetails());
        }
    }

    public Object getGraphList () {
        synchronized (graphListLocked) {
            return this.graphList;
        }
    }

    public TargetDetails getExecutableTarget (String executionName) throws NoSuchElementException {
        ValidationChecks.isExecutionExist(executionName2execution, executionName.toLowerCase());

        return executionName2execution.get(executionName).getExecutableTarget();
    }

    public BasicExecutionDetails getBasicExecutionDetails (String executionName) {
        ValidationChecks.isExecutionExist(executionName2execution, executionName.toLowerCase());

        GeneralTask task = executionName2execution.get(executionName);
        BasicExecutionDetails basicExecutionDetails = null;

        switch (task.getTaskName()) {
            case SIMULATION:
                basicExecutionDetails = ((SimulationTask)task).getBasicExecutionDetails();
                break;
            case COMPILATION:
                basicExecutionDetails = ((CompilationTask)task).getBasicExecutionDetails();
        }

        return basicExecutionDetails;
    }

    public void onTargetFinishedToRun (RunningResultDetails runningResultDetails) {
        this.executionName2execution.get(runningResultDetails.getExecutionName()).onTargetEndToRun(runningResultDetails);
    }

    public RunTimeTaskDetails getRunTimeTaskDetails(String executionName) {
        return this.executionName2RunTimeDetails.get(executionName.toLowerCase());
    }

    public ExecutionStatus getExecutionStatus(String executionName ){
        return this.executionName2execution.get(executionName.toLowerCase()).getExecutionStatus();
    }

    public void createNewExecution (NewExecutionDetails newExecutionDetails) throws Exception {
        List<Consumer<Map<String, String>>> outputConsumers = new LinkedList<>();
        GeneralTask generalTask = null;

        outputConsumers.add(new Consumer<Map<String, String>>() {
            @Override
            public void accept(Map<String, String> s) {
                for (Map.Entry<String, String> entry : s.entrySet())
                {
                    System.out.println(entry.getKey() + ":");
                    System.out.println(entry.getValue());
                }
            }
        });


        ValidationChecks.newExecution(newExecutionDetails, executionName2execution, graphName2graph);

        switch (newExecutionDetails.getTaskName()) {
            case SIMULATION:
                generalTask = new SimulationTask(newExecutionDetails.getExecutionName().toLowerCase(), newExecutionDetails.getCreatedBy(), newExecutionDetails.getTaskName(), graphName2graph.get(newExecutionDetails.getGraphName().toLowerCase()), outputConsumers, rootDirectory);
                ((SimulationTask)generalTask).init(newExecutionDetails.getTargetProcessingTime(), newExecutionDetails.getIsRandomTime(), newExecutionDetails.getSuccessRate(), newExecutionDetails.getWarningRate(), newExecutionDetails.getTargetsToPerform());
                break;
            case COMPILATION:
                generalTask = new CompilationTask(newExecutionDetails.getExecutionName().toLowerCase(), newExecutionDetails.getCreatedBy(), newExecutionDetails.getTaskName(),  graphName2graph.get(newExecutionDetails.getGraphName().toLowerCase()), outputConsumers, rootDirectory);
                ((CompilationTask)generalTask).init(newExecutionDetails.getSrcPath(), newExecutionDetails.getDstPath(), newExecutionDetails.getTargetsToPerform());
                break;
        }

        this.executionName2execution.put(generalTask.getExecutionName(),generalTask);
        this.executionName2RunTimeDetails.put(generalTask.getExecutionName().toLowerCase(), generalTask.getRunTimeTaskDetails());
    }

    public void duplicateExecution ( DupExecutionDetails duplicateExecutionDetails) throws Exception {
        GeneralTask generalTask = null;

        ValidationChecks.isExecutionExist(executionName2execution, duplicateExecutionDetails.getSrcExecutionName().toLowerCase());

        GeneralTask copyFrom = executionName2execution.get(duplicateExecutionDetails.getSrcExecutionName().toLowerCase());

        switch (copyFrom.getTaskName()) {
            case SIMULATION:
                generalTask = new SimulationTask((SimulationTask)copyFrom, duplicateExecutionDetails.getCreatedBy(), duplicateExecutionDetails.getRunType());
                break;
            case COMPILATION:
                generalTask = new CompilationTask((CompilationTask)copyFrom, duplicateExecutionDetails.getCreatedBy(), duplicateExecutionDetails.getRunType());
        }

        executionName2execution.put(generalTask.getExecutionName(),generalTask);
        this.executionName2RunTimeDetails.put(generalTask.getExecutionName(), generalTask.getRunTimeTaskDetails());

    }

    public int getPrice (String executionName) throws Exception {
        ValidationChecks.isExecutionExist(this.executionName2execution, executionName);

        return this.executionName2execution.get(executionName).getPricePerTarget();
    }

    public void setExecutionStatus (String executionName, ExecutionStatus executionStatus) throws Exception {
        ValidationChecks.isExecutionExist(this.executionName2execution, executionName);

        switch (executionStatus) {
            case PLAYING:
                this.executionName2execution.get(executionName).getStarted();
                break;
            default:
                this.executionName2execution.get(executionName).setStatus(executionStatus == ExecutionStatus.RESUMED ? ExecutionStatus.PLAYING : executionStatus);
                break;
        }

    }

    public boolean didExecutionStop (String executionName) {
        ValidationChecks.isExecutionExist(this.executionName2execution, executionName);

        if (this.executionName2execution.get(executionName).getExecutionStatus() == ExecutionStatus.STOPPED) {
            return true;
        }
        return false;
    }

    public TaskResult getTaskResult (String executionName) throws Exception {
        ValidationChecks.isExecutionExist(this.executionName2execution, executionName);

        return this.executionName2execution.get(executionName).getTaskResult();
    }

    public int getNunOfWorkers(String execution) {
        return this.executionName2execution.get(execution).getNunOfWorkers();
    }

    public int getNumOfTargets(String execution) {
        return this.executionName2RunTimeDetails.get(execution).getSumOfTargets();
    }

    public int getNumOfCompletedTarget(String execution) {
        return this.executionName2RunTimeDetails.get(execution).getSumOfCompletedTargets();
    }

    public boolean isValidName (String executionName) {
        return !this.executionName2execution.containsKey(executionName);
    }

    public void createGraphviz (String graphName, String directoryPath, String fileName) {
        ValidationChecks.isGraphExist(this.graphName2graph, graphName);
        graphName2graph.get(graphName).createGraphviz(directoryPath, fileName);
    }
}

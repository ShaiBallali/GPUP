package engine;

import dto.dtoServer.execution.NewExecutionDetails;
import dto.enums.ExecutionStatus;
import dto.enums.TaskName;
import engine.graph.Graph;
import executions.GeneralTask;


import java.util.Map;
import java.util.NoSuchElementException;

public class ValidationChecks {

    public static void newExecution (NewExecutionDetails newExecutionDetails, Map<String , GeneralTask> executionName2execution, Map<String, Graph> graphName2graph) throws Exception {
        if (executionName2execution.containsKey(newExecutionDetails.getExecutionName().toLowerCase())) {
            throw new Exception ("There is already a task with the name: " + newExecutionDetails.getExecutionName() + ". Please select another name.");
        }

        if (!graphName2graph.containsKey(newExecutionDetails.getGraphName().toLowerCase())) {
            throw new Exception ("There is no graph with name: " +  newExecutionDetails.getGraphName().toLowerCase());
        }

        TaskName taskName = newExecutionDetails.getTaskName();
        Map<String, Integer> task2price = graphName2graph.get(newExecutionDetails.getGraphName().toLowerCase()).task2price;

        if (!task2price.containsKey(taskName.toString().toUpperCase())) {
            throw new Exception ("There is no task with name: " +  taskName.toString().toUpperCase());
        }
    }

    public static void loadGraph (Map<String, Graph> graphName2graph, String graphName ) throws Exception {
        if (graphName2graph.containsKey(graphName)) {
            throw new Exception("Graph with name: " + "\"" + graphName + "\"" + " already exist.");
        }
    }

    public static void isGraphExist (Map<String, Graph> graphName2graph, String graphName) throws NoSuchElementException {
        if (!graphName2graph.containsKey(graphName)) {
            throw new NoSuchElementException("graph with name: " + graphName + " does not exist.");
        }
    }

    public static void isExecutionExist(Map<String, GeneralTask> executionName2execution,String  executionName) throws NoSuchElementException {
        if (!executionName2execution.containsKey(executionName)) {
            throw new NoSuchElementException("Execution with name: " + executionName + " does not exist.");
        }

    }

    public static void subScribeToExecution (Map<String, GeneralTask> executionName2execution,String executionName) throws Exception {
        isExecutionExist(executionName2execution,executionName);

        ExecutionStatus executionStatus = executionName2execution.get(executionName).getExecutionStatus();
        if ( executionStatus == ExecutionStatus.FINISHED || executionStatus == ExecutionStatus.STOPPED) {
            throw new Exception("It is not possible to subscribe to execution that have been completed or stopped");
        }
    }
}

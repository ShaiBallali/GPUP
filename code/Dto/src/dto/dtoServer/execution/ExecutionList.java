package dto.dtoServer.execution;

import java.util.ArrayList;
import java.util.List;

public class ExecutionList {
    private final Execution[] executions;
    private int logSize;

    public ExecutionList(int size) {
        this.executions = new Execution[size];
        this.logSize = 0;
    }

    public void addExecution(Execution execution) {
        this.executions[logSize++] = execution;
    }

    public List<TableExecution> getExecution() {
        List<TableExecution> executionsList = new ArrayList<>();
        for (int i = 0; i < logSize; ++i) {
            Execution currExecution = executions[i];
            TableExecution currTableExecution = new TableExecution(currExecution.getName(), currExecution.getCreatedBy(), currExecution.getGraphName(),
                    currExecution.getTargetAmount(), currExecution.getRootCount(), currExecution.getMiddleCount(), currExecution.getLeafCount(),
                    currExecution.getIndependentsCount(), currExecution.getTotalPrice(), currExecution.getTotalWorkers(), currExecution.getExecutionStatus());
            executionsList.add(currTableExecution);
        }
        return executionsList;
    }

    public int getLogSize(){
        return logSize;
    }

    public Execution[] getExecutions() {
        return executions;
    }
}

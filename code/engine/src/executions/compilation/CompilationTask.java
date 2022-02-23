package executions.compilation;


import dto.dtoServer.worker.server2worker.BasicExecutionDetails;
import dto.enums.RunType;
import dto.enums.TaskName;
import engine.graph.Graph;
import executions.GeneralTask;

import java.nio.file.FileSystemException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class CompilationTask extends GeneralTask {

    private String srcPath;
    private String dstPath;

    public CompilationTask(String name, String createdBy, TaskName taskName, Graph graph, List<Consumer <Map<String, String>>> outputData, String rootDirectory) {
        super(name, createdBy, taskName, graph,outputData, rootDirectory);
    }

    public CompilationTask (CompilationTask copyFrom, String createdBy, RunType runType) {
        super(copyFrom, createdBy, runType);
        this.srcPath = copyFrom.getSrcPath();
        this.dstPath = copyFrom.getDstPath();
    }

    public void init (String srcPath, String dstPath, Set<String> targetsToPerform) throws FileSystemException  {
        this.srcPath = srcPath;
        this.dstPath = dstPath;
        generalInit(targetsToPerform);
    }

    public String getSrcPath() {
        return srcPath;
    }

    public String getDstPath() {
        return dstPath;
    }

    public BasicExecutionDetails getBasicExecutionDetails () {
        return new BasicExecutionDetails(this.name, this.taskName, this.getPricePerTarget(), 0, false, 0, 0, this.srcPath, this.dstPath );
    }
}

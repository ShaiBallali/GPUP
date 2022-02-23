package engine.job;

import dto.dtoServer.worker.workerengine2worker.RunTimeTargetsDetails;
import dto.enums.RunResult;
import engine.RegisteredExecutions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CompilationJob extends GeneralJob {

    private String srcPath;
    private String dstPath;

    public CompilationJob(RunTimeTargetsDetails runTimeTargetsDetails, String targetName, String generalInfo, RegisteredExecutions registeredExecutions) {
        super(runTimeTargetsDetails, targetName,generalInfo, registeredExecutions.getName());

        this.srcPath = registeredExecutions.getSrcPath();
        this.dstPath = registeredExecutions.getDstPath();
    }

    @Override
    public void specificJob() {
        int result = 1;
        String filePath = createFQN(generalInfo);

        String compilationLine = "javac -d " + dstPath + " -cp " + dstPath + " " + filePath;

        this.logsManager.addToLogs("Data on " + targetName + ": file data: " + filePath );

        String[] command = {"cmd.exe", "/c", compilationLine };

        this.logsManager.addToLogs("Compiler is going to execute: " + compilationLine);

        this.runTimeTargetsDetails.addLogRT(this.targetName, this.executionName, this.logsManager.getLogs());

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = null;

        try {
            Long compilerStart = System.currentTimeMillis();

            process = processBuilder.start();
            process.waitFor();

            Long compilerEnd = System.currentTimeMillis();

            this.logsManager.addToLogs("Compiler is working for " + (compilerEnd - compilerStart) + " ms.");

        } catch (Exception ignored) {}

        result = process.exitValue();
        if (result != 0) {             // the task failure
            try {
                String errorMessage = new BufferedReader(
                        new InputStreamReader(process.getErrorStream()))
                        .readLine();

                this.logsManager.addToLogs("Compilation failed. Error message: " + errorMessage);
                this.runResult = RunResult.FAILURE;
            } catch (IOException ignored) {}
        }
        else {
            this.runResult = RunResult.SUCCESS;
        }

        this.runTimeTargetsDetails.addLogRT(this.targetName, this.executionName, this.logsManager.getLogs());
    }

    private String createFQN (String relativePath) {
        relativePath = relativePath.replace(".", "/");
        String FQN = srcPath + "/" + relativePath + ".java";
        return FQN;
    }
}

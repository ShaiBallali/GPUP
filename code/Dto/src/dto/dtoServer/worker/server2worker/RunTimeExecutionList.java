package dto.dtoServer.worker.server2worker;

import dto.dtoServer.worker.workerengine2worker.RunTimeTargetsDetails;

public class RunTimeExecutionList {

    private RunTimeExecutionDetails[] runTimeExecutionsDetails;
    private int logSize;
    private int phzSize;

    public RunTimeExecutionList (int size) {
        this.runTimeExecutionsDetails = new RunTimeExecutionDetails[size];
        this.logSize = 0 ;
        this.phzSize = size;
    }

    public void addToRunTimeExecutionsDetails ( RunTimeExecutionDetails runTimeExecutionDetails) {
        if ( this.logSize == this.phzSize ) {
            RunTimeExecutionDetails[] temp = new RunTimeExecutionDetails[++phzSize];
            int i = 0;
            for (RunTimeExecutionDetails item : runTimeExecutionsDetails) {
                temp[i++] = item;
            }
            this.runTimeExecutionsDetails = temp;
        }
         this.runTimeExecutionsDetails[logSize++] = runTimeExecutionDetails;
    }

    public RunTimeExecutionDetails[] getRunTimeExecutionsDetails() {
        return runTimeExecutionsDetails;
    }
}

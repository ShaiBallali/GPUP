package dto.dtoServer.graph;

public class GraphList {
    public GraphDetails[] graphDetails;
    public int logSize;
    public int size;

    public GraphList(int size) {
        this.logSize = 0;
        this.size = size;
        graphDetails = new GraphDetails[size];
    }

    public void addGraph(GraphDetails graphDetails) {
        if (this.size == logSize) {
            GraphDetails[] newGraphDetails = new GraphDetails[++size];
            int i = 0;
            for (GraphDetails graph : this.graphDetails) {
                newGraphDetails[i++] = graph;
            }
            this.graphDetails = newGraphDetails;
        }
        this.graphDetails[logSize++] = graphDetails;
    }

    public GraphDetails[] getGraphDetails() {
        return this.graphDetails;
    }

    public int getLogSize(){
        return logSize;
    }
}

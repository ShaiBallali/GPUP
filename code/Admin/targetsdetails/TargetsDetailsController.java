package targetsdetails;

import Util.Constants;
import Util.http.HttpClientUtil;
import datatable.DataTableController;
import dto.dtoServer.graph.GraphDetails;
import dto.dtoServer.graph.GraphList;
import dto.dtoServer.graph.TargetDetails;
import dto.graph.TargetDetailsJavaFX;
import graphs.TableGraph;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import main.AdminAppController;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TargetsDetailsController {

    private AdminAppController adminAppController;
    private final List<TargetDetailsJavaFX> targetDetailsList;
    private String currGraphName;

    @FXML private Label allTargetsLbl;
    @FXML private Label rootsLbl;
    @FXML private Label middleLbl;
    @FXML private Label leavesLbl;
    @FXML private Label independentsLbl;
    @FXML private StackPane tablePlaceHolder;
    @FXML private Label displayedGraphLbl;
    @FXML private Label msgLbl;

    private final SimpleIntegerProperty allTargets;
    private final SimpleIntegerProperty roots;
    private final SimpleIntegerProperty middles;
    private final SimpleIntegerProperty leaves;
    private final SimpleIntegerProperty independents;

    public TargetsDetailsController(){
        targetDetailsList = new ArrayList<>();
        allTargets = new SimpleIntegerProperty();
        roots = new SimpleIntegerProperty();
        middles = new SimpleIntegerProperty();
        leaves = new SimpleIntegerProperty();
        independents = new SimpleIntegerProperty();
    }

    @FXML public void initialize(){
        allTargetsLbl.textProperty().bind(Bindings.format("%,d", allTargets));
        rootsLbl.textProperty().bind(Bindings.format("%,d", roots));
        middleLbl.textProperty().bind(Bindings.format("%,d", middles));
        leavesLbl.textProperty().bind(Bindings.format("%,d", leaves));
        independentsLbl.textProperty().bind(Bindings.format("%,d", independents));
    }

    @FXML
    void detectCircleBtnAction(ActionEvent event) {
        adminAppController.setDetectCirclesPage();
    }

    @FXML
    void findPathBtnAction(ActionEvent event) {
        adminAppController.setFindPathPage();
    }

    @FXML
    void whatIfBtnAction(ActionEvent event) {
        adminAppController.setWhatIfPage();
    }

    public void setAdminAppController(AdminAppController adminAppController) {
        this.adminAppController = adminAppController;
    }


    private ArrayList<TableGraph> graphListToListOfGraphs(GraphList graphNames) {
        GraphDetails[] graphs = graphNames.getGraphDetails();
        ArrayList<TableGraph> tableGraphs = new ArrayList<>();
        for (int i = 0; i < graphNames.getLogSize(); ++i) {
            GraphDetails currGraph = graphs[i];
            TableGraph curr = new TableGraph(currGraph.getName(), currGraph.getCreatedBy(), currGraph.getTargetAmount(), currGraph.getRootCount(),
                    currGraph.getMiddleCount(), currGraph.getLeafCount(), currGraph.getIndependentCount(), currGraph.getTaskPrices(), currGraph.getTargetDetails(), currGraph.getTargetsNames()); //TableTask
            tableGraphs.add(curr);
        }
        return tableGraphs;

    }

    public List<TargetDetailsJavaFX> targetDetailsArrayToListOfTargetDetails(TableGraph tableGraph) {
        TargetDetails[] targetDetails = tableGraph.getTargetDetails();
        List<TargetDetailsJavaFX> targetDetailsList = new ArrayList<>();
        for (int i = 0; i < tableGraph.getTargetAmount(); ++i) {
            TargetDetails currTargetDetails = targetDetails[i];
            TargetDetailsJavaFX curr = new TargetDetailsJavaFX(currTargetDetails.getName(), currTargetDetails.getPosition(), currTargetDetails.getGeneralInfo(),
                    currTargetDetails.getAllDependsOn(), currTargetDetails.getDirectDependsOn(),
                    currTargetDetails.getDirectRequiredFor(), currTargetDetails.getAllRequiredFor());
            targetDetailsList.add(curr);
        }
        return targetDetailsList;
    }


    public void setTables(StackPane dataTableComponent, DataTableController dataTableController) {
        tablePlaceHolder.getChildren().clear();

        Response response = HttpClientUtil.runSync(Constants.GRAPH_LIST);
        try {
            String jsonArrayOfGraphNames = response.body().string();
            GraphList graphNames = Constants.GSON_INSTANCE.fromJson(jsonArrayOfGraphNames, GraphList.class);
            ArrayList<TableGraph> graphList = new ArrayList<>(graphListToListOfGraphs(graphNames));
            TableGraph chosenTableGraph = adminAppController.getChosenTableGraph();
            for (TableGraph tableGraph : graphList) {
                if (tableGraph.getGraphName().equals(chosenTableGraph.getGraphName())) {
                    targetDetailsList.clear();
                    targetDetailsList.addAll(targetDetailsArrayToListOfTargetDetails(tableGraph));
                    currGraphName = tableGraph.getGraphName();
                    displayedGraphLbl.setText(currGraphName);
                    allTargets.set(tableGraph.getTargetAmount());
                    independents.set(tableGraph.getIndependentsCount());
                    leaves.set(tableGraph.getLeafCount());
                    roots.set(tableGraph.getRootCount());
                    middles.set(tableGraph.getMiddleCount());
                }
            }

            dataTableController.setAllTargetsDetails(targetDetailsList);
            tablePlaceHolder.getChildren().add(dataTableComponent);
        }
        catch(IOException e){
            msgLbl.setText("Error: " + e.getMessage());
        }

    }

}

package detectCircle;

import Util.Constants;
import Util.http.HttpClientUtil;
import com.google.gson.stream.JsonReader;
import datatable.DataTableController;
import dto.dtoServer.graph.GraphDetails;
import dto.dtoServer.graph.GraphList;
import dto.dtoServer.graph.TargetDetails;
import dto.dtoServer.graphAction.CirclePath;
import dto.graph.TargetDetailsJavaFX;
import graphs.TableGraph;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import main.AdminAppController;
import okhttp3.HttpUrl;
import okhttp3.Response;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class DetectCirclesController {

    private final List<String> targetsNamesList;
    private final List<TargetDetailsJavaFX> targetDetailsList;
    private String currGraphName;

    private final SimpleBooleanProperty isTargetSelectedProperty;
    private final SimpleStringProperty msgProperty;
    private String responseBody;

    @FXML private ComboBox targetSelectionCB;
    @FXML private Button detectCirclesBtn;
    @FXML private TextArea detectCircleMsg;
    @FXML private StackPane tablePlaceHolder;
    @FXML private Label displayedGraphLbl;

    private AdminAppController adminAppController;



    public DetectCirclesController(){
        isTargetSelectedProperty = new SimpleBooleanProperty(false);
        msgProperty = new SimpleStringProperty();

        targetDetailsList = new ArrayList<>();
        targetsNamesList = new ArrayList<>();
    }

    @FXML void detectCirclesAction(ActionEvent event) {
            try {
                String finalUrl = HttpUrl
                        .parse(Constants.DETECT_CIRCLE)
                        .newBuilder()
                        .addQueryParameter("graphName", currGraphName)
                        .addQueryParameter("targetName", targetSelectionCB.getValue().toString())
                        .build()
                        .toString();

                Response response = HttpClientUtil.runSync(finalUrl);
                responseBody = response.body().string();
                if (response.code() != 200) {

                    Platform.runLater(() ->
                            msgProperty.set("Something went wrong: " + responseBody)
                    );
                } else {
                    Platform.runLater(() -> {
                        try {
                            JsonReader reader = new JsonReader(new StringReader(responseBody));
                            reader.setLenient(true);
                            CirclePath circlePath = Constants.GSON_INSTANCE.fromJson(reader,CirclePath.class);
                            printPath(circlePath);
                        } catch (Exception e) {
                            msgProperty.set("Something went wrong: " + e.getMessage());
                        }

                    });
                }

            }
            catch(Exception e){
                msgProperty.set(e.getMessage());
            }

    }
    @FXML void chooseTargetCBAction(ActionEvent event) {
        if (targetSelectionCB.buttonCellProperty()!=null) { // If ComboBox is chosen (not null)
            isTargetSelectedProperty.set(true);
        }
    }
    @FXML public void initialize() {
        detectCirclesBtn.disableProperty().bind(isTargetSelectedProperty.not()); // Not letting the user press detect unless a target was chosen
        detectCircleMsg.textProperty().bind(msgProperty);
    }

    private void printPath(CirclePath pathDetails) {
        List<String> path = pathDetails.getPath();
        String targetName = (String)targetSelectionCB.getValue(); // Get selected target
        String msg = "";
        if (path.isEmpty()) {
            msg = msg.concat(targetName + " is not in a circle\n");
        } else  {
            msg = msg.concat("There is a circle:\n");
            boolean isFirst = true;
            for (String name : path) {
                if (!isFirst)
                    msg = msg.concat(" -> ");
                msg = msg.concat(name);
                isFirst = false;
            }
            msg = msg.concat("\n");
        }
        msgProperty.set(msg);
    }

    private void setTargetsNames() {
        // Setting items for Combo Boxes
        targetSelectionCB.setItems(FXCollections.observableArrayList(targetsNamesList));
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

    public void setTables(StackPane dataTableComponent, DataTableController dataTableController){
        tablePlaceHolder.getChildren().clear();
        try {
            Response response = HttpClientUtil.runSync(Constants.GRAPH_LIST);
            String jsonArrayOfGraphNames = response.body().string();
            GraphList graphNames = Constants.GSON_INSTANCE.fromJson(jsonArrayOfGraphNames, GraphList.class); // TaskList.class
            ArrayList<TableGraph> graphList = new ArrayList<>(graphListToListOfGraphs(graphNames));
            TableGraph chosenTableGraph = adminAppController.getChosenTableGraph();
            for (TableGraph tableGraph : graphList) {
                if (tableGraph.getGraphName().equals(chosenTableGraph.getGraphName())) {
                    targetDetailsList.clear();
                    targetDetailsList.addAll(targetDetailsArrayToListOfTargetDetails(tableGraph));
                    targetsNamesList.addAll(stringArrayToListOfStrings(tableGraph));
                    setTargetsNames();
                    currGraphName = tableGraph.getGraphName();
                    displayedGraphLbl.setText(currGraphName);
                }
            }
        }
        catch(IOException e){
            msgProperty.set(e.getMessage());
        }

        dataTableController.setAllTargetsDetails(targetDetailsList);
        tablePlaceHolder.getChildren().add(dataTableComponent);
    }

    public void clear(){ // Clear before next press on Detect Circle button (we want a fresh screen)
        targetSelectionCB.getSelectionModel().clearSelection();
        isTargetSelectedProperty.set(false);
        msgProperty.set("");
    }

    public void setAdminAppController(AdminAppController adminAppController) {
        this.adminAppController = adminAppController;
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

    public List<String> stringArrayToListOfStrings(TableGraph tableGraph) {
        String[] targetNames = tableGraph.getTargetNames();
        List<String> targetNamesList = new ArrayList<>();
        for (int i = 0; i < tableGraph.getTargetAmount(); ++i) {
            String curr = targetNames[i];
            targetNamesList.add(curr);
        }
        return targetNamesList;
    }
}
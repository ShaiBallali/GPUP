package findPath;

import Util.Constants;
import Util.http.HttpClientUtil;
import com.google.gson.stream.JsonReader;
import datatable.DataTableController;
import dto.dtoServer.graph.GraphDetails;
import dto.dtoServer.graph.GraphList;
import dto.dtoServer.graph.TargetDetails;
import dto.dtoServer.graphAction.TargetsPaths;
import dto.enums.DependencyType;
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
import java.util.Arrays;
import java.util.List;

public class FindPathController {
    private final List<String> targetsNamesList;
    private final ArrayList<String> dependency;
    private AdminAppController adminAppController;
    private final List<TargetDetailsJavaFX> targetDetailsList;
    private String currGraphName;
    private String responseBody;

    private final SimpleBooleanProperty isSourceTargetSelectedProperty;
    private final SimpleBooleanProperty isDestTargetSelectedProperty;
    private final SimpleBooleanProperty areBothTargetsSelectedProperty;
    private final SimpleBooleanProperty isDependencySelectedProperty;
    private final SimpleStringProperty statusMsgProperty;

    @FXML private ComboBox sourceTargetCB;
    @FXML private ComboBox destTargetCB;
    @FXML private ComboBox dependencyCB;
    @FXML private Button findPathBtn;
    @FXML private TextArea findPathMsg;
    @FXML private StackPane tablePlaceHolder;
    @FXML private Label displayedGraphLbl;


    public FindPathController() {
        isSourceTargetSelectedProperty = new SimpleBooleanProperty(false);
        isDestTargetSelectedProperty = new SimpleBooleanProperty(false);
        areBothTargetsSelectedProperty = new SimpleBooleanProperty(false);
        isDependencySelectedProperty = new SimpleBooleanProperty(false);
        statusMsgProperty = new SimpleStringProperty();
        dependency = new ArrayList<>();
        targetDetailsList = new ArrayList<>();
        targetsNamesList = new ArrayList<>();
    }

    public void initialize() {
        dependencyCB.disableProperty().bind(areBothTargetsSelectedProperty.not());
        findPathBtn.disableProperty().bind(isDependencySelectedProperty.not());
        findPathMsg.textProperty().bind(statusMsgProperty);
        dependency.add(DependencyType.DEPENDS_ON.toString()); // Adding to Dependency ComboBox
        dependency.add(DependencyType.REQUIRED_FOR.toString());

    }

    @FXML
    void chooseDestTargetCB(ActionEvent event) {
        if (destTargetCB.buttonCellProperty() != null) {
            isDestTargetSelectedProperty.set(true);
            areBothTargetsSelectedProperty.bind(isSourceTargetSelectedProperty); // If destination target was selected, only source target needs to be selected
        }
    }

    @FXML
    void chooseSourceTargetCB(ActionEvent event) {
        if (sourceTargetCB.buttonCellProperty() != null) {
            isSourceTargetSelectedProperty.set(true);
            areBothTargetsSelectedProperty.bind(isDestTargetSelectedProperty); // reverse condition to chooseDestTargetCB
        }
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

    public List<String> stringArrayToListOfStrings(TableGraph tableGraph) {
        String[] targetNames = tableGraph.getTargetNames();
        return new ArrayList<>(Arrays.asList(targetNames).subList(0, tableGraph.getTargetAmount()));
    }


    @FXML
    void findPathBtnAction(ActionEvent event) {
        try {
            String finalUrl = HttpUrl
                    .parse(Constants.FIND_PATH)
                    .newBuilder()
                    .addQueryParameter("graphName", currGraphName)
                    .addQueryParameter("srcName", sourceTargetCB.getValue().toString())
                    .addQueryParameter("dstName", destTargetCB.getValue().toString())
                    .addQueryParameter("dependencyType", dependencyCB.getValue().toString())
                    .build()
                    .toString();

            Response response = HttpClientUtil.runSync(finalUrl);
            responseBody = response.body().string();
            if (response.code() != 200) {

                Platform.runLater(() ->
                        statusMsgProperty.set("Something went wrong: " + responseBody)
                );
            } else {
                Platform.runLater(() -> {
                    try {
                        JsonReader reader = new JsonReader(new StringReader(responseBody));
                        reader.setLenient(true);
                        TargetsPaths targetsPaths = Constants.GSON_INSTANCE.fromJson(reader,TargetsPaths.class);
                        if (targetsPaths.getAllPaths()!= null)
                            printAllPaths(targetsPaths.getAllPaths());
                        else {
                            String dependencyType = (String) dependencyCB.getValue();
                            String srcName = (String) sourceTargetCB.getValue();
                            String dstName = (String) destTargetCB.getValue();
                            statusMsgProperty.set("There is no valid " + dependencyType + " path between " + srcName.toUpperCase() + " and " + dstName.toUpperCase() + ".\n");
                        }
                     } catch (Exception e) {
                        statusMsgProperty.set("Something went wrong: " + e.getMessage());
                    }
                });
            }

        }
        catch(Exception e){
            statusMsgProperty.set(e.getMessage());
        }
    }

    @FXML
    void chooseDependencyCB(ActionEvent event) {
        // We can choose dependency type only after we chose both targets
        if (sourceTargetCB.buttonCellProperty() != null)
            isDependencySelectedProperty.set(true);

    }


    public void setAdminAppController(AdminAppController adminAppController) {
        this.adminAppController = adminAppController;
    }

    private void printAllPaths(List<List<String>> allPaths) {
        String dependencyType = (String) dependencyCB.getValue();
        String srcName = (String) sourceTargetCB.getValue();
        String dstName = (String) destTargetCB.getValue();

        String message = "";

        if (allPaths.isEmpty()) {
            message = message.concat("There is no valid " + dependencyType + " path between " + srcName.toUpperCase() + " and " + dstName.toUpperCase() + ".\n");
        } else {
            String grammarPath; // Plural -> print paths, singular -> print path
            if (allPaths.size() > 1) grammarPath = " paths";
            else grammarPath = " path";

            message = message.concat(dependencyType + grammarPath + " between " + srcName.toUpperCase() + " and " + dstName.toUpperCase() + ":\n");
            for (List<String> list : allPaths) {
                for (String name : list) {
                    if (!name.equals(list.get(0))) // Do not print -> prior to the first target (e.g -> A -> B)
                        message = message.concat(" -> ");
                    message = message.concat(name);
                }
                message = message.concat("\n");
            }
        }

        statusMsgProperty.set(message);
    }

    private void setTargetsNames() {
        // Setting items for Combo Boxes
        sourceTargetCB.setItems(FXCollections.observableArrayList(targetsNamesList));
        destTargetCB.setItems(FXCollections.observableArrayList(targetsNamesList));
        dependencyCB.setItems(FXCollections.observableArrayList(dependency));


    }

    public void setTables(StackPane dataTableComponent, DataTableController dataTableController) {
        tablePlaceHolder.getChildren().clear();
        try {
            Response response = HttpClientUtil.runSync(Constants.GRAPH_LIST);
            String jsonArrayOfGraphNames = response.body().string();
            GraphList graphNames = Constants.GSON_INSTANCE.fromJson(jsonArrayOfGraphNames, GraphList.class);
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
        } catch (IOException e) {
            statusMsgProperty.set(e.getMessage());
        }

        dataTableController.setAllTargetsDetails(targetDetailsList);
        tablePlaceHolder.getChildren().add(dataTableComponent);

    }

    public void clear(){ // Refreshing page
        dependencyCB.getSelectionModel().clearSelection();
        sourceTargetCB.getSelectionModel().clearSelection();
        destTargetCB.getSelectionModel().clearSelection();
        isDependencySelectedProperty.set(false);
        isSourceTargetSelectedProperty.set(false);
        isDestTargetSelectedProperty.set(false);
        statusMsgProperty.set("");
    }


}




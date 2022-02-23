package dashboard;

import Util.Constants;

import configuration.GsonConfig;
import configuration.HttpConfig;
import dto.dtoServer.execution.DupExecutionDetails;
import dto.dtoServer.execution.TableExecution;
import dto.enums.ExecutionStatus;
import dto.enums.RunType;
import dto.enums.TaskName;
import graphs.TableGraph;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import main.AdminAppController;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.RequestBody;
import users.TableUser;

import java.util.*;

public class DashboardController {

    private AdminAppController adminAppController;

    private Timer timer;
    private TimerTask listRefresher;
    private final BooleanProperty autoUpdate;
    private final IntegerProperty totalUsers;
    private String chosenGraph;
    private TableExecution chosenExecution;
    private Map<String, TableExecution> nameToTableExecution;
    private SimpleBooleanProperty isCompilationEnabled, isSimulationEnabled, isExecutionControlPanelEnabled, isRunTypeEnabled;


    private final String EMPTY_STRING = "";
    private final String UPLOADING_USER_LBL = "Uploading User: ";
    private final String TOTAL_TARGETS_LBL = "Total Number of Target: ";
    private final String INDEPENDENT_LBL = "Total Independent: ";
    private final String LEAF_LBL = "Total Leaves: ";
    private final String ROOT_LBL = "Total Roots: ";
    private final String MIDDLE_LBL = "Total Middle: ";
    private final String COMPILATION_PRICE_LBL = "Compilation Price: ";
    private final String SIMULATION_PRICE_LBL = "Simulation Price: ";
    private final String CHOSEN_GRAPH_LBL = "Chosen Graph: ";
    private final String CHOSEN_EXECUTION_LBL = "Chosen Execution: ";

    @FXML private Label chosenGraphLbl;
    @FXML private Label uploadingUserLbl;
    @FXML private Label totalTargetsLbl;
    @FXML private Label independentLbl;
    @FXML private Label leafLbl;
    @FXML private Label middleLbl;
    @FXML private Label rootLbl;
    @FXML private Label compilationPrice;
    @FXML private Label simulationPrice;

    @FXML private Button createSimulationBtn;
    @FXML private Button createCompilationBtn;
    @FXML private Button executionControlBtn;
    @FXML private Button fromScratchBtn;
    @FXML private Button incrementalBtn;




    public DashboardController() {
        autoUpdate = new SimpleBooleanProperty();
        totalUsers = new SimpleIntegerProperty();
        isCompilationEnabled = new SimpleBooleanProperty(false);
        isSimulationEnabled = new SimpleBooleanProperty(false);
        isExecutionControlPanelEnabled = new SimpleBooleanProperty(false);
        isRunTypeEnabled = new SimpleBooleanProperty(false);
        nameToTableExecution = new HashMap<>();

    }

    @FXML
    public void initialize() {
        graphnameColumn.setCellValueFactory(new PropertyValueFactory<TableGraph, String>("graphName"));
        //------//
        usernameColumn.setCellValueFactory(new PropertyValueFactory<TableUser, String>("username"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<TableUser, String>("userType"));
        //------//
        executionNameColumn.setCellValueFactory(new PropertyValueFactory<TableExecution, String>("executionName"));
        createdByColumn.setCellValueFactory(new PropertyValueFactory<TableExecution, String>("createdBy"));
        graphExecutionNameColumn.setCellValueFactory(new PropertyValueFactory<TableExecution, String>("graphName"));
        targetAmountColumn.setCellValueFactory(new PropertyValueFactory<TableExecution, Integer>("targetAmount"));
        rootsColumn.setCellValueFactory(new PropertyValueFactory<TableExecution, Integer>("rootCount"));
        middlesColumn.setCellValueFactory(new PropertyValueFactory<TableExecution, Integer>("middleCount"));
        leavesColumn.setCellValueFactory(new PropertyValueFactory<TableExecution, Integer>("leafCount"));
        independentsColumn.setCellValueFactory(new PropertyValueFactory<TableExecution, Integer>("independentCount"));
        totalPriceColumn.setCellValueFactory(new PropertyValueFactory<TableExecution, Integer>("totalPrice"));
        totalWorkersColumn.setCellValueFactory(new PropertyValueFactory<TableExecution, Integer>("totalWorkers"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<TableExecution, String>("status"));
        //------//
        executionNameColumn1.setCellValueFactory(new PropertyValueFactory<TableExecution, String>("executionName"));
        createdByColumn1.setCellValueFactory(new PropertyValueFactory<TableExecution, String>("createdBy"));
        graphExecutionNameColumn1.setCellValueFactory(new PropertyValueFactory<TableExecution, String>("graphName"));
        targetAmountColumn1.setCellValueFactory(new PropertyValueFactory<TableExecution, Integer>("targetAmount"));
        rootsColumn1.setCellValueFactory(new PropertyValueFactory<TableExecution, Integer>("rootCount"));
        middlesColumn1.setCellValueFactory(new PropertyValueFactory<TableExecution, Integer>("middleCount"));
        leavesColumn1.setCellValueFactory(new PropertyValueFactory<TableExecution, Integer>("leafCount"));
        independentsColumn1.setCellValueFactory(new PropertyValueFactory<TableExecution, Integer>("independentCount"));
        totalPriceColumn1.setCellValueFactory(new PropertyValueFactory<TableExecution, Integer>("totalPrice"));
        totalWorkersColumn1.setCellValueFactory(new PropertyValueFactory<TableExecution, Integer>("totalWorkers"));
        statusColumn1.setCellValueFactory(new PropertyValueFactory<TableExecution, String>("status"));
        //------//
        createSimulationBtn.disableProperty().bind(isSimulationEnabled.not());
        createCompilationBtn.disableProperty().bind(isCompilationEnabled.not());
        executionControlBtn.disableProperty().bind(isExecutionControlPanelEnabled.not());
        fromScratchBtn.disableProperty().bind(isRunTypeEnabled.not());
        incrementalBtn.disableProperty().bind(isRunTypeEnabled.not());
        //------//
        compilationPrice.textProperty().addListener((observable, oldValue, newValue) -> {
            isCompilationEnabled.set(!newValue.equals(""));
        });

        simulationPrice.textProperty().addListener((observable, oldValue, newValue) -> {
            isSimulationEnabled.set(!newValue.equals(""));
        });
    }



    private void setLabelsActive(TableGraph tableGraph) {
        chosenGraphLbl.setText(CHOSEN_GRAPH_LBL + chosenGraph);
        uploadingUserLbl.setText(UPLOADING_USER_LBL + tableGraph.getCreatedBy());
        totalTargetsLbl.setText(TOTAL_TARGETS_LBL + tableGraph.getTargetAmount());
        independentLbl.setText(INDEPENDENT_LBL + tableGraph.getIndependentsCount());
        rootLbl.setText(ROOT_LBL + tableGraph.getRootCount());
        middleLbl.setText(MIDDLE_LBL + tableGraph.getMiddleCount());
        leafLbl.setText(LEAF_LBL + tableGraph.getLeafCount());
        if (tableGraph.getTaskPrices().length == 1) {
            if (tableGraph.getTaskPrices()[0].getName().equals(TaskName.COMPILATION.toString())) {
                compilationPrice.setText(COMPILATION_PRICE_LBL + String.valueOf(tableGraph.getTaskPrices()[0].getPrice()));
            } else {
                simulationPrice.setText(SIMULATION_PRICE_LBL + String.valueOf(tableGraph.getTaskPrices()[0].getPrice()));
            }
        } else if (tableGraph.getTaskPrices().length > 1) {
            compilationPrice.setText(COMPILATION_PRICE_LBL + String.valueOf(tableGraph.getTaskPrices()[0].getPrice()));
            simulationPrice.setText(SIMULATION_PRICE_LBL + String.valueOf(tableGraph.getTaskPrices()[0].getPrice()));
        }
        adminAppController.getGraphActionsButton().setDisable(false);
        adminAppController.setChosenTableGraph(tableGraph);


    }

    private void setLabelsInActive() {
        chosenGraphLbl.setText(EMPTY_STRING);
        uploadingUserLbl.setText(EMPTY_STRING);
        totalTargetsLbl.setText(EMPTY_STRING);
        independentLbl.setText(EMPTY_STRING);
        rootLbl.setText(EMPTY_STRING);
        middleLbl.setText(EMPTY_STRING);
        leafLbl.setText(EMPTY_STRING);
        compilationPrice.setText(EMPTY_STRING);
        simulationPrice.setText(EMPTY_STRING);
        adminAppController.getGraphActionsButton().setDisable(true);
        isExecutionControlPanelEnabled.set(false);
        isRunTypeEnabled.set(false);
    }


    //Execution table
    @FXML private TableView<TableUser> usersTable;
    @FXML private TableView<TableExecution> executionTable;
    @FXML private TableColumn<TableExecution, String> executionNameColumn;
    @FXML private TableColumn<TableExecution, String> createdByColumn;
    @FXML private TableColumn<TableExecution, String> graphExecutionNameColumn;
    @FXML private TableColumn<TableExecution, Integer> targetAmountColumn;
    @FXML private TableColumn<TableExecution, Integer> rootsColumn;
    @FXML private TableColumn<TableExecution, Integer> middlesColumn;
    @FXML private TableColumn<TableExecution, Integer> leavesColumn;
    @FXML private TableColumn<TableExecution, Integer> independentsColumn;
    @FXML private TableColumn<TableExecution, Integer> totalPriceColumn;
    @FXML private TableColumn<TableExecution, Integer> totalWorkersColumn;
    @FXML private TableColumn<TableExecution, String> statusColumn;

    //Execution made by current user table
    @FXML private TableView<TableExecution> selfExecutionTable;
    @FXML private TableColumn<TableExecution, String> executionNameColumn1;
    @FXML private TableColumn<TableExecution, String> createdByColumn1;
    @FXML private TableColumn<TableExecution, String> graphExecutionNameColumn1;
    @FXML private TableColumn<TableExecution, Integer> targetAmountColumn1;
    @FXML private TableColumn<TableExecution, Integer> rootsColumn1;
    @FXML private TableColumn<TableExecution, Integer> middlesColumn1;
    @FXML private TableColumn<TableExecution, Integer> leavesColumn1;
    @FXML private TableColumn<TableExecution, Integer> independentsColumn1;
    @FXML private TableColumn<TableExecution, Integer> totalPriceColumn1;
    @FXML private TableColumn<TableExecution, Integer> totalWorkersColumn1;
    @FXML private TableColumn<TableExecution, String> statusColumn1;



    @FXML private TableColumn<TableGraph, String> graphnameColumn;
    @FXML private TableColumn<TableUser, String> usernameColumn;
    @FXML private TableColumn<TableUser, String> roleColumn;
    @FXML private TableView<TableGraph> graphsTable;


    // Updaters
    private void updateUsersList(List<TableUser> usersNames) {
        Platform.runLater(() -> {
            ObservableList<TableUser> usersData = FXCollections.observableArrayList(usersNames);
            usersTable.getItems().clear();
            usersTable.setItems(usersData);
            totalUsers.set(usersNames.size());
        });
    }

    private void updateGraphsList(List<TableGraph> graphsNames) {
        Platform.runLater(() -> {
            ObservableList<TableGraph> items = graphsTable.getItems();
            items.clear();
            items.addAll(graphsNames);
        });
    }

    private void updateExecutionList(List<TableExecution> executionNames){
        Platform.runLater(() -> {
            ObservableList<TableExecution> items = executionTable.getItems();
            items.clear();
            items.addAll(executionNames);
        });
    }

    private void updateErrorMsg(String msg){
        Platform.runLater(()-> {
            setLabelsInActive();
            chosenGraphLbl.setText(msg);
        });
    }

    // Executions current admin published only.
    private void updateSelfExecutionList(List<TableExecution> executionNames){
        Platform.runLater(() -> {
            ObservableList<TableExecution> items = selfExecutionTable.getItems();
            items.clear();
            List<TableExecution> newItems = new ArrayList<>();
            for (TableExecution execution : executionNames){
                if (execution.getCreatedBy().equals(adminAppController.getUsername()))
                    newItems.add(execution);
            }
            items.addAll(newItems);
        });
    }

    /*-------*/

    public void setActive() {
        startListUsersRefresher();
        startListGraphsRefresher();
        startListExecutionsRefresher();
        startListSelfExecutionsRefresher();
        setAutoUpdate(true);
    }

    public void setInActive(){
        setAutoUpdate(false);
        setLabelsInActive();
    }

    /*-------*/

    // Refreshers
    public void startListUsersRefresher() {
        listRefresher = new UserListRefresher(
                autoUpdate,
                this::updateUsersList,
                this::updateErrorMsg);
        timer = new Timer();
        timer.schedule(listRefresher, Constants.DELAY_RATE, Constants.REFRESH_RATE);
    }

    public void startListGraphsRefresher() {
        listRefresher = new GraphsRefresher(
                autoUpdate,
                this::updateGraphsList,
                this::updateErrorMsg);
        timer = new Timer();
        timer.schedule(listRefresher, Constants.DELAY_RATE, Constants.REFRESH_RATE);
    }

    public void startListExecutionsRefresher(){
        listRefresher = new ExecutionRefresher(
                autoUpdate,
                this::updateExecutionList,
                this::updateErrorMsg);
        timer = new Timer();
        timer.schedule(listRefresher, Constants.DELAY_RATE, Constants.REFRESH_RATE);
    }

    public void startListSelfExecutionsRefresher(){
        listRefresher = new SelfExecutionRefresher(
                autoUpdate,
                this::updateSelfExecutionList,
                this::updateErrorMsg);
        timer = new Timer();
        timer.schedule(listRefresher, Constants.DELAY_RATE, Constants.REFRESH_RATE);
    }

    /*-------*/

    public void setAdminAppController(AdminAppController adminAppController) {
        this.adminAppController = adminAppController;
    }

    public void setAutoUpdate(boolean newValue) {
        autoUpdate.set(newValue);
    }

    // Incremental
    public DupExecutionDetails tableExecutionToDupExecution(TableExecution execution, RunType runType){
        DupExecutionDetails curr = new DupExecutionDetails(execution.getExecutionName(), runType);
        curr.setCreatedBy(execution.getCreatedBy());
        return curr;
    }

    public void sendDupExecutionToServer(DupExecutionDetails dupExecution){
        String dupExecutionDetailsGson = GsonConfig.gson.toJson(dupExecution);

        String body = "dupExecutionDetails=" + dupExecutionDetailsGson;

        Request request = new Request.Builder().url(Constants.CREATE_DUP_EXECUTION)
                .post(RequestBody.create(body.getBytes()))
                .build();

        Call call = HttpConfig.HTTP_CLIENT.newCall(request);
        call.enqueue(HttpConfig.SIMPLE_CALLBACK);
    }

    /*-------*/

    @FXML
    void createCompilationBtnAction(ActionEvent event) {
        adminAppController.setCompilationDetailsFillPage();
    }

    @FXML
    void createSimulationBtnAction(ActionEvent event) {
        adminAppController.setSimulationDetailsFillPage();
    }

    @FXML
    void executionControlBtnAction(ActionEvent event) {
        adminAppController.setMidRunPage();
    }

    @FXML
    void incrementalBtnAction(ActionEvent event) {
        DupExecutionDetails dupExecutionDetails = tableExecutionToDupExecution(chosenExecution, RunType.INCREMENTAL);
        sendDupExecutionToServer(dupExecutionDetails);
    }

    @FXML
    void fromScratchBtnAction(ActionEvent event) {
        DupExecutionDetails dupExecutionDetails = tableExecutionToDupExecution(chosenExecution, RunType.FROM_SCRATCH);
        sendDupExecutionToServer(dupExecutionDetails);
    }

    /*-------*/

    // Mouse press
    @FXML void graphsMousePressed(MouseEvent event) {
        // Double-click on graphs table
        if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
            Node node = ((Node) event.getTarget()).getParent();
            if (!(node instanceof TableView)) {
                if (node != null) {
                    TableRow row;
                    if (node instanceof TableRow) {
                        row = (TableRow) node;
                    } else {
                        // clicking on text part
                        row = (TableRow) node.getParent();
                    }
                    if (row.getItem() != null) {
                        chosenGraph = ((TableGraph) row.getItem()).getGraphName();
                        setLabelsInActive();
                        setLabelsActive((TableGraph) row.getItem());
                    }
                }

            }
        }
    }

    @FXML void selfExecutionsMousePressed(MouseEvent event) {
        if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
            Node node = ((Node) event.getTarget()).getParent();
            TableRow row;
            if (!(node instanceof TableView)) {
                if (node instanceof TableRow) {
                    row = (TableRow) node;
                } else {
                    // clicking on text part
                    row = (TableRow) node.getParent();
                }
                if (row.getItem() != null) {
                    chosenExecution = (TableExecution) row.getItem();
                    adminAppController.setChosenExecution(chosenExecution);
                    setLabelsInActive();
                    isExecutionControlPanelEnabled.set(true);
                    Platform.runLater(()->chosenGraphLbl.setText(CHOSEN_EXECUTION_LBL + chosenExecution.getExecutionName()));
                    isRunTypeEnabled.set(chosenExecution.getStatus().equals(ExecutionStatus.FINISHED.toString()));
                }
            }


        }
    }

    // Clicking background
    @FXML void gridPaneMousePressed(MouseEvent event) {
        if (event.isPrimaryButtonDown() && event.getClickCount() == 1) {
            setLabelsInActive();
        }
    }

    // Clicking execution table
    @FXML void executionMousePressed(MouseEvent event) {
        if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
            Node node = ((Node) event.getTarget()).getParent();
            TableRow row;
            if (!(node instanceof TableView)) {
                if (node instanceof TableRow) {
                    row = (TableRow) node;
                } else {
                    // Clicking on text part
                    row = (TableRow) node.getParent();
                }

                if (row.getItem() != null) {

                    TableExecution newlyChosenExecution = (TableExecution) row.getItem();
                    chosenExecution = (TableExecution) row.getItem();
                    adminAppController.setChosenExecution(chosenExecution);
                    setLabelsInActive();
                    Platform.runLater(() -> chosenGraphLbl.setText(CHOSEN_EXECUTION_LBL + chosenExecution.getExecutionName()));
                    // After a user clicks a graph, he can choose running incremental or from scratch if the chosen graph has already run before.
                    isRunTypeEnabled.set(chosenExecution.getStatus().equals(ExecutionStatus.FINISHED.toString()));
                }
            }

        }
    }
}




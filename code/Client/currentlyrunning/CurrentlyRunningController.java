package currentlyrunning;

import Util.Constants;
import Util.http.HttpClientUtil;
import dto.dtoServer.execution.TableExecution;
import dto.dtoServer.worker.server2worker.RunTimeExecutionDetails;
import dto.dtoServer.worker.server2worker.RunTimeExecutionList;
import dto.dtoServer.worker.workerengine2worker.RunTimeTargetsDetails;
import dto.dtoServer.worker.workerengine2worker.TargetPerformByMe;
import engine.ClientEngine;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import main.ClientAppController;
import okhttp3.HttpUrl;
import okhttp3.Response;
import java.util.*;

public class CurrentlyRunningController {

    @FXML private TableView<TargetPerformByMe> targetsTable;
    @FXML private TableColumn<TargetPerformByMe, String> executionNameColumn;
    @FXML private TableColumn<TargetPerformByMe, String> taskTypeColumn;
    @FXML private TableColumn<TargetPerformByMe, String> targetNameColumn;
    @FXML private TableColumn<TargetPerformByMe, String> stateColumn;
    @FXML private TableColumn<TargetPerformByMe, Integer> pricePaidColumn;

    @FXML private TableView<RunTimeExecutionDetails> executionTable;
    @FXML private TableColumn<RunTimeExecutionDetails, String> nameColumn;
    @FXML private TableColumn<RunTimeExecutionDetails, Integer> totalWorkersColumn;
    @FXML private TableColumn<RunTimeExecutionDetails, Integer> totalCreditsColumn;
    @FXML private TableColumn<RunTimeExecutionDetails, String> activityStatusColumn;
    @FXML private TableColumn<RunTimeExecutionDetails, Double> progressColumn;
    @FXML private TableColumn<RunTimeExecutionDetails, Integer> targetsCompletedColumn;

    @FXML private Button pauseButton;
    @FXML private Button resumeButton;
    @FXML private Button unsubscribeButton;
    @FXML private Label chosenLbl;
    @FXML private TextArea msgTA;
    @FXML private Label numberOfThreadsLbl;
    @FXML private Label numberOfBusyThreadsLbl;

    private Timer timer;
    private TimerTask listRefresher;
    private final BooleanProperty autoUpdate;
    private ClientAppController clientAppController;
    private ClientEngine clientEngine;
    private final SimpleStringProperty msgProperty;
    private RunTimeExecutionDetails chosenExecutionPage2;
    private boolean wasClickedOnce;
    private Map<String, RunTimeExecutionDetails> nameToRunTimeExecution;

    public CurrentlyRunningController() {
        autoUpdate = new SimpleBooleanProperty(false);
        msgProperty = new SimpleStringProperty("");
        wasClickedOnce = false;
        nameToRunTimeExecution = new HashMap<>();
    }

    @FXML
    public void initialize(){
        msgTA.textProperty().bind(msgProperty);

        executionNameColumn.setCellValueFactory(new PropertyValueFactory<TargetPerformByMe, String>("executionName"));
        taskTypeColumn.setCellValueFactory(new PropertyValueFactory<TargetPerformByMe, String>("taskName"));
        targetNameColumn.setCellValueFactory(new PropertyValueFactory<TargetPerformByMe, String>("targetName"));
        stateColumn.setCellValueFactory(new PropertyValueFactory<TargetPerformByMe, String>("targetState"));
        pricePaidColumn.setCellValueFactory(new PropertyValueFactory<TargetPerformByMe, Integer>("credit"));

        nameColumn.setCellValueFactory(new PropertyValueFactory<RunTimeExecutionDetails, String>("name"));
        totalWorkersColumn.setCellValueFactory(new PropertyValueFactory<RunTimeExecutionDetails, Integer>("workerAmount"));
        totalCreditsColumn.setCellValueFactory(new PropertyValueFactory<RunTimeExecutionDetails, Integer>("totalPriceFromThisExecution"));
        activityStatusColumn.setCellValueFactory(new PropertyValueFactory<RunTimeExecutionDetails, String>("isPaused"));
        progressColumn.setCellValueFactory(new PropertyValueFactory<RunTimeExecutionDetails, Double>("progress"));
        targetsCompletedColumn.setCellValueFactory(new PropertyValueFactory<RunTimeExecutionDetails, Integer>("numOfCompletedTargets"));

    }

    public void setClientAppController(ClientAppController clientAppController) {
        this.clientAppController = clientAppController;
    }

    public void setClientEngine(ClientEngine clientEngine) {
        this.clientEngine = clientEngine;
    }

    @FXML
    void executionMousePressed(MouseEvent event) {
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
                    if (row.getItem() != null) {
                        RunTimeExecutionDetails newlyChosenExecutionPage2 = (RunTimeExecutionDetails) row.getItem();
                        if (nameToRunTimeExecution.containsKey(newlyChosenExecutionPage2.getName())) {
                            chosenExecutionPage2 = nameToRunTimeExecution.get(newlyChosenExecutionPage2.getName());
                            clientAppController.setChosenExecutionPage2(chosenExecutionPage2);
                        } else {
                            nameToRunTimeExecution.put(newlyChosenExecutionPage2.getName(), newlyChosenExecutionPage2);
                            clientAppController.setChosenExecutionPage2(newlyChosenExecutionPage2);
                            chosenExecutionPage2 = newlyChosenExecutionPage2;
                        }
                        Platform.runLater(() -> chosenLbl.setText(chosenExecutionPage2.getName()));
                        enableButtons();
                    }
                }

            }
        }
    }
    public void disableButtons(){
        pauseButton.setDisable(true);
        resumeButton.setDisable(true);
        unsubscribeButton.setDisable(true);
        chosenLbl.setText("");
    }

    public void enableButtons(){
        if (chosenExecutionPage2.getIsPaused()) {
            pauseButton.setDisable(true);
            resumeButton.setDisable(false);
        }
        else{
            pauseButton.setDisable(false);
            resumeButton.setDisable(true);
        }
        unsubscribeButton.setDisable(false);
    }

    @FXML
    void gridPaneMousePressed(MouseEvent event) {
        if (event.isPrimaryButtonDown() && event.getClickCount() == 1) {
            Platform.runLater(this::disableButtons);
        }
    }

    @FXML void pauseButtonAction(ActionEvent event) {

        String finalUrl = HttpUrl
                .parse(Constants.PAUSE_REGISTRATION)
                .newBuilder()
                .addQueryParameter("executionName", chosenExecutionPage2.getName())
                .build()
                .toString();

        Response response = HttpClientUtil.runSync(finalUrl);
        try {
            String responseBody = response.body().string();
            if (response.code() != 200) {

                Platform.runLater(() ->
                        msgProperty.set("Something went wrong: " + responseBody)
                );
            } else {
                Platform.runLater(() -> {
                    try {
                        chosenExecutionPage2.setIsPaused(true);
                        pauseButton.setDisable(true);
                        resumeButton.setDisable(false);
                    } catch (Exception e) {
                        msgProperty.set("Something went wrong: " + e.getMessage());
                    }

                });
            }
        } catch (Exception e) {
            Platform.runLater(() ->
            msgProperty.set(e.getMessage()));
        }
    }

    @FXML void resumeButtonAction(ActionEvent event){
        String finalUrl = HttpUrl
                .parse(Constants.RESUME_REGISTRATION)
                .newBuilder()
                .addQueryParameter("executionName",chosenExecutionPage2.getName())
                .build()
                .toString();

        Response response = HttpClientUtil.runSync(finalUrl);
        try {
            String responseBody = response.body().string();
            if (response.code() != 200) {

                Platform.runLater(() ->
                        msgProperty.set("Something went wrong: " + responseBody)
                );
            } else {
                Platform.runLater(() -> {
                    try {
                        chosenExecutionPage2.setIsPaused(false);
                        pauseButton.setDisable(false);
                        resumeButton.setDisable(true);
                    } catch (Exception e) {
                        msgProperty.set("Something went wrong: " + e.getMessage());
                    }

                });
            }
        } catch (Exception e) {
            Platform.runLater(() ->
                    msgProperty.set(e.getMessage()));
        }
    }

    private void setUnsubscribeButton(){
        unsubscribeButton.setText("ARE YOU SURE?");
        unsubscribeButton.setTextFill(Color.RED);
    }

    private void resetUnsubscribeButton(){
        unsubscribeButton.setText("Unsubscribe");
        unsubscribeButton.setTextFill(Color.BLACK);
        wasClickedOnce = false;
    }

    @FXML
    void unsubscribeMousePressed(MouseEvent event) {
        if (event.isPrimaryButtonDown()) {
            Platform.runLater(() -> {
                setUnsubscribeButton();
                wasClickedOnce = true;
            });
        }
        if (event.isPrimaryButtonDown() && wasClickedOnce) {
            Platform.runLater(() -> {
                resetUnsubscribeButton();
                unsubscribeButton.setDisable(true);
            });
            clientEngine.unsubscribeToExecution(chosenExecutionPage2.getName());
            Platform.runLater(() -> {
                pauseButton.setDisable(true);
                resumeButton.setDisable(true);
                unsubscribeButton.setDisable(true);
            });
        }
    }



    @FXML
    void targetsTableMousePressed(MouseEvent event) {
        gridPaneMousePressed(event);
    }

    public void updateErrorMsg(String msg){
        msgProperty.set(msg);
    }
    public void setActive(){
       autoUpdate.set(true);
       initButtons();
       startListTargetsRefresher();
       startListExecutionRefresher();
       Platform.runLater(()->  numberOfThreadsLbl.setText(String.valueOf(clientEngine.getNumOfThread())));
    }

    public void setInActive(){
        autoUpdate.set(false);
    }

    public void initButtons(){
        pauseButton.setDisable(true);
        resumeButton.setDisable(true);
        resetUnsubscribeButton();
        unsubscribeButton.setDisable(true);
    }

    public void startListTargetsRefresher(){
        listRefresher = new TargetsRefresher(
                autoUpdate,
                this::updateTargetList,
                this::updateErrorMsg,
                clientEngine);
        timer = new Timer();
        timer.schedule(listRefresher, Constants.DELAY_RATE, Constants.REFRESH_RATE);
    }

    private void updateTargetList(RunTimeTargetsDetails executionNames){
        Platform.runLater(() -> {
            ObservableList<TargetPerformByMe> items = targetsTable.getItems();
            items.clear();
            Set<TargetPerformByMe> targetsSet = executionNames.getTargetsPerformByMe();
            String finalMsg = "";
            for (TargetPerformByMe target : targetsSet ) {
                items.add(target);
                String targetName = target.getTargetName();
                String targetLogs = target.getLogs();
                finalMsg = finalMsg.concat(targetName + " " + targetLogs + "\n");
            }
            setMessage(finalMsg);

            numberOfBusyThreadsLbl.setText(String.valueOf(clientEngine.getNumOfBusyThreads()));

        });
    }

    public void setMessage(String message) {
        if (!msgProperty.get().equals(message))
            Platform.runLater(() ->
                    msgProperty.set(message));
    }


    private void updateExecutionList(RunTimeExecutionList runTimeExecutionDetails) {

        ObservableList<RunTimeExecutionDetails> items = executionTable.getItems();
        Platform.runLater(() -> {
            if (items.isEmpty()) chosenExecutionPage2 = null;
            if (chosenExecutionPage2 != null)
                enableButtons();
            else
                disableButtons();


            items.clear();
            RunTimeExecutionDetails[] runTimeExecutionDetailsArr = runTimeExecutionDetails.getRunTimeExecutionsDetails();
            items.addAll(Arrays.asList(runTimeExecutionDetailsArr));
        });
    }

    public void startListExecutionRefresher(){
        listRefresher = new ExecutionRefresher(
                autoUpdate,
                this::updateExecutionList,
                clientEngine);
        timer = new Timer();
        timer.schedule(listRefresher, Constants.DELAY_RATE, Constants.REFRESH_RATE);
    }
}

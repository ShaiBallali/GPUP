package midRunTable;

import Util.Constants;
import Util.http.HttpClientUtil;
import com.google.gson.stream.JsonReader;
import dto.dtoServer.execution.TableExecution;
import dto.dtoServer.runtime.RunTimeTargetDetails;
import dto.dtoServer.runtime.RunTimeTaskDetails;
import dto.enums.ExecutionStatus;
import dto.enums.Position;
import dto.enums.RunResult;
import dto.dtoServer.result.TaskResult;
import graphs.TableGraph;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import main.AdminAppController;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

public class MidRunTableController { // This table is the "control panel" mid-run

    @FXML private Label allTargetsLbl;
    @FXML private Label skippedLbl;
    @FXML private Label finishedLbl;
    @FXML private Label successLbl;
    @FXML private Label successWithWarningsLbl;
    @FXML private Label failedLbl;
    @FXML private Label independentLbl;
    @FXML private Label rootLbl;
    @FXML private Label leafLbl;
    @FXML private Label middleLbl;
    @FXML private Label totalWorkersLbl;
    @FXML private Label runningTargetsLbl;
    @FXML private Label waitingTargetsLbl;
    @FXML private Label graphNameLbl;
    @FXML private Label executionNameLbl;

    @FXML private Button pauseBtn;
    @FXML private Button resumeBtn;
    @FXML private Button stopBtn;
    @FXML private Button playBtn;
    @FXML private TextArea msgTA;
    @FXML private Label taskProgressLbl;
    @FXML private ProgressBar targetProgressBar;

    @FXML private TableView<RunTimeTargetDetails> waitingTable;
    @FXML private TableColumn<RunTimeTargetDetails, String> waitingNameColumn;
    @FXML private TableView<RunTimeTargetDetails> skippedTable;
    @FXML private TableColumn<RunTimeTargetDetails, String> skippedNameColumn;
    @FXML private TableView<RunTimeTargetDetails> inProcessTable;
    @FXML private TableColumn<RunTimeTargetDetails, String> inProcessNameColumn;
    @FXML private TableView<RunTimeTargetDetails> frozenTable;
    @FXML private TableColumn<RunTimeTargetDetails, String> frozenNameColumn;
    @FXML private TableView<RunTimeTargetDetails> finishedTable;
    @FXML private TableColumn<RunTimeTargetDetails, String> finishedNameColumn;
    @FXML private TableColumn<RunTimeTargetDetails, String> finishedStatusColumn;
    @FXML private TableColumn<RunTimeTargetDetails, Long> waitingWaitingForColumn;
    @FXML private TableColumn<RunTimeTargetDetails, String> skippedDependenciesFailedColumn;
    @FXML private TableColumn<RunTimeTargetDetails, Long> inProcessProcessingTimeColumn;
    @FXML private TableColumn<RunTimeTargetDetails, String> frozenBlockingDependenciesColumn;
    @FXML private TableColumn<RunTimeTargetDetails, Long> finishedTotalProcessingTimeColumn;
    @FXML private TableColumn<RunTimeTargetDetails, String> waitingStateColumn;
    @FXML private TableColumn<RunTimeTargetDetails, String> skippedStateColumn;
    @FXML private TableColumn<RunTimeTargetDetails, String> frozenStateColumn;
    @FXML private TableColumn<RunTimeTargetDetails, String> finishedStateColumn;
    @FXML private TableColumn<RunTimeTargetDetails, String> inProcessStateColumn;

    private final SimpleIntegerProperty totalTargets;
    private final SimpleIntegerProperty totalSkipped;
    private final SimpleIntegerProperty totalSuccess;
    private final SimpleIntegerProperty totalSuccessWithWarnings;
    private final SimpleIntegerProperty totalFinished;
    private final SimpleIntegerProperty totalFailed;
    private final SimpleIntegerProperty totalIndependents;
    private final SimpleIntegerProperty totalLeaves;
    private final SimpleIntegerProperty totalRoots;
    private final SimpleIntegerProperty totalMiddles;
    private final SimpleIntegerProperty totalWorkers;
    private final SimpleIntegerProperty totalBeingRun;
    private final SimpleIntegerProperty totalWaiting;
    private final SimpleDoubleProperty  barUpdater;
    private final SimpleBooleanProperty isExecutionFinished;
    private final SimpleStringProperty msgProperty;

    /*-----*/

    private final BooleanProperty autoUpdate;
    private Timer timer;
    private TimerTask listRefresher;
    private AdminAppController adminAppController;
    private int leafCount, independentCount, middleCount, rootCount;


    public MidRunTableController(){
        /*------------*/
        totalTargets = new SimpleIntegerProperty(0);
        totalSkipped = new SimpleIntegerProperty(0);
        totalFinished = new SimpleIntegerProperty(0);
        totalSuccess = new SimpleIntegerProperty(0);
        totalSuccessWithWarnings = new SimpleIntegerProperty(0);
        totalFailed = new SimpleIntegerProperty(0);
        totalWorkers = new SimpleIntegerProperty(0);
        totalIndependents = new SimpleIntegerProperty(0);
        totalLeaves = new SimpleIntegerProperty(0);
        totalRoots = new SimpleIntegerProperty(0);
        totalMiddles = new SimpleIntegerProperty(0);
        totalBeingRun = new SimpleIntegerProperty(0);
        totalWaiting = new SimpleIntegerProperty(0);
        isExecutionFinished = new SimpleBooleanProperty(false);
        msgProperty = new SimpleStringProperty("");

        /*---------*/
        autoUpdate = new SimpleBooleanProperty();
        barUpdater = new SimpleDoubleProperty();


    }

    @FXML private void initialize(){
        /*------------*/
        allTargetsLbl.textProperty().bind(Bindings.format("%,d", totalTargets));
        skippedLbl.textProperty().bind(Bindings.format("%,d", totalSkipped));
        finishedLbl.textProperty().bind(Bindings.format("%,d", totalFinished));
        successLbl.textProperty().bind(Bindings.format("%,d", totalSuccess));
        successWithWarningsLbl.textProperty().bind(Bindings.format("%,d", totalSuccessWithWarnings));
        failedLbl.textProperty().bind(Bindings.format("%,d", totalFailed));
        waitingTargetsLbl.textProperty().bind(Bindings.format("%,d", totalWaiting));
        runningTargetsLbl.textProperty().bind(Bindings.format("%,d", totalBeingRun));
        totalWorkersLbl.textProperty().bind(Bindings.format("%,d", totalWorkers));
        independentLbl.textProperty().bind(Bindings.format("%,d", totalIndependents));
        rootLbl.textProperty().bind(Bindings.format("%,d", totalRoots));
        middleLbl.textProperty().bind(Bindings.format("%,d", totalMiddles));
        leafLbl.textProperty().bind(Bindings.format("%,d", totalLeaves));
        msgTA.textProperty().bind(msgProperty);

        isExecutionFinished.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(
                    ObservableValue<? extends Boolean> observable,
                    Boolean oldValue, Boolean newValue
            ) {
                if (newValue)
                    setInActive();
            }

            });

        targetProgressBar.progressProperty().bind(barUpdater);
        taskProgressLbl.textProperty().bind(
                Bindings.concat(
                        Bindings.format(
                                "%.0f",
                                Bindings.multiply(
                                        barUpdater, 100)),
                        " %"));
        taskProgressLbl.textProperty().addListener((new ChangeListener<String>() { // // Success with warning text field listens to corresponding slider
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (newValue.equals("100 %")) { // if task is finished
                    stopBtn.setDisable(true);
                    resumeBtn.setDisable(true);
                    playBtn.setDisable(true);
                    pauseBtn.setDisable(true);
                }
            }
        }));

        /*------------*/
        waitingNameColumn.setCellValueFactory(new PropertyValueFactory<RunTimeTargetDetails, String>("name"));
        skippedNameColumn.setCellValueFactory(new PropertyValueFactory<RunTimeTargetDetails, String>("name"));
        inProcessNameColumn.setCellValueFactory(new PropertyValueFactory<RunTimeTargetDetails, String>("name"));
        frozenNameColumn.setCellValueFactory(new PropertyValueFactory<RunTimeTargetDetails, String>("name"));
        finishedNameColumn.setCellValueFactory(new PropertyValueFactory<RunTimeTargetDetails, String>("name"));
        finishedStatusColumn.setCellValueFactory(new PropertyValueFactory<RunTimeTargetDetails, String>("runResult"));
        waitingWaitingForColumn.setCellValueFactory(new PropertyValueFactory<RunTimeTargetDetails, Long>("totalWaitingTime"));
        skippedDependenciesFailedColumn.setCellValueFactory(new PropertyValueFactory<RunTimeTargetDetails, String>("DependentsThatFailed"));
        inProcessProcessingTimeColumn.setCellValueFactory(new PropertyValueFactory<RunTimeTargetDetails, Long>("totalProcessingTime"));
        frozenBlockingDependenciesColumn.setCellValueFactory(new PropertyValueFactory<RunTimeTargetDetails, String>("DependentsThatBlock"));
        finishedTotalProcessingTimeColumn.setCellValueFactory(new PropertyValueFactory<RunTimeTargetDetails, Long>("totalProcessTime"));
        waitingStateColumn.setCellValueFactory(new PropertyValueFactory<RunTimeTargetDetails, String>("position"));
        skippedStateColumn.setCellValueFactory(new PropertyValueFactory<RunTimeTargetDetails, String>("position"));
        frozenStateColumn.setCellValueFactory(new PropertyValueFactory<RunTimeTargetDetails, String>("position"));
        finishedStateColumn.setCellValueFactory(new PropertyValueFactory<RunTimeTargetDetails, String>("position"));
        inProcessStateColumn.setCellValueFactory(new PropertyValueFactory<RunTimeTargetDetails, String>("position"));
    }

    public Set<RunTimeTargetDetails> convertTaskToTargets(RunTimeTaskDetails runTimeTaskDetails){
        Set<RunTimeTargetDetails> finalList = new HashSet<>();
        Collections.addAll(finalList, runTimeTaskDetails.getRuntimeTargetDetails());
        return finalList;
    }

    private void updateSummary(TaskResult taskResult){
        Map<RunResult, Integer> runResultCounter = taskResult.getRunResultCounter();
        setTotalSuccess(runResultCounter.get(RunResult.SUCCESS));
        setTotalFailed(runResultCounter.get(RunResult.FAILURE));
        setTotalSuccessWithWarnings(runResultCounter.get(RunResult.SUCCESS_WITH_WARNING));
        setTotalSkipped(runResultCounter.get(RunResult.SKIPPED));
        setTotalFinished(taskResult.getFinished());
        setTotalTargets(taskResult.getAllTargets());
    }

    public void updateOnStart(){
        leafCount = 0;
        independentCount = 0;
        middleCount = 0;
        rootCount = 0;

       RunTimeTaskDetails runTimeTaskDetails = getTaskDetailsFromServer();
       Set<RunTimeTargetDetails> runTimeTargetDetailsSet = convertTaskToTargets(runTimeTaskDetails);
       for (RunTimeTargetDetails runTimeTargetDetails : runTimeTargetDetailsSet){
           switch(runTimeTargetDetails.getPosition()){
               case "LEAF":
                   leafCount++;
                   break;
               case "INDEPENDENT":
                   independentCount++;
                   break;
               case "MIDDLE":
                   middleCount++;
                   break;
               case "ROOT":
                   rootCount++;
                   break;
           }
       }
       Platform.runLater(()-> {
           setTotalRoots(rootCount);
           setTotalMiddles(middleCount);
           setTotalIndependents(independentCount);
           setTotalLeaves(leafCount);
           graphNameLbl.setText(adminAppController.getChosenExecution().getGraphName());
           executionNameLbl.setText(adminAppController.getChosenExecution().getExecutionName());
       });

    }

    private TaskResult getTaskResultFromServer() {
        try {
            String finalUrl = HttpUrl
                    .parse(Constants.RUN_RESULT)
                    .newBuilder()
                    .addQueryParameter("executionName", adminAppController.getChosenExecution().getExecutionName())
                    .build()
                    .toString();

            Response response = HttpClientUtil.runSync(finalUrl);
            String responseBody = response.body().string();
            if (response.code() != 200) {
                setMessage("Something went wrong: " + responseBody);

            } else {
                 try {
                        JsonReader reader = new JsonReader(new StringReader(responseBody));
                        reader.setLenient(true);
                        return Constants.GSON_INSTANCE.fromJson(reader, TaskResult.class);
                    } catch (Exception e) {
                        setMessage("Something went wrong: " + e.getMessage());
                    }

            }

        } catch (Exception e) {
            setMessage(e.getMessage());
        }
        return null;
    }

    private RunTimeTaskDetails getTaskDetailsFromServer() {
        try {
            String finalUrl = HttpUrl
                    .parse(Constants.RUN_TIME)
                    .newBuilder()
                    .addQueryParameter("executionName", adminAppController.getChosenExecution().getExecutionName())
                    .build()
                    .toString();

            Response response = HttpClientUtil.runSync(finalUrl);
            String responseBody = response.body().string();
            if (response.code() != 200) {
                setMessage("Something went wrong: " + responseBody);

            } else {
                try {
                    JsonReader reader = new JsonReader(new StringReader(responseBody));
                    reader.setLenient(true);
                    return Constants.GSON_INSTANCE.fromJson(reader, RunTimeTaskDetails.class);
                } catch (Exception e) {
                    setMessage("Something went wrong: " + e.getMessage());
                }

            }

        } catch (Exception e) {
            setMessage(e.getMessage());
        }
        return null;
    }

    private void updateStatusTables(RunTimeTaskDetails taskDetails){

        Set<RunTimeTargetDetails> newTargets = convertTaskToTargets(taskDetails);
        Platform.runLater(() -> {
            setWaitingTargets(taskDetails.getWaitingTargets()); // waiting targets
            setTotalBeingRun(taskDetails.getCurrRunningTargets()); // running targets
            setMessage(taskDetails.getMessage()); // msg
            double completedDividedByTotalTargets = (double) ((double)taskDetails.getSumOfCompletedTargets() / (double)taskDetails.getSumOfTargets());
            barUpdater.set(completedDividedByTotalTargets); // progress bar
            totalWorkers.set(adminAppController.getChosenExecution().getTotalWorkers());
            /*-------*/
            ObservableList<RunTimeTargetDetails> waitingItems = waitingTable.getItems();
            ObservableList<RunTimeTargetDetails> inProcessItems = inProcessTable.getItems();
            ObservableList<RunTimeTargetDetails> skippedItems = skippedTable.getItems();
            ObservableList<RunTimeTargetDetails> frozenItems = frozenTable.getItems();
            ObservableList<RunTimeTargetDetails> finishedItems = finishedTable.getItems();
            waitingItems.clear();
            inProcessItems.clear();
            frozenItems.clear();
            skippedItems.clear();
            finishedItems.clear();
            for(RunTimeTargetDetails currTarget: newTargets){
                switch(currTarget.getState()){
                    case "WAITING":
                        waitingItems.add(currTarget);
                        break;
                    case "FROZEN":
                        frozenItems.add(currTarget);
                        break;
                    case "SKIPPED":
                        skippedItems.add(currTarget);
                        break;
                    case "FINISHED":
                        finishedItems.add(currTarget);
                        break;
                    case "IN_PROCESS":
                        inProcessItems.add(currTarget);
                        break;
                }
            }
        });

        if (taskDetails.getExecutionStatus().equals(ExecutionStatus.FINISHED)){
            isExecutionFinished.set(true);
            TaskResult taskResult = getTaskResultFromServer();
            if (taskResult!=null)
                Platform.runLater(() ->
                        updateSummary(taskResult));
        }
    }

    public void startMidRunTableRefresher(){
        listRefresher = new MidRunTableRefresher(
                autoUpdate,
                this::updateStatusTables, adminAppController);
        timer = new Timer();
        timer.schedule(listRefresher, Constants.DELAY_RATE, Constants.REFRESH_RATE);
    }

    public void setButtons(){
        TableExecution chosenExecution = adminAppController.getChosenExecution();
        Platform.runLater(()-> {
            if (chosenExecution.getStatus().equals(ExecutionStatus.FINISHED.toString())) {
                resumeBtn.setDisable(true);
                playBtn.setDisable(true);
                pauseBtn.setDisable(true);
                stopBtn.setDisable(true);
            } else {
                if (chosenExecution.getIsPlayed()) {
                    if (chosenExecution.getIsPaused()) {
                        resumeBtn.setDisable(false);
                        pauseBtn.setDisable(true);
                    } else {
                        resumeBtn.setDisable(true);
                        pauseBtn.setDisable(false);
                    }
                    stopBtn.setDisable(false);
                    playBtn.setDisable(true);
                } else {
                    playBtn.setDisable(chosenExecution.getIsStopped());
                    resumeBtn.setDisable(true);
                    pauseBtn.setDisable(true);
                    stopBtn.setDisable(true);
                }
            }
        });

    }

    public void reActivateButtons(){
        Platform.runLater(()-> {
            pauseBtn.setDisable(false);
            stopBtn.setDisable(false);
            resumeBtn.setDisable(true);
        });
    }

    public void reActivate(){
        reActivateButtons();
        startMidRunTableRefresher();
        setAutoUpdate(true);
    }

    public void setActive() {
        setButtons();
        updateOnStart();
        startMidRunTableRefresher();
        setAutoUpdate(true);
    }

    public void setInActive(){
        setAutoUpdate(false);
    }

    public void setAutoUpdate(boolean newValue) {
        autoUpdate.set(newValue);
    }

    public void setAdminAppController(AdminAppController adminAppController) {
        this.adminAppController = adminAppController;
    }



    @FXML void pauseBtnAction(ActionEvent event) {
        Platform.runLater(()->pauseBtn.setDisable(true));
        TableExecution chosenExecution = adminAppController.getChosenExecution();
        try {
            String finalUrl = HttpUrl
                    .parse(Constants.SET_STATUS)
                    .newBuilder()
                    .addQueryParameter("executionName", chosenExecution.getExecutionName())
                    .addQueryParameter("status", ExecutionStatus.PAUSED.toString())
                    .build()
                    .toString();

            Response response = HttpClientUtil.runSync(finalUrl);
            String responseBody = response.body().string();
            if (response.code() != 200) {

                       setMessage("Something went wrong: " + responseBody);
            }
            else chosenExecution.setIsPaused(true);

        } catch (Exception e) {

                setMessage(e.getMessage());
        }
        resumeBtn.disableProperty().set(false);
    }

    @FXML void resumeBtnAction(ActionEvent event) {
        Platform.runLater(()-> pauseBtn.setDisable(false));
        reActivate();
        TableExecution chosenExecution = adminAppController.getChosenExecution();
        try {
            String finalUrl = HttpUrl
                    .parse(Constants.SET_STATUS)
                    .newBuilder()
                    .addQueryParameter("executionName", chosenExecution.getExecutionName())
                    .addQueryParameter("status", ExecutionStatus.RESUMED.toString())
                    .build()
                    .toString();

            Response response = HttpClientUtil.runSync(finalUrl);
            String responseBody = response.body().string();
            if (response.code() != 200) {
                setMessage("Something went wrong: " + responseBody);
                chosenExecution.setIsPaused(true);
            }




        } catch (Exception e) {
            setMessage(e.getMessage());
        }
        Platform.runLater(()-> resumeBtn.setDisable(true));
    }

    @FXML void playBtnAction(ActionEvent event) {
        Platform.runLater(()-> playBtn.setDisable(true));
        TableExecution chosenExecution = adminAppController.getChosenExecution();
        try {
            String finalUrl = HttpUrl
                    .parse(Constants.SET_STATUS)
                    .newBuilder()
                    .addQueryParameter("executionName", chosenExecution.getExecutionName())
                    .addQueryParameter("status", ExecutionStatus.PLAYING.toString())
                    .build()
                    .toString();

            Response response = HttpClientUtil.runSync(finalUrl);
            String responseBody = response.body().string();
            if (response.code() != 200)
                setMessage("Something went wrong: " + responseBody);
            else
                chosenExecution.setIsPlayed(true);

        } catch (Exception e) {
            setMessage(e.getMessage());

        }
        Platform.runLater(() -> {
            resumeBtn.setDisable(true);
            pauseBtn.setDisable(false);
            stopBtn.setDisable(false);
        });
    }

    @FXML void stopBtnAction(ActionEvent event){
        try {
            TableExecution chosenExecution = adminAppController.getChosenExecution();
            setInActive();
            String finalUrl = HttpUrl
                    .parse(Constants.SET_STATUS)
                    .newBuilder()
                    .addQueryParameter("executionName", chosenExecution.getExecutionName())
                    .addQueryParameter("status", ExecutionStatus.STOPPED.toString())
                    .build()
                    .toString();

            Response response = HttpClientUtil.runSync(finalUrl);
            String responseBody = response.body().string();
            if (response.code() != 200)
                setMessage("Something went wrong: " + responseBody);
            else {
                chosenExecution.setIsStopped(true);
                chosenExecution.setIsPlayed(false);
                chosenExecution.setIsPaused(false);
            }


        } catch (Exception e) {

                    setMessage(e.getMessage());
        }
        Platform.runLater(() -> {
            pauseBtn.setDisable(true);
            resumeBtn.setDisable(true);
            stopBtn.setDisable(true);
        });
    }

    public void setTotalTargets(int totalTargets){
        this.totalTargets.set(totalTargets);
    } // Summary methods

    public void setTotalSuccess(int totalSuccess){
        this.totalSuccess.set(totalSuccess);
    }

    public void setTotalSuccessWithWarnings(int totalSuccessWithWarnings){
        this.totalSuccessWithWarnings.set(totalSuccessWithWarnings);
    }

    public void setTotalFailed(int totalFailed){
        this.totalFailed.set(totalFailed);
    }

    public void setTotalSkipped(int totalSkipped){
        this.totalSkipped.set(totalSkipped);
    }

    public void setTotalFinished(int totalFinished){
        this.totalFinished.set(totalFinished);
    }

    public void setTotalIndependents(int totalIndependents){ this.totalIndependents.set(totalIndependents); }

    public void setTotalRoots(int totalRoots){ this.totalRoots.set(totalRoots); }

    public void setTotalMiddles(int totalMiddles){ this.totalMiddles.set(totalMiddles); }

    public void setTotalLeaves(int totalLeaves){ this.totalLeaves.set(totalLeaves); }

    public void setWaitingTargets(int totalWaiting){ this.totalWaiting.set(totalWaiting); }

    public void setTotalBeingRun(int totalBeingRun){ this.totalBeingRun.set(totalBeingRun); }


    public void clear() {
        Platform.runLater(()-> {

            // Reset summary

            setTotalFinished(0);
            setTotalTargets(0);
            setTotalSuccess(0);
            setTotalFailed(0);
            setTotalSuccessWithWarnings(0);
            setTotalSkipped(0);
        });
    }

    public void setMessage(String message) {
        if (!msgProperty.get().equals(message))
            Platform.runLater(() ->
                    msgProperty.set(message));
    }
}




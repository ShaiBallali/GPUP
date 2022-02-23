package dashboard;

import Util.Constants;
import dto.dtoServer.execution.TableClientExecution;
import dto.enums.ExecutionStatus;
import engine.ClientEngine;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import main.ClientAppController;
import users.TableUser;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DashboardController {

    private ClientAppController clientAppController;
    private ClientEngine clientEngine;
    private Timer timer;
    private TimerTask listRefresher;
    private final BooleanProperty autoUpdate;
    private final IntegerProperty totalUsers;
    private TableClientExecution chosenExecution;

    @FXML private TableView<TableUser> usersTable;
    @FXML private TableColumn<TableUser, String> usernameColumn;
    @FXML private TableColumn<TableUser, String> roleColumn;

    @FXML private TableView<TableClientExecution> executionTable;
    @FXML private TableColumn<TableClientExecution, String> nameColumn;
    @FXML private TableColumn<TableClientExecution, String> submittingUserColumn;
    @FXML private TableColumn<TableClientExecution, String> taskTypeColumn;
    @FXML private TableColumn<TableClientExecution, Integer> targetsCountColumn;
    @FXML private TableColumn<TableClientExecution, Integer> independentColumn;
    @FXML private TableColumn<TableClientExecution, Integer> leafColumn;
    @FXML private TableColumn<TableClientExecution, Integer> rootColumn;
    @FXML private TableColumn<TableClientExecution, Integer> middleColumn;
    @FXML private TableColumn<TableClientExecution, Integer> targetPriceColumn;
    @FXML private TableColumn<TableClientExecution, Integer> numOfExecutorsColumn;
    @FXML private TableColumn<TableClientExecution, Boolean> isRegisteredColumn;
    @FXML private TableColumn<TableClientExecution, String> statusColumn;

    @FXML private Button registerButton;
    @FXML private Label creditsLbl;
    @FXML private Label chosenLbl;

    public DashboardController() {
        autoUpdate = new SimpleBooleanProperty();
        totalUsers = new SimpleIntegerProperty();
    }

    @FXML
    public void initialize() {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<TableUser, String>("username"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<TableUser, String>("userType"));

        nameColumn.setCellValueFactory(new PropertyValueFactory<TableClientExecution, String>("name"));
        submittingUserColumn.setCellValueFactory(new PropertyValueFactory<TableClientExecution, String>("createdBy"));
        taskTypeColumn.setCellValueFactory(new PropertyValueFactory<TableClientExecution, String>("taskType"));
        targetsCountColumn.setCellValueFactory(new PropertyValueFactory<TableClientExecution, Integer>("targetAmount"));
        independentColumn.setCellValueFactory(new PropertyValueFactory<TableClientExecution, Integer>("middleCount"));
        leafColumn.setCellValueFactory(new PropertyValueFactory<TableClientExecution, Integer>("rootCount"));
        rootColumn.setCellValueFactory(new PropertyValueFactory<TableClientExecution, Integer>("leafCount"));
        middleColumn.setCellValueFactory(new PropertyValueFactory<TableClientExecution, Integer>("independentCount"));
        targetPriceColumn.setCellValueFactory(new PropertyValueFactory<TableClientExecution, Integer>("totalPrice"));
        numOfExecutorsColumn.setCellValueFactory(new PropertyValueFactory<TableClientExecution, Integer>("totalWorkers"));
        isRegisteredColumn.setCellValueFactory(new PropertyValueFactory<TableClientExecution, Boolean>("isRegistered"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<TableClientExecution, String>("status"));

        registerButton.setDisable(true);
    }

    @FXML
    void registerButtonAction(ActionEvent event) {
        clientEngine.subscribeToExecution(chosenExecution.getExecutionName());
        registerButton.setDisable(true);
    }


    private void updateUsersList(List<TableUser> usersNames) {
        Platform.runLater(() -> {
            ObservableList<TableUser> usersData = FXCollections.observableArrayList(usersNames);
            usersTable.getItems().clear();
            usersTable.setItems(usersData);
            totalUsers.set(usersNames.size());
        });
    }

    private void updateExecutionList(List<TableClientExecution> executionNames) {
        Platform.runLater(() -> {
            ObservableList<TableClientExecution> items = executionTable.getItems();
            items.clear();
            items.addAll(executionNames);
        });
    }

    private void updateCredits(Integer credits) {
        Platform.runLater(()->
        creditsLbl.setText(String.valueOf(credits)));
    }

    public void startListRefresher() {
        listRefresher = new UserListRefresher(
                autoUpdate,
                this::updateUsersList,
                this::updateCredits,
                clientEngine);
        timer = new Timer();
        timer.schedule(listRefresher, Constants.DELAY_RATE, Constants.REFRESH_RATE);
    }

    public void setActive(){
        startListRefresher();
        startTableExecutionsRefresher();
    }

    public void setInActive(){
        setAutoUpdate(false);
    }

    public void startTableExecutionsRefresher() {
        listRefresher = new TableExecutionRefresher(
                autoUpdate,
                this::updateExecutionList,
                clientEngine,clientEngine.getRegisteredExecutionsLock()
                );
        timer = new Timer();
        timer.schedule(listRefresher, Constants.DELAY_RATE, Constants.REFRESH_RATE);
    }

    public void setClientAppController(ClientAppController clientAppController) {
        this.clientAppController = clientAppController;
    }

    public void setClientEngine(ClientEngine clientEngine) {
        this.clientEngine = clientEngine;
    }

    public void setAutoUpdate(boolean newValue) {
        autoUpdate.set(newValue);
    }


    // Mouse press
    @FXML void userListMousePressed(MouseEvent event){
        gridPaneMousePressed(event);
    }

    @FXML void gridPaneMousePressed(MouseEvent event) {
        if (event.isPrimaryButtonDown() && event.getClickCount() == 1) {
            Platform.runLater(()-> {
                registerButton.setDisable(true);
                chosenLbl.setText("");
            });
        }
    }

    @FXML void executionMousePressed(MouseEvent event) {
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
                        chosenExecution = (TableClientExecution) row.getItem();
                        Platform.runLater(()-> {
                            chosenLbl.setText(chosenExecution.getExecutionName());
                            registerButton.setDisable(chosenExecution.getIsRegistered() || (!chosenExecution.getStatus().equals(ExecutionStatus.PLAYING.toString()) &&
                                    !chosenExecution.getStatus().equals(ExecutionStatus.RESUMED.toString()) &&
                                    !chosenExecution.getStatus().equals(ExecutionStatus.PAUSED.toString()) &&
                                    !chosenExecution.getStatus().equals(ExecutionStatus.STOPPED.toString())));
                        });
                    }
                }
            }
        }
    }


}




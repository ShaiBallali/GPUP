package main;

import currentlyrunning.CurrentlyRunningController;
import dashboard.DashboardController;
import dto.dtoServer.execution.TableClientExecution;
import dto.dtoServer.worker.server2worker.RunTimeExecutionDetails;
import engine.ClientEngine;
import javafx.scene.layout.StackPane;

import java.io.Closeable;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

public class ClientAppController implements Closeable {
    private ComponentsContainer componentsContainer;
    private ClientEngine clientEngine;
    private String currUsername;
    private TableClientExecution chosenExecution;
    private RunTimeExecutionDetails chosenExecutionPage2;
    private int currNumOfThreads;
    private String selectedSkin;
    private final static String CSS_SUFFIX = ".css";


    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private StackPane centerStackPane;



    @Override
    public void close() throws IOException {
        //chatRoomComponentController.close();
    }

    public void setCurrNumOfThreads(int currNumOfThreads) {
        this.currNumOfThreads = currNumOfThreads;
    }

    public void setCurrUsername(String currUsername) {
        this.currUsername = currUsername;
    }

    public void setChosenExecution(TableClientExecution chosenExecution) {
        this.chosenExecution = chosenExecution;
    }

    public void setChosenExecutionPage2(RunTimeExecutionDetails chosenExecutionPage2) {
        this.chosenExecutionPage2 = chosenExecutionPage2;
    }

    public void setDashboardPage(){
        componentsContainer.getNavbarComponentController().setEnabled();
        DashboardController dashboardController = componentsContainer.getDashboardComponentController();
        dashboardController.setClientEngine(clientEngine);
        dashboardController.setAutoUpdate(true);
        dashboardController.setActive();
        mainBorderPane.setCenter(componentsContainer.getDashboardComponent());

    }

    public void setCurrentlyRunningPage(){
        CurrentlyRunningController currentlyRunningController = componentsContainer.getCurrentlyRunningComponentController();
        currentlyRunningController.setClientEngine(clientEngine);
        currentlyRunningController.setActive();
        mainBorderPane.setCenter(componentsContainer.getCurrentlyRunningComponent());
    }

    public void setMainMenu () {
        StackPane mainMenu = componentsContainer.getNavbarComponent();
        mainBorderPane.setLeft(mainMenu);
    }

    private void onSetComponentsContainer () {
        // connect all controllers to the app controller
        componentsContainer.setMainController();
        // connect all controllers to their components
        componentsContainer.setControllersComponents();
    }

    public void onSetMyComponents () {
        // load welcome screen and main menu
        setMainMenu();
        mainBorderPane.setCenter(componentsContainer.getLoginComponent());
        componentsContainer.getNavbarComponentController().setDisabled();
        mainBorderPane.setCenter(componentsContainer.getLoginComponent());
        setRefreshersInActive();

    }

    public void setComponentsContainer(ComponentsContainer componentsContainer){
        this.componentsContainer = componentsContainer;
        onSetComponentsContainer();
    }

    public void setRefreshersInActive(){
        componentsContainer.getCurrentlyRunningComponentController().setInActive();
        componentsContainer.getDashboardComponentController().setInActive();
    }


    public void setClientEngine(ClientEngine clientEngine){
        this.clientEngine = clientEngine;
    }

    public void setSelectedSkin(String selectedSkin){
        this.selectedSkin = selectedSkin.concat(CSS_SUFFIX);
        this.selectedSkin = this.selectedSkin.replaceAll("\\s+","");
        mainBorderPane.getStylesheets().removeAll(mainBorderPane.getStylesheets());
        mainBorderPane.getStylesheets().add(getClass().getResource( this.selectedSkin ).toExternalForm());
    }

    public ClientEngine getClientEngine() {
        return clientEngine;
    }
}

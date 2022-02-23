package main;

import Util.Constants;
import dashboard.DashboardController;
import dto.dtoServer.execution.NewExecutionDetails;
import dto.dtoServer.execution.TableExecution;
import graphs.TableGraph;
import graphviz.GraphvizController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import loadXml.LoadXmlController;
import taskdatatable.TaskDataTableController;
import taskdetailsfill.compilationdetailsfill.CompilationDetailsFillController;
import taskdetailsfill.simulationdetailsfill.SimulationDetailsFillController;
import java.io.IOException;

public class AdminAppController {
    private ComponentsContainer componentsContainer;
    private TableGraph chosenTableGraph;
    private TableExecution chosenExecution;
    private String username;
    private NewExecutionDetails currExecution;
    private String selectedSkin;
    private final static String CSS_SUFFIX = ".css";

    private boolean wasWhatIfLoaded = false,
            wasDetectCircleLoaded = false,
            wasFindPathLoaded = false, wasSimulationDetailsFillLoaded = false,
            wasMidRunLoaded = false, wasTaskDataTablePageLoaded = false, wasGraphvizLoaded = false;


    @FXML private BorderPane mainBorderPane;

    public AdminAppController(){
        selectedSkin = Constants.DEFAULT_SKIN;
    }

    public void setSelectedSkin(String selectedSkin){
        this.selectedSkin = selectedSkin.concat(CSS_SUFFIX);
        this.selectedSkin = this.selectedSkin.replaceAll("\\s+","");
        mainBorderPane.getStylesheets().removeAll(mainBorderPane.getStylesheets());
        mainBorderPane.getStylesheets().add(getClass().getResource( this.selectedSkin ).toExternalForm());
    }

    public void setChosenTableGraph(TableGraph chosenTableGraph){
        this.chosenTableGraph = chosenTableGraph;
    }

    public void setChosenExecution(TableExecution chosenExecution) { this.chosenExecution = chosenExecution; }

    public TableGraph getChosenTableGraph(){
        return chosenTableGraph;
    }

    public void setCurrExecution(NewExecutionDetails currExecution){
        this.currExecution = currExecution;
    }

    public NewExecutionDetails getCurrExecution(){return currExecution;}

    public TableExecution getChosenExecution() {
        return chosenExecution;
    }

    public void setDashboardPage(){
        componentsContainer.getNavbarComponentController().setEnabled();
        DashboardController dashboardController = componentsContainer.getDashboardComponentController();
        dashboardController.setActive();
        mainBorderPane.setCenter(componentsContainer.getDashboardComponent());

    }

    public void setGraphDetailsPage () {
        componentsContainer.getTargetsDetailsComponentController().setTables(componentsContainer.getDataTableComponent(), componentsContainer.getDataTableComponentController());
        StackPane targetsDetailsComponent = componentsContainer.getTargetsDetailsComponent();
        mainBorderPane.setCenter(targetsDetailsComponent);
    }

    public void setLoadXmlPage(){
        setRefreshersInActive();
        LoadXmlController loadXmlController = componentsContainer.getLoadXmlComponentController();
        loadXmlController.initPage();
        componentsContainer.getDashboardComponentController().setAutoUpdate(false);
        mainBorderPane.setCenter(componentsContainer.getLoadXmlComponent());
    }

    public void setMainMenu () {
        StackPane mainMenu = componentsContainer.getNavbarComponent();
        mainBorderPane.setLeft(mainMenu);
    }

    public void setSubMenu(){
        StackPane subMenu = componentsContainer.getSubMenuComponent();
        mainBorderPane.setLeft(subMenu);
    }

    public void setGraphvizPage() {
        StackPane graphvizComponent = componentsContainer.getGraphvizComponent();
        GraphvizController graphvizController = componentsContainer.getGraphvizComponentController();
        mainBorderPane.setCenter(graphvizComponent);
        if (wasGraphvizLoaded)
            graphvizController.clear();
        wasGraphvizLoaded = true;
    }

    public void setFindPathPage() {
        setRefreshersInActive();
        componentsContainer.getFindPathComponentController().setTables(componentsContainer.getDataTableComponent(), componentsContainer.getDataTableComponentController());
        StackPane findPath = componentsContainer.getFindPathComponent();
        mainBorderPane.setCenter(findPath);
        if (wasFindPathLoaded)
            componentsContainer.getFindPathComponentController().clear();
        wasFindPathLoaded = true;
    }

    public void setMidRunPage(){
        setRefreshersInActive();
        StackPane midRunPage = componentsContainer.getMidRunTableComponent();
        componentsContainer.getMidRunTableComponentController().setActive();
        mainBorderPane.setCenter(midRunPage);
        if (wasMidRunLoaded)
            componentsContainer.getMidRunTableComponentController().clear();
        wasMidRunLoaded = true;
    }

    public void setWhatIfPage() {
        setRefreshersInActive();
        componentsContainer.getWhatIfController().setTables(componentsContainer.getDataTableComponent(), componentsContainer.getDataTableComponentController());
        StackPane whatIf = componentsContainer.getWhatIfComponent();
        mainBorderPane.setCenter(whatIf);
        if (wasWhatIfLoaded)
            componentsContainer.getWhatIfController().clear();
        wasWhatIfLoaded = true;
    }

    public void setSimulationDetailsFillPage(){
        setRefreshersInActive();
        StackPane simulationDetailsFillComponent = componentsContainer.getSimulationDetailsFillComponent();
        SimulationDetailsFillController simulationDetailsFillController = componentsContainer.getSimulationDetailsFillComponentController();
        mainBorderPane.setCenter(simulationDetailsFillComponent);
        if (wasSimulationDetailsFillLoaded)
            simulationDetailsFillController.clear();
        wasSimulationDetailsFillLoaded = true;
    }

    public void setCompilationDetailsFillPage(){
        setRefreshersInActive();
        StackPane compilationDetailsFillComponent = componentsContainer.getCompilationDetailsFillComponent();
        CompilationDetailsFillController compilationDetailsFillController = componentsContainer.getCompilationDetailsFillComponentController();
        mainBorderPane.setCenter(compilationDetailsFillComponent);
        if (wasSimulationDetailsFillLoaded)
            compilationDetailsFillController.clear();
        wasSimulationDetailsFillLoaded = true;
    }

    public void setTaskDataTablePage() { // target selection
        setRefreshersInActive();
        TaskDataTableController taskDatatableController = componentsContainer.getTaskDataTableComponentController();
        if (wasTaskDataTablePageLoaded)
            taskDatatableController.clear();
        wasTaskDataTablePageLoaded = true;
        taskDatatableController.onStart();
        taskDatatableController.setAllTargetsDetails(getChosenTableGraph());
        StackPane chooseTargetsPage = componentsContainer.getTaskDataTableComponent();
        mainBorderPane.setCenter(chooseTargetsPage);
    }

    public void setDetectCirclesPage(){

            setRefreshersInActive();
            componentsContainer.getDetectCirclesComponentController().setTables(componentsContainer.getDataTableComponent(), componentsContainer.getDataTableComponentController());
            StackPane detectCircles = componentsContainer.getDetectCirclesComponent();
            mainBorderPane.setCenter(detectCircles);
            if (wasDetectCircleLoaded)
                componentsContainer.getDetectCirclesComponentController().clear();
            wasDetectCircleLoaded = true;
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
        componentsContainer.getNavbarComponentController().setDisabled();
        mainBorderPane.setCenter(componentsContainer.getLoginComponent());
        setRefreshersInActive();
    }

    public void setComponentsContainer(ComponentsContainer componentsContainer){
        this.componentsContainer = componentsContainer;
        onSetComponentsContainer();
    }

    public Button getGraphActionsButton(){
        return componentsContainer.getNavbarComponentController().getGraphActionsBtn();
    }
    public void updateUsername(String username){
        this.username = username;
    }

    public String getUsername(){
        return username;
    }

    public void setRefreshersInActive(){
        componentsContainer.getMidRunTableComponentController().setInActive();
        componentsContainer.getDashboardComponentController().setInActive();
    }

}

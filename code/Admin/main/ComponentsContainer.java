package main;

import dashboard.DashboardController;
import datatable.DataTableController;
import detectCircle.DetectCirclesController;
import findPath.FindPathController;
import graphviz.GraphvizController;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import loadXml.LoadXmlController;
import login.LoginController;
import midRunTable.MidRunTableController;
import navbaradmin.NavbarController;
import navbaradmin.submenu.SubMenuController;
import targetsdetails.TargetsDetailsController;
import taskdatatable.TaskDataTableController;
import taskdetailsfill.compilationdetailsfill.CompilationDetailsFillController;
import taskdetailsfill.simulationdetailsfill.SimulationDetailsFillController;
import whatif.WhatIfController;


public class ComponentsContainer {

    // main page
    private AdminAppController appComponentController;

    // login menu
    private StackPane loginComponent;
    private LoginController loginComponentController;

    // dashboard menu
    private StackPane dashboardComponent;
    private DashboardController dashboardComponentController;

    // navbar
    private StackPane navbarComponent;
    private NavbarController navbarComponentController;

    // sub menu
    private StackPane subMenuComponent;
    private SubMenuController subMenuComponentController;

    // load xml
    private StackPane loadXmlComponent;
    private LoadXmlController loadXmlComponentController;

    //detect Circle
    private StackPane detectCirclesComponent;
    private DetectCirclesController detectCirclesComponentController;

    // find path
    private StackPane findPathComponent;
    private FindPathController findPathComponentController;

    // what if
    private StackPane whatIfComponent;
    private WhatIfController whatIfComponentController;

    // data table
    private StackPane dataTableComponent;
    private DataTableController dataTableComponentController;

    // simulation details fill
    private StackPane simulationDetailsFillComponent;
    private SimulationDetailsFillController simulationDetailsFillComponentController;

    // task data table
    private StackPane taskDataTableComponent;
    private TaskDataTableController taskDataTableComponentController;

    // compilation details fill
    private StackPane compilationDetailsFillComponent;
    private CompilationDetailsFillController compilationDetailsFillComponentController;

    // mid run table
    private StackPane midRunTableComponent;
    private MidRunTableController midRunTableComponentController;

    // target details
    private StackPane targetsDetailsComponent;
    private TargetsDetailsController targetsDetailsComponentController;

        // graphviz
    private StackPane graphvizComponent;
    private GraphvizController graphvizComponentController;



    public void setMainController () {
        loginComponentController.setAdminAppController(appComponentController);
        dashboardComponentController.setAdminAppController(appComponentController);
        navbarComponentController.setAdminAppController(appComponentController);
        subMenuComponentController.setAdminAppController(appComponentController);
        findPathComponentController.setAdminAppController(appComponentController);
        detectCirclesComponentController.setAdminAppController(appComponentController);
        whatIfComponentController.setAdminAppController(appComponentController);
        simulationDetailsFillComponentController.setAdminAppController(appComponentController);
        compilationDetailsFillComponentController.setAdminAppController(appComponentController);
        taskDataTableComponentController.setAdminAppController(appComponentController);
        midRunTableComponentController.setAdminAppController(appComponentController);
        targetsDetailsComponentController.setAdminAppController(appComponentController);
        graphvizComponentController.setAdminAppController(appComponentController);
    }

    public void setControllersComponents(){
        appComponentController.onSetMyComponents();
    }

    public void setAppComponentController (AdminAppController appComponentController) {
        this.appComponentController = appComponentController;
    }

    public void setNavbarComponent (StackPane navbarComponent) {
        this.navbarComponent = navbarComponent;
    }

    public void setNavbarComponentController (NavbarController navbarComponentController) {
        this.navbarComponentController = navbarComponentController;
    }

    public StackPane getNavbarComponent () {
        return this.navbarComponent;
    }

    public NavbarController getNavbarComponentController () {
        return this.navbarComponentController;
    }

    // ----- //

    public void setTargetsDetailsComponent(StackPane targetsDetailsComponent) {
        this.targetsDetailsComponent = targetsDetailsComponent;
    }

    public void setTargetsDetailsComponentController(TargetsDetailsController targetsDetailsComponentController) {
        this.targetsDetailsComponentController = targetsDetailsComponentController;
    }



    public StackPane getTargetsDetailsComponent() {
        return targetsDetailsComponent;
    }

    public TargetsDetailsController getTargetsDetailsComponentController() {
        return targetsDetailsComponentController;
    }

    // ---- //
    public void setSubMenuComponent (StackPane subMenuComponent) {
        this.subMenuComponent = subMenuComponent;
    }

    public void setSubMenuComponentController (SubMenuController subMenuComponentController) {
        this.subMenuComponentController = subMenuComponentController;
    }

    public StackPane getSubMenuComponent () {
        return this.subMenuComponent;
    }

    //------//

    public void setDashboardComponent (StackPane dashboardComponent) {
        this.dashboardComponent = dashboardComponent;
    }

    public void setDashboardComponentController (DashboardController dashboardComponentController) {
        this.dashboardComponentController = dashboardComponentController;
    }

    public StackPane getDashboardComponent () {
        return this.dashboardComponent;
    }

    public DashboardController getDashboardComponentController () {
        return this.dashboardComponentController;
    }

    // ---- //


    public void setLoginComponent (StackPane loginComponent) {
        this.loginComponent = loginComponent;
    }

    public void setLoginComponentController (LoginController loginComponentController) {
        this.loginComponentController = loginComponentController;
    }

    public StackPane getLoginComponent () {
        return this.loginComponent;
    }

    // ---- //

    public void setLoadXmlComponent (StackPane loadXmlComponent) {
        this.loadXmlComponent = loadXmlComponent;
    }

    public void setLoadXmlComponentController (LoadXmlController loadXmlComponentController) {
        this.loadXmlComponentController = loadXmlComponentController;
    }

    public StackPane getLoadXmlComponent () {
        return this.loadXmlComponent;
    }

    public LoadXmlController getLoadXmlComponentController () {return this.loadXmlComponentController;}

    // ----- //

    public void setDetectCirclesComponent(StackPane detectCirclesComponent) {
        this.detectCirclesComponent = detectCirclesComponent;
    }

    public void setDetectCirclesComponentController(DetectCirclesController detectCirclesComponentController) {
        this.detectCirclesComponentController = detectCirclesComponentController;
    }

    public StackPane getDetectCirclesComponent() {
        return detectCirclesComponent;
    }

    public DetectCirclesController getDetectCirclesComponentController() {
        return detectCirclesComponentController;
    }

    // --- //

    public void setFindPathComponent(StackPane findPathComponent) {
        this.findPathComponent = findPathComponent;
    }

    public void setFindPathComponentController(FindPathController findPathComponentController) {this.findPathComponentController = findPathComponentController;}

    public StackPane getFindPathComponent() {
        return findPathComponent;
    }

    public FindPathController getFindPathComponentController() {
        return findPathComponentController;
    }

    // --- //

    public StackPane getWhatIfComponent(){
        return whatIfComponent;
    }

    public WhatIfController getWhatIfController() {
        return whatIfComponentController;
    }

    public void setWhatIfController(WhatIfController whatIfController){this.whatIfComponentController = whatIfController;}

    public void setWhatIfComponent(StackPane whatIfComponent){
        this.whatIfComponent = whatIfComponent;
    }

    // --- //
    public TaskDataTableController getTaskDataTableComponentController(){return this.taskDataTableComponentController;}

    public StackPane getTaskDataTableComponent(){
        return this.taskDataTableComponent;
    }

    public void setTaskDataTableComponentController(TaskDataTableController taskDataTableComponentController){
        this.taskDataTableComponentController = taskDataTableComponentController;
    }

    public void setTaskDataTableComponent(StackPane taskDataTableComponent){
        this.taskDataTableComponent = taskDataTableComponent;
    }
    /*------*/

    public StackPane getDataTableComponent() { return this.dataTableComponent;}

    public DataTableController getDataTableComponentController() {return this.dataTableComponentController;}

    public void setDataTableComponent(StackPane dataTableComponent) {this.dataTableComponent = dataTableComponent;}

    public void setDataTableComponentController(DataTableController dataTableComponentController) {this.dataTableComponentController = dataTableComponentController;}

    //------//

    public SimulationDetailsFillController getSimulationDetailsFillComponentController(){return this.simulationDetailsFillComponentController;}

    public StackPane getSimulationDetailsFillComponent(){return this.simulationDetailsFillComponent;}

    public void setSimulationDetailsFillComponentController(SimulationDetailsFillController simulationDetailsFillComponentController){ this.simulationDetailsFillComponentController = simulationDetailsFillComponentController;}

    public void setSimulationDetailsFillComponent(StackPane simulationDetailsFillComponent){this.simulationDetailsFillComponent = simulationDetailsFillComponent;}

    //-------//

    public CompilationDetailsFillController getCompilationDetailsFillComponentController(){return this.compilationDetailsFillComponentController;}

    public StackPane getCompilationDetailsFillComponent(){return this.compilationDetailsFillComponent;}

    public void setCompilationDetailsFillComponent(StackPane compilationDetailsFillComponent){this.compilationDetailsFillComponent = compilationDetailsFillComponent;}

    public void setCompilationDetailsFillComponentController(CompilationDetailsFillController compilationDetailsFillComponentController){this.compilationDetailsFillComponentController = compilationDetailsFillComponentController;}

    //-------//

    public MidRunTableController getMidRunTableComponentController(){return this.midRunTableComponentController;}

    public StackPane getMidRunTableComponent() {
        return midRunTableComponent;
    }

    public void setMidRunTableComponent(StackPane midRunTableComponent) {
        this.midRunTableComponent = midRunTableComponent;
    }

    public void setMidRunTableComponentController(MidRunTableController midRunTableComponentController) {
        this.midRunTableComponentController = midRunTableComponentController;
    }

    /*--------*/

    public GraphvizController getGraphvizComponentController(){return this.graphvizComponentController;}

    public StackPane getGraphvizComponent(){return this.graphvizComponent;}

    public void setGraphvizComponent(StackPane graphvizComponent){this.graphvizComponent = graphvizComponent;}

    public void setGraphvizComponentController(GraphvizController graphvizComponentController){this.graphvizComponentController = graphvizComponentController;}
}



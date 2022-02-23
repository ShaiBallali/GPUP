import dashboard.DashboardController;
import datatable.DataTableController;
import detectCircle.DetectCirclesController;
import findPath.FindPathController;
import graphviz.GraphvizController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import loadXml.LoadXmlController;
import login.LoginController;
import main.AdminAppController;
import main.ComponentsContainer;
import midRunTable.MidRunTableController;
import navbaradmin.NavbarController;
import navbaradmin.submenu.SubMenuController;
import targetsdetails.TargetsDetailsController;
import taskdatatable.TaskDataTableController;
import taskdetailsfill.compilationdetailsfill.CompilationDetailsFillController;
import taskdetailsfill.simulationdetailsfill.SimulationDetailsFillController;
import whatif.WhatIfController;

import java.net.URL;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader;
        URL url;

        ComponentsContainer componentsContainer = new ComponentsContainer();

        // login component
        fxmlLoader = new FXMLLoader();
        url = getClass().getResource("/login/login.fxml");
        fxmlLoader.setLocation(url);
        StackPane loginComponent = fxmlLoader.load(url.openStream());
        LoginController loginComponentController = fxmlLoader.getController();

        componentsContainer.setLoginComponent(loginComponent);
        componentsContainer.setLoginComponentController(loginComponentController);

        // load xml component
        fxmlLoader = new FXMLLoader();
        url = getClass().getResource("/loadXml/loadXml.fxml");
        fxmlLoader.setLocation(url);
        StackPane loadXmlComponent = fxmlLoader.load(url.openStream());
        LoadXmlController loadXmlComponentController = fxmlLoader.getController();

        componentsContainer.setLoadXmlComponent(loadXmlComponent);
        componentsContainer.setLoadXmlComponentController(loadXmlComponentController);

        // dashboard component
        fxmlLoader = new FXMLLoader();
        url = getClass().getResource("/dashboard/dashboard.fxml");
        fxmlLoader.setLocation(url);
        StackPane dashboardComponent = fxmlLoader.load(url.openStream());
        DashboardController dashboardComponentController = fxmlLoader.getController();

        componentsContainer.setDashboardComponent(dashboardComponent);
        componentsContainer.setDashboardComponentController(dashboardComponentController);

        // graph details component
        fxmlLoader = new FXMLLoader();
        url = getClass().getResource("/targetsdetails/targetsDetails.fxml");
        fxmlLoader.setLocation(url);
        StackPane targetsDetailsComponent = fxmlLoader.load(url.openStream());
        TargetsDetailsController targetsDetailsComponentController = fxmlLoader.getController();

        componentsContainer.setTargetsDetailsComponent(targetsDetailsComponent);
        componentsContainer.setTargetsDetailsComponentController(targetsDetailsComponentController);

        // load main menu page
        fxmlLoader = new FXMLLoader();
        url = getClass().getResource("/navbaradmin/navbar.fxml");
        fxmlLoader.setLocation(url);
        StackPane navbarComponent = fxmlLoader.load(url.openStream());
        NavbarController navbarComponentController = fxmlLoader.getController();

        componentsContainer.setNavbarComponent(navbarComponent);
        componentsContainer.setNavbarComponentController(navbarComponentController);

        // load sub menu page
        fxmlLoader = new FXMLLoader();
        url = getClass().getResource("/navbaradmin/submenu/submenu.fxml");
        fxmlLoader.setLocation(url);
        StackPane subMenuComponent = fxmlLoader.load(url.openStream());
        SubMenuController subMenuComponentController = fxmlLoader.getController();

        componentsContainer.setSubMenuComponent(subMenuComponent);
        componentsContainer.setSubMenuComponentController(subMenuComponentController);

        // load data table component
        fxmlLoader = new FXMLLoader();
        url = getClass().getResource("/datatable/dataTable.fxml");
        fxmlLoader.setLocation(url);
        StackPane dataTableComponent = fxmlLoader.load(url.openStream());
        DataTableController dataTableComponentController = fxmlLoader.getController();

        componentsContainer.setDataTableComponent(dataTableComponent);
        componentsContainer.setDataTableComponentController(dataTableComponentController);

        // load task data table component
        fxmlLoader = new FXMLLoader();
        url = getClass().getResource("/taskdatatable/taskDataTable.fxml");
        fxmlLoader.setLocation(url);
        StackPane taskDataTableComponent = fxmlLoader.load(url.openStream());
        TaskDataTableController taskDataTableComponentController = fxmlLoader.getController();

        componentsContainer.setTaskDataTableComponent(taskDataTableComponent);
        componentsContainer.setTaskDataTableComponentController(taskDataTableComponentController);

        // load detect circles page
        fxmlLoader = new FXMLLoader();
        url = getClass().getResource("/detectCircle/detectCircle.fxml");
        fxmlLoader.setLocation(url);
        StackPane detectCirclesComponent = fxmlLoader.load(url.openStream());
        DetectCirclesController detectCirclesComponentController = fxmlLoader.getController();

        componentsContainer.setDetectCirclesComponent(detectCirclesComponent);
        componentsContainer.setDetectCirclesComponentController(detectCirclesComponentController);

        // find path page
        fxmlLoader = new FXMLLoader();
        url = getClass().getResource("/findPath/findPath.fxml");
        fxmlLoader.setLocation(url);
        StackPane findPathComponent = fxmlLoader.load(url.openStream());
        FindPathController findPathComponentController = fxmlLoader.getController();

        componentsContainer.setFindPathComponent(findPathComponent);
        componentsContainer.setFindPathComponentController(findPathComponentController);

        // what if page
        fxmlLoader = new FXMLLoader();
        url = getClass().getResource("/whatif/whatIf.fxml");
        fxmlLoader.setLocation(url);
        StackPane whatIfComponent = fxmlLoader.load(url.openStream());
        WhatIfController whatIfComponentController = fxmlLoader.getController();

        componentsContainer.setWhatIfComponent(whatIfComponent);
        componentsContainer.setWhatIfController(whatIfComponentController);

        // simulation details fill
        fxmlLoader = new FXMLLoader();
        url = getClass().getResource("/taskdetailsfill/simulationdetailsfill/simulationDetailsFill.fxml");
        fxmlLoader.setLocation(url);
        StackPane simulationDetailsFillComponent = fxmlLoader.load(url.openStream());
        SimulationDetailsFillController simulationDetailsFillComponentController = fxmlLoader.getController();

        componentsContainer.setSimulationDetailsFillComponent(simulationDetailsFillComponent);
        componentsContainer.setSimulationDetailsFillComponentController(simulationDetailsFillComponentController);

         //compilation details fill
        fxmlLoader = new FXMLLoader();
        url = getClass().getResource("/taskdetailsfill/compilationdetailsfill/compilationDetailsFill.fxml");
        fxmlLoader.setLocation(url);
        StackPane compilationDetailsFillComponent = fxmlLoader.load(url.openStream());
        CompilationDetailsFillController compilationDetailsFillComponentController = fxmlLoader.getController();

        componentsContainer.setCompilationDetailsFillComponent(compilationDetailsFillComponent);
        componentsContainer.setCompilationDetailsFillComponentController(compilationDetailsFillComponentController);

        //compilation details fill
        fxmlLoader = new FXMLLoader();
        url = getClass().getResource("/midRunTable/midRunTable.fxml");
        fxmlLoader.setLocation(url);
        StackPane midRunTableComponent = fxmlLoader.load(url.openStream());
        MidRunTableController midRunTableComponentController = fxmlLoader.getController();

        componentsContainer.setMidRunTableComponent(midRunTableComponent);
        componentsContainer.setMidRunTableComponentController(midRunTableComponentController);

        // graphviz
        fxmlLoader = new FXMLLoader();
        url = getClass().getResource("/graphviz/graphviz.fxml");
        fxmlLoader.setLocation(url);
        StackPane graphvizComponent = fxmlLoader.load(url.openStream());
        GraphvizController graphvizComponentController = fxmlLoader.getController();

        componentsContainer.setGraphvizComponent(graphvizComponent);
        componentsContainer.setGraphvizComponentController(graphvizComponentController);

        // load main page
        fxmlLoader = new FXMLLoader();
        url = getClass().getResource("/main/app.fxml");
        fxmlLoader.setLocation(url);
        ScrollPane root = fxmlLoader.load(url.openStream());
        AdminAppController clientAppComponentController = fxmlLoader.getController();

        componentsContainer.setAppComponentController(clientAppComponentController);

        clientAppComponentController.setComponentsContainer(componentsContainer);

        Scene scene = new Scene(root, 1500, 900);

        primaryStage.setTitle("GPUP - ADMIN");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

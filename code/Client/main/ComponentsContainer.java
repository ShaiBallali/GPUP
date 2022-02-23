package main;

import currentlyrunning.CurrentlyRunningController;
import dashboard.DashboardController;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import login.LoginController;
import navbar.NavbarController;

public class ComponentsContainer {

    // main page
    private ClientAppController appComponentController;

    // login menu
    private StackPane loginComponent;
    private LoginController loginComponentController;

    // dashboard menu
    private StackPane dashboardComponent;
    private DashboardController dashboardComponentController;

    // navbar
    private StackPane navbarComponent;
    private NavbarController navbarComponentController;

    //mid run
    private StackPane currentlyRunningComponent;
    private CurrentlyRunningController currentlyRunningComponentController;



    public void setMainController () {
        loginComponentController.setClientAppController(appComponentController);
        dashboardComponentController.setClientAppController(appComponentController);
        navbarComponentController.setClientAppController(appComponentController);
        currentlyRunningComponentController.setClientAppController(appComponentController);
    }

    public void setControllersComponents(){
        appComponentController.onSetMyComponents();
    }

    public void setAppComponentController (ClientAppController appComponentController) {
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


    // ---- //


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

    //------//


    public void setCurrentlyRunningComponent (StackPane currentlyRunningComponent) {
        this.currentlyRunningComponent = currentlyRunningComponent;
    }

    public void setCurrentlyRunningComponentController (CurrentlyRunningController currentlyRunningComponentController) {
        this.currentlyRunningComponentController = currentlyRunningComponentController;
    }

    public StackPane getCurrentlyRunningComponent () {
        return this.currentlyRunningComponent;
    }

    public CurrentlyRunningController getCurrentlyRunningComponentController () {
        return this.currentlyRunningComponentController;
    }



}
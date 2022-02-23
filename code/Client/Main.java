import currentlyrunning.CurrentlyRunningController;
import dashboard.DashboardController;
import javafx.scene.Scene;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import login.LoginController;
import main.ClientAppController;
import main.ComponentsContainer;
import navbar.NavbarController;
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

        // dashboard component
        fxmlLoader = new FXMLLoader();
        url = getClass().getResource("/dashboard/dashboard.fxml");
        fxmlLoader.setLocation(url);
        StackPane dashboardComponent = fxmlLoader.load(url.openStream());
        DashboardController dashboardComponentController = fxmlLoader.getController();

        componentsContainer.setDashboardComponent(dashboardComponent);
        componentsContainer.setDashboardComponentController(dashboardComponentController);

        // load main menu page
        fxmlLoader = new FXMLLoader();
        url = getClass().getResource("/navbar/navbar.fxml");
        fxmlLoader.setLocation(url);
        StackPane navbarComponent = fxmlLoader.load(url.openStream());
        NavbarController navbarComponentController = fxmlLoader.getController();

        componentsContainer.setNavbarComponent(navbarComponent);
        componentsContainer.setNavbarComponentController(navbarComponentController);

        // load main menu page
        fxmlLoader = new FXMLLoader();
        url = getClass().getResource("/currentlyrunning/currentlyrunning.fxml");
        fxmlLoader.setLocation(url);
        StackPane currentlyRunningComponent = fxmlLoader.load(url.openStream());
        CurrentlyRunningController currentlyRunningComponentController = fxmlLoader.getController();

        componentsContainer.setCurrentlyRunningComponent(currentlyRunningComponent);
        componentsContainer.setCurrentlyRunningComponentController(currentlyRunningComponentController);



        // load main page
        fxmlLoader = new FXMLLoader();
        url = getClass().getResource("/main/app.fxml");
        fxmlLoader.setLocation(url);
        ScrollPane root = fxmlLoader.load(url.openStream());
        ClientAppController clientAppComponentController = fxmlLoader.getController();

        componentsContainer.setAppComponentController(clientAppComponentController);

        clientAppComponentController.setComponentsContainer(componentsContainer);

        Scene scene = new Scene(root, 1500, 900);

        primaryStage.setTitle("GPUP - CLIENT");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

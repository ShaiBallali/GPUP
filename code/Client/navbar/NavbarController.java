package navbar;

import Util.Constants;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.StackPane;
import main.ClientAppController;
import java.util.ArrayList;

public class NavbarController {

    private ClientAppController clientAppController;
    private ArrayList<String> skins;


    @FXML private Button dashboardBtn;
    @FXML private Button currentlyRunningBtn;
    @FXML private ComboBox<String> chooseSkinCB;

    public NavbarController(){
        skins = new ArrayList<>();
    }

    @FXML public void initialize(){
        skins.add(Constants.DARK_THEME_SKIN);
        skins.add(Constants.DEFAULT_SKIN);
        chooseSkinCB.setItems(FXCollections.observableArrayList(skins));
    }

    @FXML
    void currentlyRunningBtnAction(ActionEvent event) {
        clientAppController.setCurrentlyRunningPage();
    }

    @FXML
    void dashboardBtnAction(ActionEvent event) {
        clientAppController.setDashboardPage();
    }

    public void setDisabled(){
        dashboardBtn.disableProperty().set(true);
        currentlyRunningBtn.disableProperty().set(true);
    }

    public void setEnabled(){
        dashboardBtn.disableProperty().set(false);
        currentlyRunningBtn.disableProperty().set(false);
    }

    public void setClientAppController(ClientAppController clientAppController){
        this.clientAppController = clientAppController;
    }

    @FXML void chooseSkinCBAction(ActionEvent event) {
        clientAppController.setSelectedSkin(chooseSkinCB.getValue()); // app controller can set skins
    }

}

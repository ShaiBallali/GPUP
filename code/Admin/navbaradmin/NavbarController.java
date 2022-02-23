package navbaradmin;

import Util.Constants;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import main.AdminAppController;
import java.util.ArrayList;

public class NavbarController {


    private AdminAppController adminAppController;
    private final ArrayList<String> skins;


    @FXML private Button loadXmlBtn;
    @FXML private Button dashboardBtn;
    @FXML private Button graphActionsBtn;
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
    void loadXmlBtnAction() {
        adminAppController.setLoadXmlPage();
    }

    @FXML
    void dashboardBtnAction(ActionEvent event) {
        adminAppController.setDashboardPage();
    }

    public void setAdminAppController(AdminAppController adminAppController){
        this.adminAppController = adminAppController;
    }

    public void setDisabled(){
        dashboardBtn.disableProperty().set(true);
        loadXmlBtn.disableProperty().set(true);
        graphActionsBtn.disableProperty().set(true);

    }
    public void setEnabled(){
        dashboardBtn.disableProperty().set(false);
        loadXmlBtn.disableProperty().set(false);
    }


    @FXML void chooseSkinCBAction(ActionEvent event) {
        adminAppController.setSelectedSkin(chooseSkinCB.getValue()); // app controller can set skins
    }

    @FXML
    void graphActionsBtnAction(ActionEvent event) {
        adminAppController.setSubMenu();
    }

    public Button getGraphActionsBtn() {
        return graphActionsBtn;
    }
}

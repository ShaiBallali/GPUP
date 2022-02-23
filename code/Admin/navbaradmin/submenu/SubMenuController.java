package navbaradmin.submenu;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import main.AdminAppController;

public class SubMenuController {

    private AdminAppController adminAppController;

    @FXML
    void detectCircleBtnAction(ActionEvent event) {
       adminAppController.setDetectCirclesPage();
    }

    @FXML
    void findPathBtnAction(ActionEvent event) {
        adminAppController.setFindPathPage();
    }

    @FXML
    void returnBtn(ActionEvent event) {
        adminAppController.setMainMenu();
        adminAppController.setDashboardPage();
    }

    @FXML
    void whatIfBtnAction(ActionEvent event) {
        adminAppController.setWhatIfPage();
    }

    public void setAdminAppController(AdminAppController adminAppController) {
        this.adminAppController = adminAppController;
    }

    @FXML void graphDetailsBtnAction(ActionEvent event) {
        adminAppController.setGraphDetailsPage();
    }

    @FXML
    void graphvizBtnAction(ActionEvent event) {
        adminAppController.setGraphvizPage();
    }

}

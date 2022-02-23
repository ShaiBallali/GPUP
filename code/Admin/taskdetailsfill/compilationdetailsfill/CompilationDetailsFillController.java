package taskdetailsfill.compilationdetailsfill;

import Util.Constants;
import Util.http.HttpClientUtil;
import dto.dtoServer.execution.NewExecutionDetails;
import dto.enums.TaskName;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import main.AdminAppController;
import okhttp3.HttpUrl;
import okhttp3.Response;
import java.io.File;

public class CompilationDetailsFillController {

    @FXML private TextField mainDirectoryPathTF;
    @FXML private TextField destDirectoryPathTF;
    @FXML private Button executeBtn;
    @FXML private Label msgLbl;
    @FXML private TextField executionNameTF;
    @FXML private Button chooseDestPathBtn;

    private AdminAppController adminAppController;
    private String absolutePath;
    private String mainPath;
    private String destPath;
    private String executionName;
    private String currGraphName;

    private final String ILLEGAL_PATH = "No legal route was selected";

    private final SimpleBooleanProperty areAllDetailsFilled;
    private final SimpleBooleanProperty isMainPathSelected;
    private final SimpleBooleanProperty isDestPathSelected;
    private final SimpleStringProperty mainDirectoryPathProperty;
    private final SimpleStringProperty destDirectoryPathProperty;
    private final SimpleBooleanProperty isExecutionNameSelected;
    private final SimpleStringProperty msgProperty;


    public CompilationDetailsFillController() {
        areAllDetailsFilled = new SimpleBooleanProperty(false);
        isExecutionNameSelected = new SimpleBooleanProperty(false);
        isMainPathSelected = new SimpleBooleanProperty(false);
        mainDirectoryPathProperty = new SimpleStringProperty();
        destDirectoryPathProperty = new SimpleStringProperty();
        isDestPathSelected = new SimpleBooleanProperty();
        msgProperty = new SimpleStringProperty();
    }

    @FXML
    public void initialize() {
        executeBtn.disableProperty().bind(areAllDetailsFilled.not());
        mainDirectoryPathTF.disableProperty().bind(isMainPathSelected.not());
        destDirectoryPathTF.disableProperty().bind(isDestPathSelected.not());
        mainDirectoryPathTF.textProperty().bind(mainDirectoryPathProperty); // Show chosen main directory path on the text field
        destDirectoryPathTF.textProperty().bind(destDirectoryPathProperty); // Show chosen dest directory path on the text field
        chooseDestPathBtn.disableProperty().bind(isMainPathSelected.not());
        executionNameTF.disableProperty().bind(isDestPathSelected.not());
        executionNameTF.setText("");


        msgLbl.textProperty().bind(msgProperty);

        executionNameTF.textProperty().addListener(new ChangeListener<String>() { // Processing time text field listens to corresponding slider
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {

                if (!newValue.equals("")) {
                    isExecutionNameSelected.set(true);
                    msgProperty.set(""); // Clear error message
                    if (isMainPathSelected.get())
                        areAllDetailsFilled.set(isDestPathSelected.get());
                    else
                        areAllDetailsFilled.set(false);

                } else {
                    isExecutionNameSelected.set(false);
                    areAllDetailsFilled.set(false);
                }
            }
        });


    }

    @FXML
    void executeBtnAction(ActionEvent event) {
        mainPath = mainDirectoryPathProperty.get().replace("\\", "/");
        destPath = destDirectoryPathProperty.get().replace("\\", "/");
        executionName = executionNameTF.getText();
        currGraphName = adminAppController.getChosenTableGraph().getGraphName();
        /*---------*/
        try {
            String finalUrl = HttpUrl
                    .parse(Constants.IS_VALID_NAME)
                    .newBuilder()
                    .addQueryParameter("executionName", executionName)
                    .build()
                    .toString();

            Response response = HttpClientUtil.runSync(finalUrl);
            String responseBody = response.body().string();
            if (response.code() != 200) {

                Platform.runLater(() ->
                        msgProperty.set("Something went wrong: " + responseBody)
                );
            } else {
                Platform.runLater(() -> {
                    try {
                        NewExecutionDetails currExecution = new NewExecutionDetails(executionName, currGraphName, TaskName.COMPILATION);
                        currExecution.initCompilationDetails(mainPath, destPath);
                        adminAppController.setCurrExecution(currExecution);
                        adminAppController.setTaskDataTablePage();
                    } catch (Exception e) {
                        msgProperty.set("Something went wrong: " + e.getMessage());
                    }

                });
            }
        } catch(Exception e) {
            msgProperty.set(e.getMessage());
        }

    }

    @FXML void mainDirectoryTFMouseClick(MouseEvent event) {
        msgLbl.setText("");
    }

    @FXML public void chooseMainPathBtnAction(){
        DirectoryChooser fc = new DirectoryChooser();
        File selectedFile = fc.showDialog(null);

        if (selectedFile != null) {
            absolutePath = selectedFile.getAbsolutePath();
            isMainPathSelected.set(true);
            mainDirectoryPathProperty.set(absolutePath);
        }
        else {
            msgProperty.set(ILLEGAL_PATH);
        }

    }

    @FXML public void chooseDestPathBtnAction(){
        DirectoryChooser fc = new DirectoryChooser(); // Opening dialog upon click
        File selectedFile = fc.showDialog(null);

        if (selectedFile != null) {
            absolutePath = selectedFile.getAbsolutePath();
            isDestPathSelected.set(true);
            destDirectoryPathProperty.set(absolutePath);
        }
        else {
            msgProperty.set(ILLEGAL_PATH);
        }
    }

    public void setAdminAppController(AdminAppController adminAppController) {
        this.adminAppController = adminAppController;
    }


    public void clear(){ // Refresh page for the next time the user gets to this page
        msgProperty.set("");
        mainDirectoryPathProperty.set("");
        destDirectoryPathProperty.set("");
        executionNameTF.setText("");
        areAllDetailsFilled.set(false);
    }

}

package graphviz;

import Util.Constants;
import Util.http.HttpClientUtil;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import main.AdminAppController;
import okhttp3.HttpUrl;
import okhttp3.Response;

import java.io.File;

public class GraphvizController {

    @FXML private TextField directoryTF;
    @FXML private Button saveBtn;
    @FXML private TextField fileNameTF;
    @FXML private Label msgLbl;
    @FXML private Label textFileLB;
    @FXML private Label imageFileLB;
    @FXML private ImageView imagePlaceholder;
    @FXML private Button showGraphvizBtn;

    private static final String ILLEGAL_PATH = "No legal path was selected";
    private static final String SUCCESS_MSG = "File was saved successfully.";

    private final SimpleBooleanProperty areAllDetailsFilled;
    private final SimpleBooleanProperty isPathSelected;
    private final SimpleBooleanProperty isNameSelected;
    private final SimpleStringProperty directoryPath;
    private AdminAppController adminAppController;

    public GraphvizController(){
        isPathSelected = new SimpleBooleanProperty(false);
        isNameSelected = new SimpleBooleanProperty(false);
        areAllDetailsFilled = new SimpleBooleanProperty(false);
        directoryPath = new SimpleStringProperty();

    }

    @FXML public void initialize() {
        saveBtn.disableProperty().bind(areAllDetailsFilled.not()); // Save only after you entered directory path and file name
        directoryTF.textProperty().bind(directoryPath); // Show the chosen directory with the Directory Dialog on directory text field

        fileNameTF.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("")){
                isNameSelected.set(true);
                if (isPathSelected.get())
                    areAllDetailsFilled.set(true);
            }
            else {
                isNameSelected.set(false);
                areAllDetailsFilled.set(false);
            }
        });

        directoryTF.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("")){ // Only if no path was chosen, do not set true on path selected.
                isPathSelected.set(true);
                if (isNameSelected.get())
                    areAllDetailsFilled.set(true);
            }
            else {
                isPathSelected.set(false);
                areAllDetailsFilled.set(false);
            }
        });

        showGraphvizBtn.setDisable(true);
    }

    public void setAdminAppController(AdminAppController adminAppController) {
        this.adminAppController = adminAppController;
    }

    @FXML void chooseDirectoryBtnAction() {
        DirectoryChooser fc = new DirectoryChooser(); // Directory chooser - where the user should save the graphviz file
        File selectedFile = fc.showDialog(null);

        if (selectedFile != null) {
            String absolutePath = selectedFile.getAbsolutePath();
            isPathSelected.set(true);
            directoryPath.set(absolutePath);
        }
        else{
            setMessage(ILLEGAL_PATH);
        }
    }

    @FXML
    void saveBtnAction() {
        createGraphviz(directoryPath.get(), fileNameTF.getText());
        setMessage(SUCCESS_MSG);
        areAllDetailsFilled.set(false); // For smooth feeling, after you press save it becomes disabled.
        textFileLB.setText(directoryPath.get() + "\\" + fileNameTF.getText() + ".viz");
        imageFileLB.setText(directoryPath.get() + "\\" + fileNameTF.getText() + ".png");
        showGraphvizBtn.setDisable(false);
    }

    @FXML
    void showGraphvizBtnAction(ActionEvent event) {
        File file = new File(imageFileLB.getText());
        Image image = new Image(file.toURI().toString());
        imagePlaceholder.setImage(image);
    }

    public void clear(){ // Refresh
        setMessage("");
        areAllDetailsFilled.set(false);
        directoryPath.set("");
        fileNameTF.setText("");
        showGraphvizBtn.disableProperty().set(true);
        textFileLB.setText("");
        imageFileLB.setText("");
        imagePlaceholder.setImage(null);
    }

    private void createGraphviz(String directoryPath, String fileName) {
        try {
            String finalUrl = HttpUrl
                    .parse(Constants.CREATE_GRAPHVIZ)
                    .newBuilder()
                    .addQueryParameter("graphName", adminAppController.getChosenTableGraph().getGraphName())
                    .addQueryParameter("directoryPath", directoryPath)
                    .addQueryParameter("fileName", fileName)
                    .build()
                    .toString();

            Response response = HttpClientUtil.runSync(finalUrl);
            String responseBody = response.body().string();
            if (response.code() != 200) {

                Platform.runLater(() ->
                        setMessage("Something went wrong: " + responseBody)
                );
            }
        } catch (Exception e) {
            setMessage(e.getMessage());
        }
    }

    private void setMessage(String msg){
        Platform.runLater(()->{
            msgLbl.setText(msg);
        });
    }




}

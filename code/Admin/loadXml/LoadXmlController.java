package loadXml;

import Util.Constants;
import configuration.HttpConfig;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import okhttp3.*;
import java.io.File;

public class LoadXmlController {
    private File selectedFile;

    @FXML private TextField fileTextField;
    @FXML private TextArea loadXMLStatusMsg;
    @FXML private Button loadBtn;

    private static final String LOADING_SUCCESS = "Loaded XML Successfully";
    private static final String NO_PATH_SELECTED = "No legal route was selected";

    private final SimpleStringProperty statusMsgProperty;
    private final SimpleStringProperty fileTextFieldProperty;
    private final SimpleBooleanProperty isFileSelected;
    private final SimpleBooleanProperty isFileLoaded;


    public LoadXmlController(){
        statusMsgProperty = new SimpleStringProperty();
        fileTextFieldProperty = new SimpleStringProperty();
        isFileSelected = new SimpleBooleanProperty(false);
        isFileLoaded = new SimpleBooleanProperty(false);
    }

    @FXML
    public void initialize() {
        loadXMLStatusMsg.textProperty().bind(statusMsgProperty);
        fileTextField.textProperty().bind(fileTextFieldProperty); // Update file text field when file is selected using the file chooser dialog
        loadBtn.disableProperty().bind(isFileSelected.not()); // Only if a file was loaded, enable load button
        fileTextField.disableProperty().bind(isFileSelected.not());
    }
    @FXML
    void loadBtnAction(ActionEvent event) {
        String RESOURCE = "/gpup/upload-file";
        RequestBody body =
                new MultipartBody.Builder()
                        .addFormDataPart("file", selectedFile.getName(), RequestBody.create(selectedFile, MediaType.parse("text/plain")))
                        .build();
        Request request = new Request.Builder().url(Constants.BASE_URL + RESOURCE)
                .post(body)
                .build();

        Call call = HttpConfig.HTTP_CLIENT.newCall(request);
        try {
            Response response = call.execute();
            statusMsgProperty.set(LOADING_SUCCESS);
            isFileLoaded.set(true);
        }
        catch(Exception ignored){
            statusMsgProperty.set("error in call");
        }
    }

    @FXML
    void loadXMLBtn(ActionEvent event) {

        clear();
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter( "XML files","*.xml" )); // We want XML files only
        selectedFile = fc.showOpenDialog(null);

        if (selectedFile != null) {
            String absolutePath = selectedFile.getAbsolutePath();
            isFileSelected.set(true);
            fileTextFieldProperty.set(absolutePath); // Show the path in the text field
        }
        else {
            statusMsgProperty.set(NO_PATH_SELECTED);
        }
    }

    public void clear(){
        statusMsgProperty.set("");
        fileTextFieldProperty.set("");
    }

    public void initPage(){
        clear();
        isFileSelected.set(false);
        isFileLoaded.set(false);
    }

}

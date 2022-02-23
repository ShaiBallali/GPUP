package taskdetailsfill.simulationdetailsfill;
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
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import main.AdminAppController;
import okhttp3.HttpUrl;
import okhttp3.Response;

public class SimulationDetailsFillController {

    @FXML private TextField executionNameTF;
    @FXML private TextField processingTimeTF;
    @FXML private TextField successProbTF;
    @FXML private TextField successWarningProbTF;
    @FXML private RadioButton isRandomRB;
    @FXML private Slider successSlider;
    @FXML private Slider successWarningSlider;
    @FXML private Button executeBtn;
    @FXML private Label msgLbl;

    private AdminAppController adminAppController;

    private final SimpleBooleanProperty isRandomProperty;
    private final SimpleBooleanProperty areAllDetailsFilled;
    private final SimpleBooleanProperty isExecutionNameSelected;
    private final SimpleBooleanProperty isProcessingTimeSelected;
    private final SimpleStringProperty  msgProperty;

    private int targetProcessingTime;
    private float successRate;
    private float warningRate;
    private boolean isRandom;
    private String executionName;
    private String currGraphName;
    private static final String ILLEGAL_INPUT = "You did not enter a number, please enter a number";



    public SimulationDetailsFillController(){ // Details fill page for simulation
        areAllDetailsFilled = new SimpleBooleanProperty(false);
        isRandomProperty = new SimpleBooleanProperty(false);
        isExecutionNameSelected = new SimpleBooleanProperty(false);
        isProcessingTimeSelected = new SimpleBooleanProperty(false);
        msgProperty = new SimpleStringProperty();
    }

    @FXML private void initialize(){
        processingTimeTF.setText("0");
        successProbTF.setText("0");
        successWarningProbTF.setText("0");
        msgProperty.set("");

        isRandomRB.selectedProperty().addListener((observable, oldValue, newValue) -> {
            isRandomProperty.set(newValue);
        });

        /*---------------*/

        successSlider.setMax(1);
        successWarningSlider.setMax(1);

        executeBtn.disableProperty().bind(areAllDetailsFilled.not());
        msgLbl.textProperty().bind(msgProperty);

        /*--------------*/

        msgProperty.addListener(new ChangeListener<String>() {  // Success with warning text field listens to corresponding slider
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!(newValue.equals(""))) // If there is an error message
                    areAllDetailsFilled.set(false);
            }
        });

        executionNameTF.textProperty().addListener(new ChangeListener<String>() { // Processing time text field listens to corresponding slider
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {

                if (!newValue.equals("")) {
                    isExecutionNameSelected.set(true);
                    msgProperty.set(""); // Clear error message
                    if (isProcessingTimeSelected.get())
                        areAllDetailsFilled.set(true);
                }
                else {
                    isExecutionNameSelected.set(false);
                    areAllDetailsFilled.set(false);
                }

            }
        });

        processingTimeTF.textProperty().addListener(new ChangeListener<String>() { // Processing time text field listens to corresponding slider
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {

                if (!(newValue.matches("\\d*"))) { // numeric values only
                    processingTimeTF.setText(newValue.replaceAll("[^\\d]", ""));
                    msgProperty.set("Please enter an integer");
                }
                else if (!(newValue.equals ("0") || newValue.equals(""))) {

                    isProcessingTimeSelected.set(true);
                    msgProperty.set(""); // Clear error message
                    if (isExecutionNameSelected.get())
                        areAllDetailsFilled.set(true);

                }
                else {
                    isProcessingTimeSelected.set(false);
                    areAllDetailsFilled.set(false);
                }

            }
        });

        successProbTF.textProperty().addListener(new ChangeListener<String>() { // Success probability text field listens to corresponding slider
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {

                if (!(newValue.equals(""))) {
                    try {
                        successSlider.setValue(Double.parseDouble(newValue)); // Slider gets double values..
                        msgProperty.set(""); // Clear error message
                        successSlider.disableProperty().set(false);
                    }
                    catch (NumberFormatException e){
                        msgProperty.set(ILLEGAL_INPUT);
                        successProbTF.setText("");
                        successSlider.disableProperty().set(true);
                    }
                    if ((!successProbTF.getText().equals(""))&&Double.parseDouble(newValue)>1 )  // Not letting the user enter a number bigger than 1
                        successProbTF.setText(String.valueOf(1));

                }
            }
        });

        successWarningProbTF.textProperty().addListener(new ChangeListener<String>() { // // Success with warning text field listens to corresponding slider
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {

                    if (!(newValue.equals(""))) {

                        try {
                            successWarningSlider.setValue(Double.parseDouble(newValue)); // If the given value is not a number, NumberFormatException will be thrown
                            msgProperty.set("");
                            successWarningSlider.disableProperty().set(false);

                        } catch (NumberFormatException e) {
                            msgProperty.set(ILLEGAL_INPUT);
                            successWarningProbTF.setText(""); // Not showing the illegal char
                            successWarningSlider.disableProperty().set(true); // Disabling slider so user will not start dragging slider with a letter
                        }
                            if ((!successWarningProbTF.getText().equals("")) && Double.parseDouble(newValue) > 1)  // Not letting the user enter a number bigger than 1
                                successWarningProbTF.setText(String.valueOf(1));
                    }

            }
        });

        successSlider.valueProperty().addListener( // text field listens to corresponding slider
                new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                        if (successProbTF.getText().equals("") || Double.parseDouble(successProbTF.getText()) != successSlider.getValue()) {
                            successProbTF.setText(String.valueOf(successSlider.getValue()));
                        }

                    }
                });

        successWarningSlider.valueProperty().addListener( // text field listens to corresponding slider
                new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                            if (successWarningProbTF.getText().equals("") || Double.parseDouble(successWarningProbTF.getText()) != successWarningSlider.getValue()) {
                                successWarningProbTF.setText(String.valueOf(successWarningSlider.getValue()));
                            }
                    }
                });


    }

    @FXML void executeBtnAction(ActionEvent event) {

        targetProcessingTime = Integer.parseInt(processingTimeTF.getText()); // Converting the text in text field to integer
        successRate = Float.parseFloat(successProbTF.textProperty().getValue());
        warningRate = Float.parseFloat(successWarningProbTF.textProperty().getValue());
        isRandom = isRandomProperty.getValue();
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
                        NewExecutionDetails currExecution = new NewExecutionDetails(executionName, currGraphName, TaskName.SIMULATION);
                        currExecution.initSimulationDetails(targetProcessingTime, isRandom, successRate, warningRate);
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


    @FXML
    void successTFMouseClick(MouseEvent event) {
        if(!(msgProperty.getValue().equals(""))) {
            successProbTF.setText("0");
            msgProperty.set("");
        }
    }

    @FXML
    void successWarningTFMouseClick(MouseEvent event){
        if(!(msgProperty.getValue().equals(""))) {
            successWarningProbTF.setText("0");
            msgProperty.set("");
        }
    }

    public void setAdminAppController(AdminAppController adminAppController) {
        this.adminAppController = adminAppController;
    }


    public void clear(){ // Refreshing page
        processingTimeTF.setText("0");
        isRandomRB.setSelected(false);
        successProbTF.setText("0");
        successWarningProbTF.setText("0");
        areAllDetailsFilled.set(false);
        executionNameTF.setText("");
    }


}

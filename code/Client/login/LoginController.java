package login;

import Util.Constants;
import Util.http.HttpClientUtil;
import engine.ClientEngine;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import main.ClientAppController;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import dto.enums.UserType;
import java.io.IOException;

public class LoginController {

    private ClientAppController clientAppController;
    private final SimpleBooleanProperty isUsernameEntered;
    private final SimpleBooleanProperty areAllDetailsFilled;

    private final int MAX_PARALLELISM = 5;

    @FXML public TextField userNameTextField;
    @FXML public Label errorMessageLabel;
    @FXML public Slider numOfThreadsSlider;
    @FXML public TextField numOfThreadsTF;

    private final StringProperty errorMessageProperty;

    public LoginController(){
        isUsernameEntered = new SimpleBooleanProperty(false);
        areAllDetailsFilled = new SimpleBooleanProperty(false);
        errorMessageProperty = new SimpleStringProperty();
    }

    @FXML
    public void initialize() {
        errorMessageLabel.textProperty().bind(errorMessageProperty);
        numOfThreadsTF.setText("1");
        numOfThreadsSlider.setMin(1);
        numOfThreadsSlider.setMax(MAX_PARALLELISM);

        numOfThreadsTF.textProperty().addListener(new ChangeListener<String>() { // Num of threads text field listens to corresponding slider
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!(newValue.matches("\\d*"))) { // numeric values only
                    numOfThreadsTF.setText(newValue.replaceAll("[^\\d]", ""));
                }
                else if (!(newValue.equals("") ||newValue.equals("0") )) {

                    if (Double.parseDouble(numOfThreadsTF.getText()) != numOfThreadsSlider.getValue()) {

                        numOfThreadsSlider.setValue(Double.parseDouble(numOfThreadsTF.getText()));
                        if (isUsernameEntered.get())
                            areAllDetailsFilled.set(true);

                    }
                    if (Integer.parseInt(newValue)>MAX_PARALLELISM)  // Not letting the user enter a number bigger than MaxParallelism
                        numOfThreadsTF.setText(String.valueOf(MAX_PARALLELISM));
                }
                else {
                    areAllDetailsFilled.set(false);
                }

            }
        });

        numOfThreadsSlider.valueProperty().addListener( // text field listens to corresponding slider
                new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                        if (numOfThreadsTF.getText().equals("") || Integer.parseInt(numOfThreadsTF.getText()) != (int) numOfThreadsSlider.getValue()) {
                            numOfThreadsTF.setText(String.valueOf((int) numOfThreadsSlider.getValue()));
                        }

                    }
                });

        userNameTextField.textProperty().addListener(new ChangeListener<String>() { // // Success with warning text field listens to corresponding slider
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!(newValue.equals(""))) // If username text field is not empty
                    isUsernameEntered.set(true);
            }
        });

    }




    @FXML
    private void loginButtonClicked(ActionEvent event) {

        String userName = userNameTextField.getText();
        if (userName.isEmpty()) {
            errorMessageProperty.set("User name is empty. You can't login with empty user name");
            return;
        }

        //noinspection ConstantConditions
        String finalUrl = HttpUrl
                .parse(Constants.LOGIN_PAGE)
                .newBuilder()
                .addQueryParameter("username", userName)
                .addQueryParameter("userType", UserType.WORKER.toString())
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        errorMessageProperty.set("Something went wrong: " + e.getMessage())
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() ->
                            errorMessageProperty.set("Something went wrong: " + responseBody)
                    );
                } else {
                    Platform.runLater(() -> {
                        errorMessageProperty.set("Logged in successfully.");
                        clientAppController.setCurrUsername(userName);
                        clientAppController.setCurrNumOfThreads(Integer.parseInt(numOfThreadsTF.getText()));
                        ClientEngine clientEngine = new ClientEngine(userName,Integer.parseInt(numOfThreadsTF.getText()));
                        clientAppController.setClientEngine(clientEngine);
                        clientAppController.setDashboardPage();

                    });
                }
            }
        });
    }

    @FXML
    private void userNameKeyTyped(KeyEvent event) {
        errorMessageProperty.set("");
    }

    @FXML
    private void quitButtonClicked(ActionEvent e) {
        Platform.exit();
    }

    public void setClientAppController(ClientAppController clientAppController){
        this.clientAppController = clientAppController;
    }

}


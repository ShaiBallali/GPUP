package login;

import Util.Constants;
import Util.http.HttpClientUtil;
import dto.enums.UserType;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import main.AdminAppController;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;

public class LoginController {

    private AdminAppController adminAppController;
    private final SimpleBooleanProperty isUsernameEntered;


    @FXML public TextField userNameTextField;
    @FXML public Label errorMessageLabel;

    private final StringProperty errorMessageProperty;

    public LoginController(){
        isUsernameEntered = new SimpleBooleanProperty(false);
        errorMessageProperty = new SimpleStringProperty();
    }

    @FXML
    public void initialize() {
        errorMessageLabel.textProperty().bind(errorMessageProperty);

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
                .addQueryParameter("userType", UserType.ADMIN.toString())
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
                        adminAppController.updateUsername(userName);
                        adminAppController.setDashboardPage();

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

    public void setAdminAppController(AdminAppController adminAppController){
        this.adminAppController = adminAppController;
    }

}


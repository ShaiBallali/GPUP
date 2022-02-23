package taskdatatable;

import Util.Constants;
import Util.http.HttpClientUtil;
import com.google.gson.stream.JsonReader;
import configuration.GsonConfig;
import configuration.HttpConfig;
import dto.dtoServer.execution.NewExecutionDetails;
import dto.graph.TargetDetailsJavaFX;
import dto.dtoServer.graphAction.WhatIfDetails;
import dto.enums.DependencyType;
import graphs.TableGraph;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import main.AdminAppController;
import okhttp3.*;
import java.io.StringReader;
import java.util.*;

public class TaskDataTableController {

    private final Set<TargetDetailsJavaFX> targetDetailsSet;
    private final Set<String> selectedTargetsNames;
    private AdminAppController adminAppController;
    private String responseBody;

    @FXML private TableView<TargetDetailsJavaFX> datatableView;
    @FXML private TableColumn<TargetDetailsJavaFX, String> selectColumn;
    @FXML private TableColumn<TargetDetailsJavaFX, String> nameColumn;
    @FXML private TableColumn<TargetDetailsJavaFX, String> positionColumn;
    @FXML private TableColumn<TargetDetailsJavaFX, String> generalInfoColumn;
    @FXML private TableColumn<TargetDetailsJavaFX, Integer> directDependsOnColumn;
    @FXML private TableColumn<TargetDetailsJavaFX, Integer> allDependsOnColumn;
    @FXML private TableColumn<TargetDetailsJavaFX, Integer> directRequiredForColumn;
    @FXML private TableColumn<TargetDetailsJavaFX, Integer> allRequiredForColumn;

    @FXML private CheckBox selectAllBtn;
    @FXML private ComboBox dependencyCB;
    @FXML private Button clearSelectionBtn;
    @FXML private Button whatIfBtn;
    @FXML private Button nextBtn;
    @FXML private Label msgLbl;

    private final ObservableSet<CheckBox> selectedTargetCheckBoxes = FXCollections.observableSet();
    private final ObservableSet<CheckBox> unselectedTargetCheckBoxes = FXCollections.observableSet();
    private final ArrayList<String> dependency;

    private final SimpleBooleanProperty isOneTargetSelected;
    private final SimpleBooleanProperty isAnyTargetSelected;
    private final SimpleBooleanProperty isDependencySelected;
    private final SimpleStringProperty msgProperty;

    public TaskDataTableController() {
        targetDetailsSet = new HashSet<>();
        selectedTargetsNames = new HashSet<>();
        dependency = new ArrayList<>();
        isOneTargetSelected = new SimpleBooleanProperty(false);
        isAnyTargetSelected = new SimpleBooleanProperty(false);
        isDependencySelected = new SimpleBooleanProperty(false);
        msgProperty = new SimpleStringProperty("");
    }


    @FXML
    public void initialize() {

        // set up the columns in data table

        selectColumn.setCellValueFactory(new PropertyValueFactory<TargetDetailsJavaFX, String>("remark"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<TargetDetailsJavaFX, String>("name"));
        positionColumn.setCellValueFactory(new PropertyValueFactory<TargetDetailsJavaFX, String>("position"));
        generalInfoColumn.setCellValueFactory(new PropertyValueFactory<TargetDetailsJavaFX, String>("generalInfo"));
        directDependsOnColumn.setCellValueFactory(new PropertyValueFactory<TargetDetailsJavaFX, Integer>("directDependsOn"));
        allDependsOnColumn.setCellValueFactory(new PropertyValueFactory<TargetDetailsJavaFX, Integer>("allDependsOn"));
        directRequiredForColumn.setCellValueFactory(new PropertyValueFactory<TargetDetailsJavaFX, Integer>("directRequiredFor"));
        allRequiredForColumn.setCellValueFactory(new PropertyValueFactory<TargetDetailsJavaFX, Integer>("allRequiredFor"));
        /*----------*/


        selectAllBtn.selectedProperty().addListener((observable, oldValue, newValue) -> {

            if (selectAllBtn.isSelected()) {
                for (TargetDetailsJavaFX targetDetails : targetDetailsSet) {
                    targetDetails.getRemark().setSelected(true);
                }
            }
            else {
                for (TargetDetailsJavaFX targetDetails : targetDetailsSet) {
                    targetDetails.getRemark().setSelected(false);
                }
            }
        });


        /*----------*/

        dependency.add(DependencyType.REQUIRED_FOR.toString());
        dependency.add(DependencyType.DEPENDS_ON.toString());
        dependencyCB.setItems(FXCollections.observableArrayList(dependency));

        /*----------*/


    }
    @FXML
    void whatIfBtnAction(ActionEvent event){
        if (dependencyCB.buttonCellProperty()!=null) {
            for (TargetDetailsJavaFX targetDetails : datatableView.getItems())
                if (targetDetails.getRemark().isSelected()) {
                    try {
                        String finalUrl = HttpUrl
                                .parse(Constants.WHAT_IF)
                                .newBuilder()
                                .addQueryParameter("graphName", adminAppController.getChosenTableGraph().getGraphName())
                                .addQueryParameter("targetName", targetDetails.getName())
                                .addQueryParameter("dependencyType", dependencyCB.getValue().toString())
                                .build()
                                .toString();

                        Response response = HttpClientUtil.runSync(finalUrl);
                        responseBody = response.body().string();
                        if (response.code() != 200) {

                            Platform.runLater(() ->
                                    msgProperty.set("Something went wrong: " + responseBody)
                            );
                        } else {
                            Platform.runLater(() -> {
                                try {
                                    JsonReader reader = new JsonReader(new StringReader(responseBody));
                                    reader.setLenient(true);
                                    WhatIfDetails whatIfDetails = Constants.GSON_INSTANCE.fromJson(reader, WhatIfDetails.class);
                                    Set<String> whatIfTargets = whatIfDetails.getPath();
                                    for (TargetDetailsJavaFX targetDetails2 : datatableView.getItems()) {
                                        if (whatIfTargets.contains(targetDetails2.getName()))
                                            targetDetails2.getRemark().setSelected(true);
                                    }
                                } catch (Exception e) {
                                    msgProperty.set("Something went wrong: " + e.getMessage());
                                }

                            });
                        }

                    } catch (Exception e) {
                        msgProperty.set(e.getMessage());
                    }
                }
                    dependencyCB.getSelectionModel().clearSelection();
                    isDependencySelected.set(false);
                }
        }


    @FXML void nextBtnAction(ActionEvent event) {

        for (TargetDetailsJavaFX targetDetails : targetDetailsSet){
            if (targetDetails.getRemark().isSelected())selectedTargetsNames.add(targetDetails.getName());
            else
                selectedTargetsNames.remove(targetDetails.getName());
        }
        adminAppController.getCurrExecution().initTargetsToPerform(selectedTargetsNames);
        sendCurrExecutionToServer(adminAppController.getCurrExecution());
        adminAppController.setDashboardPage();
    }

    @FXML void clearSelectionBtnAction() {
        dependencyCB.getSelectionModel().clearSelection();
        selectAllBtn.setSelected(false);
        isDependencySelected.set(false);
        for (TargetDetailsJavaFX targetDetails : targetDetailsSet) {
            targetDetails.getRemark().setSelected(false);
        }
        isOneTargetSelected.set(false);
        isAnyTargetSelected.set(false);
    }

    @FXML void dependencyCBAction(ActionEvent event) {
        if (dependencyCB.buttonCellProperty()!=null){
            isDependencySelected.set(true);
        }
    }


    public void setAllTargetsDetails(TableGraph chosenTableGraph) {
        setDataTableView(chosenTableGraph.getTargetDetailsTable());

        ObservableList<TargetDetailsJavaFX> data = datatableView.getItems();

        for(TargetDetailsJavaFX targetDetails : data) {
            configureCheckBox(targetDetails.getRemark());
        }
    }

    public void sendCurrExecutionToServer(NewExecutionDetails currExecution){
        String newExecutionDetailsGson = GsonConfig.gson.toJson(currExecution);

        String body = "executionDetails=" + newExecutionDetailsGson;

        Request request = new Request.Builder().url(Constants.CREATE_EXECUTION)
                .post(RequestBody.create(body.getBytes()))
                .build();

        Call call = HttpConfig.HTTP_CLIENT.newCall(request);
        call.enqueue(HttpConfig.SIMPLE_CALLBACK);
    }


    public void setAdminAppController(AdminAppController adminAppController){
        this.adminAppController = adminAppController;
    }

    private void setDataTableView(Set<TargetDetailsJavaFX> targetsDetails) {
        datatableView.getItems().clear();
        datatableView.setItems(getDataTableItems(targetsDetails));
        this.targetDetailsSet.clear();
        this.targetDetailsSet.addAll(targetsDetails);
    }

    private ObservableList<TargetDetailsJavaFX> getDataTableItems(Set<TargetDetailsJavaFX> targetsDetails) {
        return FXCollections.observableArrayList(targetsDetails);
    }

    private void configureCheckBox(CheckBox checkBox) { // Function distinguishes between selected checkboxes and unselected ones

        if (checkBox.isSelected()) {
            selectedTargetCheckBoxes.add(checkBox);

        } else {
            unselectedTargetCheckBoxes.add(checkBox);
        }


        checkBox.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            if (isNowSelected) {
                unselectedTargetCheckBoxes.remove(checkBox);
                selectedTargetCheckBoxes.add(checkBox);
            } else {
                selectedTargetCheckBoxes.remove(checkBox);
                unselectedTargetCheckBoxes.add(checkBox);
            }

            isOneTargetSelected.set(selectedTargetCheckBoxes.size() == 1);
            isAnyTargetSelected.set(selectedTargetCheckBoxes.size() >= 1);

        });

    }

    public void onStart(){
        dependencyCB.disableProperty().bind(isOneTargetSelected.not()); // What if dependency should be shown only if exactly one target was selected
        clearSelectionBtn.disableProperty().bind(isAnyTargetSelected.not()); // Clear selection button should be shown only if a target was chosen
        whatIfBtn.disableProperty().bind(isDependencySelected.not()); // What if button should be shown only after a dependency was selected
        nextBtn.disableProperty().bind(isAnyTargetSelected.not()); // Next button should be enabled only after all details were complete
        msgLbl.textProperty().bind(msgProperty);
    }

    public void clear(){
        clearSelectionBtnAction();
    }
}





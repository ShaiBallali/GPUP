package datatable;
import dto.graph.TargetDetailsJavaFX;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.List;

public class DataTableController {

    @FXML private TableView<TargetDetailsJavaFX> datatableView;
    @FXML private TableColumn<TargetDetailsJavaFX, String> nameColumn;
    @FXML private TableColumn<TargetDetailsJavaFX, String> positionColumn;
    @FXML private TableColumn<TargetDetailsJavaFX, String> generalInfoColumn;
    @FXML private TableColumn<TargetDetailsJavaFX, Integer> directDependsOnColumn;
    @FXML private TableColumn<TargetDetailsJavaFX, Integer> allDependsOnColumn;
    @FXML private TableColumn<TargetDetailsJavaFX, Integer> directRequiredForColumn;
    @FXML private TableColumn<TargetDetailsJavaFX, Integer> allRequiredForColumn;

    @FXML
    public void initialize() {
        // set up the columns in data table
        nameColumn.setCellValueFactory(new PropertyValueFactory<TargetDetailsJavaFX, String>("name"));
        positionColumn.setCellValueFactory(new PropertyValueFactory<TargetDetailsJavaFX, String>("position"));
        generalInfoColumn.setCellValueFactory(new PropertyValueFactory<TargetDetailsJavaFX, String>("generalInfo"));
        directDependsOnColumn.setCellValueFactory(new PropertyValueFactory<TargetDetailsJavaFX, Integer>("directDependsOn"));
        allDependsOnColumn.setCellValueFactory(new PropertyValueFactory<TargetDetailsJavaFX, Integer>("allDependsOn"));
        directRequiredForColumn.setCellValueFactory(new PropertyValueFactory<TargetDetailsJavaFX, Integer>("directRequiredFor"));
        allRequiredForColumn.setCellValueFactory(new PropertyValueFactory<TargetDetailsJavaFX, Integer>("allRequiredFor"));
    }


    // Setting table items
    public void setAllTargetsDetails (List<TargetDetailsJavaFX> allTargetsDetails) {
        setDataTableView(allTargetsDetails);
    }

    private void setDataTableView(List<TargetDetailsJavaFX> targetDetails){
        datatableView.setItems(FXCollections.observableArrayList(targetDetails));
    }

}

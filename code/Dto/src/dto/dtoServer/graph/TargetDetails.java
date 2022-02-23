package dto.dtoServer.graph;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.CheckBox;

public class TargetDetails {
    private String  name, position,  generalInfo ;
    private int allDependsOn, directDependsOn, directRequiredFor, allRequiredFor,numOfSerialSets;

    public TargetDetails(String name, String position, String generalInfo, int allDependsOn, int directDependsOn, int allRequiredFor, int directRequiredFor, int numOfSerialSets) {
        this.name = name;
        this.position = position;
        this.generalInfo = generalInfo;
        this.allDependsOn = allDependsOn;
        this.directDependsOn = directDependsOn;
        this.directRequiredFor = directRequiredFor;
        this.allRequiredFor = allRequiredFor;
        this.numOfSerialSets = numOfSerialSets;
    }

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    public String getGeneralInfo() {
        return generalInfo;
    }

    public int getAllDependsOn() {
        return allDependsOn;
    }

    public int getDirectDependsOn() {
        return directDependsOn;
    }

    public int getDirectRequiredFor() {
        return directRequiredFor;
    }

    public int getAllRequiredFor() {
        return allRequiredFor;
    }

    public int getNumOfSerialSets() {
        return numOfSerialSets;
    }
}


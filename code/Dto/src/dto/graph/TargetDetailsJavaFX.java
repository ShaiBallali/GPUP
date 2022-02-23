package dto.graph;

import javafx.beans.property.SimpleIntegerProperty;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.CheckBox;

import java.util.Objects;

public class TargetDetailsJavaFX {
    private SimpleStringProperty name, position,  generalInfo,previousRunStatus ;
    private SimpleIntegerProperty allDependsOn, directDependsOn, directRequiredFor, allRequiredFor;
    private CheckBox remark;

    public TargetDetailsJavaFX(String name, String location, String generalInfo,
                               Integer allDependsOn, Integer directDependsOn, Integer directRequiredFor, Integer allRequiredFor)
    {
        this.name = new SimpleStringProperty(name);
        this.position = new SimpleStringProperty(location);
        this.generalInfo = new SimpleStringProperty(generalInfo);
        this.directDependsOn = new SimpleIntegerProperty(allDependsOn);
        this.allDependsOn = new SimpleIntegerProperty(directDependsOn);
        this.directRequiredFor = new SimpleIntegerProperty(directRequiredFor);
        this.allRequiredFor = new SimpleIntegerProperty(allRequiredFor);
        this.remark = new CheckBox();
    }
    public String getName()
    {
        return name.get();
    }
    public String getPosition()
    {
        return position.get();
    }
    public String getGeneralInfo()
    {
        return generalInfo.get();
    }
    public Integer getDirectDependsOn()
    {
        return directDependsOn.get();
    }
    public Integer getAllDependsOn()
    {
        return allDependsOn.get();
    }
    public Integer getDirectRequiredFor()
    {
        return directRequiredFor.get();
    }
    public Integer getAllRequiredFor()
    {
        return allRequiredFor.get();
    }
    public String getPreviousRunStatus() { return previousRunStatus.get();}
    public CheckBox getRemark(){return remark;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TargetDetailsJavaFX that = (TargetDetailsJavaFX) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}


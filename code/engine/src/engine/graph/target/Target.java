package engine.graph.target;

import dto.enums.RunResult;
import dto.enums.TargetState;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Target {

    private String      name;
    private String      generalInfo;
    private Set<String> dependsOn;
    private Set<String> requiredFor;
    private TargetState       state;               // FROZEN | SKIPPED | WAITING | IN_PROCESS | FINISHED
    private RunResult   runResult;                 // SUCCESS | WARNING | FAILURE | SKIPPED
    private Set <String> mySerialSet;

    // ---------------------------------------------------------- //

    public Target (String name, String generalInfo) {
        this.name          = name;
        this.generalInfo   = generalInfo;
        requiredFor        = new HashSet<>();
        dependsOn          = new HashSet<>();
        this.mySerialSet   = new HashSet<>();

        reset();
    }

    public Set<String> getMySerialSets() {return mySerialSet;}

    public void         setDependsOn(Set<String> dependsOn) {
        this.dependsOn.addAll(dependsOn);
    }

    public void         setRequiredFor(Set<String> requiredFor) {
        this.requiredFor.addAll(requiredFor);
    }

    public String       getName () {
        return name;
    }

    public int          getNumOfDirectDependOn() {
        return dependsOn.size();
    }

    public int          getNumOfDirectRequiredFor() {
        return requiredFor.size();
    }

    public int          getNumOfSerialSets ()
    {
        return this.mySerialSet.size();
    }

    public void         setState  (TargetState state) {
        this.state = state;
    }

    public Set<String>  getRequiredFor () {
        return requiredFor;
    }

    public Set<String>  getDependsOn () {
        return dependsOn;
    }

    public void         addToRequiredFor (String targetName) {
        requiredFor.add(targetName);
    }

    public void         addToDependsOn (String targetName) {
        dependsOn.add(targetName);
    }

    public void         addToSerialSets(String serialSetsName) { mySerialSet.add(serialSetsName); }

    public void         setRunResult (RunResult runResult) {
        this.runResult = runResult;
    }

    public RunResult    getRunResult () {
        return runResult;
    }

    public String       getGeneralInfo () {
        return generalInfo != null ? generalInfo : "";

    }

    public void         reset () {
        this.state = TargetState.FROZEN; // Initial state is frozen
        this.runResult = RunResult.WITHOUT;
    }

    public TargetState getTargetState ( ) {
        return this.state;
    }

    @Override
    public boolean     equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Target)) return false;
        Target target = (Target) o;
        return Objects.equals(name, target.name);
    }

    @Override
    public int         hashCode() {
        return Objects.hash(name);
    }
}

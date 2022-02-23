package engine.graph;

import dto.enums.TargetState;

import java.util.HashSet;
import java.util.Set;

public class SerialSet {

    private String name;
    private boolean isBlocked;
    private Set <String> targets;
    private Set<String> waitingTargets;

    public SerialSet (String name, Set <String> targets) {
        this.name = name;
        this.targets = new HashSet<>();
        this.waitingTargets = new HashSet<>();
        this.targets.addAll(targets);
        this.isBlocked = false;
    }

    public void addToWaitingTargets (String targetName) {
        waitingTargets.add(targetName);
    }

    public void deleteFromWaitingTargets (String targetName) {
        this.waitingTargets.remove(targetName);
    }

    public Set<String> getWaitingTargets () {
        return this.waitingTargets;
    }

    public Set<String> getTargets () {
        return this.targets;
    }

    public boolean isBlocked () {return isBlocked;}

    public void setBlocked (boolean isBlocked) {this.isBlocked = isBlocked;}
}

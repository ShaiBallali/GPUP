package engine.graph;

import dto.enums.Position;
import dto.enums.RunResult;
import dto.enums.RunType;
import dto.enums.TargetState;
import engine.graph.target.SubTarget;
import engine.graph.target.Target;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SubGraph {
    public Graph originGraph;
    public Map<String, SubTarget> subGraphData;
    public Map<String , Set<String>> name2dependsOnBeforeChanges;
    public Map<String, Set<String>> name2dependsOn;
    public Map<String, Set<String>> name2RequiredFor;
    public Map<Position, Integer> positionCount;
    public int sumOfTargetsToPerform;
    public int sumOfTarget;

    public SubGraph (Graph originGraph) {
        this.originGraph = originGraph;
        this.subGraphData = new HashMap<>();
        this.name2dependsOnBeforeChanges = new HashMap<>();
        this.name2dependsOn = new HashMap<>();
        this.name2RequiredFor = new HashMap<>();
        this.sumOfTarget = 0;
        this.sumOfTargetsToPerform = 0;

        this.positionCount = new HashMap<>();
        positionCount.put(Position.ROOT, 0);
        positionCount.put(Position.LEAF, 0);
        positionCount.put(Position.INDEPENDENT, 0);
        positionCount.put(Position.MIDDLE, 0);
    }

    // subGraph
    public SubGraph (SubGraph copyFrom, RunType runType) {
        this.originGraph = copyFrom.originGraph;
        this.subGraphData = new HashMap<>();

        copyFrom.subGraphData.forEach((name, subTarget) -> {
            subGraphData.put(name, new SubTarget(subTarget));
        });

        this.name2dependsOnBeforeChanges = new HashMap<>();
        duplicateMap (copyFrom.name2dependsOnBeforeChanges, this.name2dependsOnBeforeChanges);

        this.name2RequiredFor = new HashMap<>();
        duplicateMap (copyFrom.name2RequiredFor, this.name2RequiredFor);

        this.name2RequiredFor = new HashMap<>(copyFrom.name2RequiredFor);
        this.positionCount = copyFrom.getPositionCount();
        this.sumOfTarget = copyFrom.sumOfTarget;
        this.sumOfTargetsToPerform = 0;

        this.name2dependsOn = new HashMap<>();

        switch (runType) {
            case INCREMENTAL:
                duplicateMap (copyFrom.name2dependsOn, this.name2dependsOn);
                resetSkippedAndFailureTarget();
                break;
            case FROM_SCRATCH:
                duplicateMap (this.name2dependsOnBeforeChanges, this.name2dependsOn);
                this.sumOfTargetsToPerform = this.sumOfTarget;
                resetAllTargets();
                break;
        }
    }

    private void duplicateMap (Map<String, Set<String>> src, Map<String, Set<String>> dst) {
        for (Map.Entry<String, Set<String>> entry : src.entrySet()) {
            dst.put(entry.getKey(), new HashSet<>(entry.getValue()));
        }
    }

    public void resetAllTargets () {
        this.subGraphData.forEach((name, subTarget) -> {
            subGraphData.get(name).reset();
        });
    }

    public void resetSkippedAndFailureTarget () {
        for (Map.Entry<String, Set<String>> mapEntry : name2dependsOn.entrySet()) {
            String name = mapEntry.getKey();
            if (subGraphData.get(name).getRunResult() == RunResult.SKIPPED || subGraphData.get(name).getRunResult() == RunResult.FAILURE) {
                subGraphData.get(name).reset();
                sumOfTargetsToPerform++;
            }
        }
    }

    public void initialSubGraph (Set<String> targetsToPerform) {
        Map<String, Set<String>> name2dependsOnOrigin = originGraph.name2dependsOn;

        for (Map.Entry<String, Set<String>> mapEntry : name2dependsOnOrigin.entrySet()) {
            String targetName = mapEntry.getKey();
            if (targetsToPerform.contains(targetName))
            {
                Set<String> dependsOnLocal = new HashSet<>(dependenciesThatExistInTargetsToPerform(targetsToPerform, mapEntry.getValue()));
                Set<String> requiredForLocal = new HashSet<>(dependenciesThatExistInTargetsToPerform(targetsToPerform, originGraph.getTargetRequiredFor(targetName)));


                this.name2dependsOnBeforeChanges.put(targetName, dependsOnLocal);
                this.name2dependsOn.put(targetName, new HashSet<>(dependsOnLocal));
                this.name2RequiredFor.put(targetName, requiredForLocal);

                Position targetPosition = this.originGraph.calcPositionOfTarget(dependsOnLocal.size(), requiredForLocal.size());

                int newAmount = positionCount.get(targetPosition) + 1;
                this.positionCount.replace(targetPosition, newAmount);

                this.subGraphData.put(targetName , new SubTarget(mapEntry.getKey(), targetPosition ,TargetState.FROZEN, RunResult.WITHOUT));
            }
        }
        this.sumOfTarget = this.name2dependsOn.size();
        this.sumOfTargetsToPerform = this.name2dependsOn.size();
    }

    public Set<String> dependenciesThatExistInTargetsToPerform(Set<String> targetsToPerform, Set<String> dependencies)  {
        Set <String> res = new HashSet<>();
        for (String dependency : dependencies) {
            if (targetsToPerform.contains(dependency)) {
                res.add(dependency);
            }
        }
        return res;
    }

    public int getSumOfTarget() {
        return this.sumOfTarget;
    }

    public int getSumOfTargetToPerform() {
        return this.sumOfTargetsToPerform;
    }

    public Map<String, Set<String>> getName2dependsOn () {
        return this.name2dependsOn;
    }

    public Target getOriginTarget(String targetName) {
        return this.originGraph.graphData.get(targetName);
    }

    public void setTargetState (String targetName, TargetState targetState) {
        this.subGraphData.get(targetName).setState(targetState);
    }

    public TargetState getTargetState (String targetName) {
        return this.subGraphData.get(targetName).getTargetState();
    }

    public Position getTargetPosition (String targetName) {
        return this.subGraphData.get(targetName).getPosition();
    }

    public void setTargetRunResult (String targetName, RunResult runResult) {
        this.subGraphData.get(targetName).setRunResult(runResult);
    }

    public RunResult getTargetRunResult (String targetName) {
        return this.subGraphData.get(targetName).getRunResult();
    }

    public Map<Position, Integer> getPositionCount() {
        return positionCount;
    }

    public void addLog (String targetName, String log) {
        this.subGraphData.get(targetName).addToLog(log);
    }

    public String getLogs (String targetName) {
        return this.subGraphData.get(targetName).getLogs();
    }
}

package jaxb.loadGraph;
import engine.graph.Graph;
import engine.graph.SerialSet;
import engine.graph.target.Target;
import jaxb.loadGraph.generated.GPUPConfiguration;
import jaxb.loadGraph.generated.GPUPDescriptor;
import jaxb.loadGraph.generated.GPUPTarget;
import jaxb.loadGraph.generated.GPUPTargetDependencies;

import java.io.InputStream;
import java.lang.Exception;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.util.*;

public class ConvertDescriptorToGraph {

    public final static String      JAXB_XML_GAME_PACKAGE_NAME = "jaxb.loadGraph.generated";
    public Map<String, Target>      graphData;
    public Map<String, SerialSet>   serialSets;
    public Map<String, Set<String>> name2dependsOn;

    private String                  graphName;
    public Map <String, Integer>     task2price;

    public ConvertDescriptorToGraph (InputStream inputStream) throws Exception{
        this.graphData      = new HashMap<>(); // Name -> target
        this.name2dependsOn = new HashMap<>(); // Name -> dependsOn
        this.serialSets     = new HashMap<>(); // set of serial sets
        this.task2price = new HashMap<>();

        //Deserialize from input stream
        GPUPDescriptor descriptor = deserializeFrom(inputStream);

        // Navigate through descriptor to get into other required properties.
        graphName = descriptor.getGPUPConfiguration().getGPUPGraphName().toLowerCase();

        List<GPUPConfiguration.GPUPPricing.GPUPTask> tasksPrices =  descriptor.getGPUPConfiguration().getGPUPPricing().getGPUPTask();

        for (GPUPConfiguration.GPUPPricing.GPUPTask taskPrice : tasksPrices ) {
            task2price.put(taskPrice.getName().toUpperCase(), taskPrice.getPricePerTarget());
        }

        List <GPUPTarget> descriptorTargets = descriptor.getGPUPTargets().getGPUPTarget();

        GPUPDescriptor.GPUPSerialSets serials = descriptor.getGPUPSerialSets();

        // Create actual targets from descriptorTargets
        createTargets(descriptorTargets);

        if (serials!= null) {
            List<GPUPDescriptor.GPUPSerialSets.GPUPSerialSet> descriptorSerialSets = serials.getGPUPSerialSet();

            // Create actual serial sets from descriptorSerialSets
            createSerialSets(descriptorSerialSets);
        }

        // Complete dependencies (sometimes we get only dependsOn/RequiredFor and need to complete the rest)
        dependencyCompletion();

        createName2dependsOn();

        // Check if A depends on B AND B depends on A (for example)
        checkDualDependencies();
    }

    private static GPUPDescriptor deserializeFrom(InputStream in) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(JAXB_XML_GAME_PACKAGE_NAME);
        Unmarshaller u = jc.createUnmarshaller();

        return (GPUPDescriptor) u.unmarshal(in);
    }

    public Graph getGraph () {
        return new Graph(graphName, graphData, name2dependsOn, serialSets, task2price);
    }

    private void createTargets(List <GPUPTarget> descriptorTargets) throws IllegalArgumentException {
        for (GPUPTarget descriptorTarget : descriptorTargets ) {

            Set <String> dependsOn     = new HashSet<>();
            Set <String> requiredFor   = new HashSet<>();

            // Using trim to be case-insensitive
            String name = descriptorTarget.getName().toUpperCase().trim();

            // We need to make sure the current target was not added into the graph yet, if it was - then current one is a duplicate
            if (graphData.get(name) != null)
                throw new IllegalArgumentException("error with xml file: duplicate target name - target with name " + name + " exist twice.");

            String generalInfo  = descriptorTarget.getGPUPUserData();

            Target newTarget = new Target(name, generalInfo);

            graphData.put(name,newTarget);

            // If the target has dependencies
            if (  descriptorTarget.getGPUPTargetDependencies() != null ) {
                List<GPUPTargetDependencies.GPUGDependency> dependencies = descriptorTarget.getGPUPTargetDependencies().getGPUGDependency();
                String value, type;
                for (GPUPTargetDependencies.GPUGDependency dependency : dependencies) {
                    value = dependency.getValue().toUpperCase().trim(); // Case-insensitive
                    type = dependency.getType();

                    if (type.equals("requiredFor")) {
                        requiredFor.add(value);
                    } else {
                        dependsOn.add(value);
                    }
                }
            }
             newTarget.setRequiredFor(requiredFor);
             newTarget.setDependsOn(dependsOn);
        };
    }

    private void createSerialSets( List <GPUPDescriptor.GPUPSerialSets.GPUPSerialSet> descriptorSerialSets) throws IllegalArgumentException {
        for (GPUPDescriptor.GPUPSerialSets.GPUPSerialSet serialSet : descriptorSerialSets)
        {
            Set <String> targetsName  = new HashSet<>();

            String serialTargets = serialSet.getTargets();

            String serialNames = serialSet.getName();

            if (this.serialSets.get(serialNames) != null)
            {
                throw new IllegalArgumentException("Error! There are 2 serial sets with the same name. the name is:" + serialNames);
            }

            String[] targetsNameArr = serialTargets.split(",", 0);

            for (String name : targetsNameArr) {
                targetsName.add(name.toUpperCase().trim());  // case insensitive

                if (graphData.get(name) == null) {
                    throw new IllegalArgumentException("Error in serial name: " + serialNames + "! there is no target with name: " + name);
                }
                // exception if there is a target that not exist is graph data
                graphData.get(name).addToSerialSets(serialNames);
            }

            this.serialSets.put(serialNames, new SerialSet(serialNames, targetsName));
        }
    }

    // Complete dependencies (sometimes we get only dependsOn/RequiredFor and need to complete the rest)
    private void dependencyCompletion () throws NoSuchElementException {

        graphData.forEach((name , target) -> {
            target.getDependsOn().forEach((targetName) -> {

                if (!graphData.containsKey(targetName))
                    throw new NoSuchElementException( "Error in dependencies of " + name + ": "+ targetName + " does not exist.");

                this.graphData.get(targetName).addToRequiredFor(name);
            });

            target.getRequiredFor().forEach((targetName) -> {

                if (!graphData.containsKey(targetName))
                    throw new NoSuchElementException( "Error in dependencies of " + name + ": "+ targetName + " does not exist.");

                this.graphData.get((targetName)).addToDependsOn(name);
            });
        });
    }

    private void createName2dependsOn () {
        graphData.forEach((name, target) -> {
            name2dependsOn.put(name, target.getDependsOn());
        });
    }

    private void checkDualDependencies () throws IllegalArgumentException {

        graphData.forEach( (name, target) -> {
            target.getDependsOn().forEach((dependsTarget) -> {
                if (graphData.get(dependsTarget).getDependsOn().contains(name))
                    throw new IllegalArgumentException("Dual dependencies was found: " + name + " depends on " + dependsTarget + ", and " + dependsTarget + " depends on " + name );
            });

            target.getRequiredFor().forEach((dependsTarget) -> {
                if (graphData.get(dependsTarget).getRequiredFor().contains(name))
                    throw new IllegalArgumentException("Dual dependencies was found: " + name + " is required for " + dependsTarget + ", and " + dependsTarget + " is required for " + name );
            });
        });
    }
}

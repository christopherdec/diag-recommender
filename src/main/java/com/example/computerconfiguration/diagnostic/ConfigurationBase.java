package com.example.computerconfiguration.diagnostic;

import com.example.computerconfiguration.base.ConfigurationLoader;
import com.example.computerconfiguration.domain.CompleteConfiguration;
import com.example.computerconfiguration.domain.SolverModel;
import com.example.computerconfiguration.domain.car.Car;
import com.example.computerconfiguration.domain.computer.Computer;
import org.chocosolver.solver.constraints.Constraint;

import java.util.AbstractMap.SimpleEntry;
import java.util.*;

/**
 * This class calculate and store the complete configurations base.
 * It provides some query methods, used by the PDiagAdvisor when calculating diagnoses
 * with the recommendation by similarity activated.
 * Based on Felfernig, A. et al - "Personalized Diagnosis for Over-Constrained Problems".
 */
public class ConfigurationBase {

    /* used for debugging purposes.
     * 0 = no print
     * 1 = print similarity table with attribute-level similarity values
     * 2 = print similarity table with attribute names
     */
    private static final int PRINT_MODE = 0;

    private static List<? extends CompleteConfiguration> configurations = new ArrayList<>();

    private static Class<? extends CompleteConfiguration> domainClass;

    public ConfigurationBase(List<Constraint> userRequirements, ConfigurationLoader loader) {

        // loads the complete configuration list
        configurations = loader.getConfigurations();
        ConfigurationBase.domainClass = configurations.get(0).getClass();

        // loades the choco model version of the domain class
        SolverModel solverModel = loader.getDomainClass();

        // calculates the current configuration variables defined by user requirements AND propagations
        Map<String, Integer> variables = solverModel.getVariables(userRequirements);

        if (PRINT_MODE > 0) {
            printHeader(userRequirements, variables);
        }
        double attributeSimilarity;
        double totalSimilarity;
        int session = 1;
        String identation = " ";

        for (CompleteConfiguration configuration : configurations) {
            totalSimilarity = 0;

            if(PRINT_MODE > 0) {
                if(session == 10) { identation = ""; }
                System.out.printf("|     s%d    %s", session++, identation);
            }

            for (Map.Entry<String, Integer> variable : variables.entrySet()) {
                try {
                    int entryValue = (int) domainClass.getDeclaredField(variable.getKey()).get(configuration);

                    AttributeInfo attributeInfo = domainClass.getDeclaredField(variable.getKey())
                            .getAnnotation(AttributeInfo.class);

                    attributeSimilarity = SimilarityMetrics.calculate(variable, entryValue, attributeInfo);

                    totalSimilarity += attributeSimilarity * attributeInfo.weight();

                    if (PRINT_MODE == 1) {
                        printStuff(String.valueOf(entryValue));
                    } else if (PRINT_MODE == 2) {
                        String entryValueStr = Computer.stringifyAttributeValue(variable.getKey(), entryValue);
                        printStuff(entryValueStr);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Failed getting declared field from configurations base, got" +
                            "exception: " + e.getClass().toString(), e);
                }
            }
            configuration.setSimilarity(totalSimilarity);

            if (PRINT_MODE > 0) {
                System.out.printf("|     %.3f    |\n", totalSimilarity);
            }
        }
        if (PRINT_MODE > 0) {
            System.out.println();
        }
    }

    private List<CompleteConfiguration> filterConfigurations(List<Constraint> path) {
        // creates a copy of the configurations base, to be used in this query
        List<CompleteConfiguration> filteredConfigurations = new ArrayList<>(configurations);
        path.forEach(ur -> {
            SimpleEntry<String, Integer> userRequirement = extractAttribute(ur);

            // filters the entry if it's value is equal to the user requirement
            configurations.forEach(entry -> {
                try {
                    if(domainClass.getDeclaredField(userRequirement.getKey()).get(entry)
                            .equals(userRequirement.getValue())) {
                        filteredConfigurations.remove(entry);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Failed to get declared field from configurations base", e);
                }
            });
        });
        return filteredConfigurations;
    }

    // selects max(similarity) from the table while filtering attributes values of the graph path constraints
    public double maxSimilarityQuery(List<Constraint> path) {
        double maxSimilarity;
        try {
            maxSimilarity = Collections.max(filterConfigurations(path), Comparator
                    .comparing(CompleteConfiguration::getSimilarity)).getSimilarity();
        } catch (NoSuchElementException e) {
            maxSimilarity = 0d;
        }
        return maxSimilarity;
    }

    public int maxReplicationsQuery(List<Constraint> path) {
        int maxReplications;
        try {
            maxReplications = Collections.max(filterConfigurations(path), Comparator
                .comparing(CompleteConfiguration::getReplications)).getReplications();
        } catch (NoSuchElementException e) {
            maxReplications = 0;
        }
        return maxReplications;
    }

    public double utilityQuery(List<Constraint> path) {
        double sum = 0;
        for (Constraint ur : path) {
            String userRequirementName = extractAttribute(ur).getKey();
            try {
                sum += 100 * domainClass.getField(userRequirementName).getAnnotation(AttributeInfo.class).weight();
            } catch (NoSuchFieldException e) {
                throw new RuntimeException("Failed to get declared field from configuration class", e);
            }
        }
        return 1/sum;
    }

    // PRINTING METHODS

    private void printHeader(List<Constraint> userRequirements, Map<String, Integer> variables) {
        System.out.println("User Requirements:");
        List<String> userReqAttributeNames = new ArrayList<>();

        for (Constraint userRequirement : userRequirements) {
            String name = extractAttribute(userRequirement).getKey();
            userReqAttributeNames.add(name);
            printStuff(name);
        }
        System.out.println("|");
        for (Constraint userRequirement : userRequirements) {
            String entryValueStr;
            if (configurations.get(0) instanceof Computer) {
                entryValueStr = Computer.stringifyAttributeValue(extractAttribute(userRequirement).getKey(), extractAttribute(userRequirement).getValue());
            } else if (configurations.get(0) instanceof Car){
                entryValueStr = Car.stringifyAttributeValue(extractAttribute(userRequirement).getKey(), extractAttribute(userRequirement).getValue());
            }
            else {
                entryValueStr = String.valueOf(extractAttribute(userRequirement).getValue());
            }
            printStuff(entryValueStr);
        }
        Map<String, Integer> propagatedVariables = new HashMap<>();
        for (Map.Entry<String, Integer> variable : variables.entrySet()) {
            if(!userReqAttributeNames.contains(variable.getKey())) {
                propagatedVariables.put(variable.getKey(), variable.getValue());
            }
        }
        if (!propagatedVariables.isEmpty()) {
            System.out.print("|\nPropagated variables:\n");
            for (Map.Entry<String, Integer> propagatedVariable : propagatedVariables.entrySet()) {
                printStuff(propagatedVariable.getKey());
            }
            System.out.println("|");
            for (Map.Entry<String, Integer> propagatedVariable : propagatedVariables.entrySet()) {
                printStuff(String.valueOf(propagatedVariable.getValue()));
            }
        }
        System.out.print("|\nSimilarity Table:\n");
        System.out.print("| Session si ");
        for (Map.Entry<String, Integer> variable : variables.entrySet()) {
            printStuff(variable.getKey());
        }
        System.out.println("| similarity   |");
    }

    private void printStuff(String stuff) {
        System.out.printf("|%s", stuff);
        int indentation = 15 - stuff.length();
        if (indentation > 0) {
            System.out.printf("%1$" + indentation + "s", "");
        }
    }

    public SimpleEntry<String, Integer> extractAttribute(Constraint constraint) {
        String[] split = constraint.toString().split(" = ");
        String name = split[0].split("\\[")[1];
        Integer value = Integer.parseInt(split[1].split("\\]")[0]);
        return new SimpleEntry<>(name, value);
    }

}

package com.example.computerconfiguration.diagnostic;

import com.example.computerconfiguration.base.ConfigurationLoader;
import com.example.computerconfiguration.domain.AttributeInfo;
import com.example.computerconfiguration.domain.Configuration;
import com.example.computerconfiguration.domain.ConfigurationModel;
import com.example.computerconfiguration.domain.car.Car;
import com.example.computerconfiguration.domain.computer.Computer;
import lombok.SneakyThrows;
import org.chocosolver.solver.constraints.Constraint;

import java.util.AbstractMap.SimpleEntry;
import java.util.*;

/**
 * Similarity table which considers user requirements AND propagation-defined values
 */
public class SimilarityTable {

    // defines if should print the similarity values (construction mode) or the attribute names
    private static final boolean CONSTRUCTION_MODE = false;

    private static List<? extends Configuration> configurations;

    public SimilarityTable(List<Constraint> userRequirements, ConfigurationLoader loader) throws
            NoSuchFieldException, IllegalAccessException {

        // loads the complete configuration list
        configurations = loader.getConfigurations();

        // loads info about name, min/max values and weight about the domain class attributes
        Map<String, AttributeInfo> attributeInfo = Configuration.getAttributeInfo();

        // loades the choco model version of the domain class
        ConfigurationModel configurationModel = loader.getDomainClass();

        // loads the variables defined by user requirements AND propagation
        Map<String, Integer> variables = configurationModel.getVariables(userRequirements);

        double attributeSimilarity;
        double totalSimilarity;

        printHeader(userRequirements, variables);

        int session = 1;
        String identation = " ";

        for (Configuration configuration : configurations) {

            totalSimilarity = 0;

            if(session == 10) { identation = ""; }
            System.out.printf("|     s%d    %s", session++, identation);

            for (Map.Entry<String, Integer> variable : variables.entrySet()) {

                int entryValue = (int) configuration.getClass().getDeclaredField(variable.getKey()).get(configuration);

                attributeSimilarity = SimilarityMetrics.calculateSimilarity(variable, entryValue, attributeInfo);

                if (configuration instanceof Computer && !CONSTRUCTION_MODE) {
                    String entryValueStr = Computer.stringifyAttributeValue(variable.getKey(), entryValue);
                    printStuff(entryValueStr);
                } else {
                    printStuff(String.valueOf(entryValue));
                }
                totalSimilarity += attributeSimilarity * attributeInfo.get(variable.getKey()).weight;
            }
            configuration.setSimilarity(totalSimilarity);

            System.out.printf("|     %.3f    |\n", totalSimilarity);
        }

        System.out.println();
    }

    // selects max(similarity) from the table while filtering attributes values of the graph path constraints
    @SneakyThrows
    public double getMaxSimilarityValue(List<Constraint> path) {
        // creates a copy of the configurations, to be consumed in this query
        List<Configuration> filteredConfigurations = new ArrayList<>(configurations);

        path.forEach(constraint -> {
            String attribute = extractAttribute(constraint).getKey();
            Integer value = extractAttribute(constraint).getValue();

            // filters the entry if the attribute value is equal to the current user requirement
            configurations.forEach(entry -> {
                try {
                    if(entry.getClass().getDeclaredField(attribute).get(entry).equals(value)) {
                        filteredConfigurations.remove(entry);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
        // from the filtered configurations, returns the max similarity value found (or 0 if table is empty)
        try {
            return Collections.max(filteredConfigurations, Comparator.comparing(Configuration::getSimilarity)).getSimilarity();
        } catch (NoSuchElementException e) {
            return 0d;
        }
    }

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
        if (CONSTRUCTION_MODE) {
            System.out.print("|\nSimilarity Table Construction:\n");
        } else {
            System.out.print("|\nSimilarity Table:\n");
        }
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

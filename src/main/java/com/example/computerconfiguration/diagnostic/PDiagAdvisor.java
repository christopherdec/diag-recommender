package com.example.computerconfiguration.diagnostic;

import com.example.computerconfiguration.base.ComputerLoader;
import com.example.computerconfiguration.base.ConfigurationLoader;
import lombok.SneakyThrows;
import org.chocosolver.solver.constraints.Constraint;

import java.util.Comparator;
import java.util.List;

public class PDiagAdvisor implements Comparator<List<Constraint>> {

    private final SimilarityTable similarityTable;

    private static ConfigurationLoader loader;

    public static void setDomain(ConfigurationLoader loader) {
        PDiagAdvisor.loader = loader;
    }

    @SneakyThrows
    public PDiagAdvisor(List<Constraint> userRequirements) {
        if (loader == null) {
            loader = ComputerLoader.getInstance();
        }
        similarityTable = new SimilarityTable(userRequirements, loader);
    }

    @Override
    public int compare(List<Constraint> path1, List<Constraint> path2) {
        try {
            double maxSimilarityValuePath1 = similarityTable.getMaxSimilarityValue(path1);

            double maxSimilarityValuePath2 = similarityTable.getMaxSimilarityValue(path2);

            System.out.print("Path 1: not{ ");
            path1.forEach(cstr -> System.out.printf("%s ", cstr.toString()));
            System.out.printf("}, similarity = %.3f\n", maxSimilarityValuePath1);

            System.out.print("Path 2: not{ ");
            path2.forEach(cstr -> System.out.printf("%s ", cstr.toString()));
            System.out.printf("}, similarity = %.3f\n", maxSimilarityValuePath2);

            return (int) Math.signum(maxSimilarityValuePath2 - maxSimilarityValuePath1);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }

    @SneakyThrows
    public double getMaxSimilarityValue(List<Constraint> path) {
        return similarityTable.getMaxSimilarityValue(path);
    }
}

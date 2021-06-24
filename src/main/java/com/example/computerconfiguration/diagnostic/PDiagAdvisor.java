package com.example.computerconfiguration.diagnostic;

import com.example.computerconfiguration.base.ComputerLoader;
import com.example.computerconfiguration.base.ConfigurationLoader;
import org.chocosolver.solver.constraints.Constraint;

import java.util.Comparator;
import java.util.List;

/**
 * This implements the comparator which sorts the PriorityQueue instance used in the PDiag algorithm,
 * which effectively enables the calculation of diagnoses with heuristics.
 * The default approach is set to similarity-based recommendation.
 */
public class PDiagAdvisor implements Comparator<List<Constraint>> {

    private static final boolean PRINT = true;

    private static ConfigurationLoader loader;

    private final ComparisonApproach approach;

    private ConfigurationBase configurationBase;

    public static void setLoader(ConfigurationLoader loader) {
        PDiagAdvisor.loader = loader;
    }

    public PDiagAdvisor(List<Constraint> userRequirements, ComparisonApproach approach) {
        this.approach = approach;
        if (!approach.equals(ComparisonApproach.CONVENTIONAL)) {
            if (loader == null) {
                loader = ComputerLoader.getInstance();
            }
            configurationBase = new ConfigurationBase(userRequirements, loader);
        }
    }

    @Override
    public int compare(List<Constraint> path1, List<Constraint> path2) {
        double path1Result;
        double path2Result;
        switch (approach) {
            case SIMILARITY:
                path1Result = configurationBase.maxSimilarityQuery(path1);
                path2Result = configurationBase.maxSimilarityQuery(path2);
                break;
            case UTILITY:
                path1Result = configurationBase.utilityQuery(path1);
                path2Result = configurationBase.utilityQuery(path2);
                break;
            case QUANTITY:
                path1Result = configurationBase.maxReplicationsQuery(path1);
                path2Result = configurationBase.maxReplicationsQuery(path2);
                break;
            default:
                throw new RuntimeException("Approach '" + approach.name() + "' not defined in PDiagAdvisor");
        }
        if (PRINT) {
            System.out.print("Path 1: not{ ");
            path1.forEach(cstr -> System.out.printf("%s ", cstr.toString()));
            System.out.printf("} value = %.3f\nPath 2: not{ ", path1Result);
            path2.forEach(cstr -> System.out.printf("%s ", cstr.toString()));
            System.out.printf("} value = %.3f\n", path2Result);
        }
        return (int) Math.signum(path2Result - path1Result);
    }

    // used when instantiating diagnostics and for printing the PDiag search path
    public double getMaxSimilarityValue(List<Constraint> path) {
        return configurationBase.maxSimilarityQuery(path);
    }

}

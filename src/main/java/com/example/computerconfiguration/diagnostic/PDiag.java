package com.example.computerconfiguration.diagnostic;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;

import java.util.*;
import java.util.stream.Collectors;

public class PDiag {

    Model model;
    // Only useful for debugging purposes, should be removed once hstree is incorporated into a comprehensive csp framework
    QXHelper qxHelper;
    // stop condition for the algorithm
    static int maxDiagnosis = 99;

    public static void setMaxDiagnosis(int maxDiagnosis) {
        PDiag.maxDiagnosis = maxDiagnosis;
    }

    public PDiag(Model model, QXHelper qxHelper) {
        this.model = model;
        this.qxHelper = qxHelper;
    }

    public static PDiag getInstance(Model model, QXHelper qxHelper) {
        return new PDiag(model, qxHelper);
    }

    public List<Diagnostic> pDiag(List<Constraint> testConstraints, List<Constraint> auxiliaryConstraints, boolean withRecommendation) {
        return pDiag(new ArrayList<>(), testConstraints, auxiliaryConstraints, withRecommendation);
    }

    /**
     * PDiag algorithm, based on
     * - Felfernig, A., Schubert, M. and Reiterer, R. "Personalized Diagnosis for Over-Constrained Problems.",
     * which, by itself is based on hsDAG algorithm:
     * - Felfernig, Alexander, Lothar Hotz, Claire Bagley, and Juha Tiihonen, eds. Knowledge-Based Configuration:
     * From Research to Business Cases. Chapters 7 and 13. Amsterdam: Elsevier/MK, Morgan Kaufmann, 2014.
     * - Felfernig, A., M. Schubert, and C. Zehentner. “An Efficient Diagnosis Algorithm for Inconsistent Constraint Sets.”
     * Artificial Intelligence for Engineering Design, Analysis and Manufacturing 26, no. 1 (February 2012): 53–62.
     * https://doi.org/10.1017/S0890060411000011.
     *
     */
    public List<Diagnostic> pDiag(List<Constraint> backgroundConstraints, List<Constraint> userRequirements,
                                  List<Constraint> auxiliaryConstraints, boolean withRecommendation) {

        List<Diagnostic> diagnoses = new ArrayList<>();
        Queue<List<Constraint>> pathQueue;
        PDiagAdvisor advisor = null;
        int diagnosticNumber = 1;
        int conflictNumber = 1;
        List<Constraint> conflictSet = QuickXplain.getInstance(model, auxiliaryConstraints)
                                            .qx(backgroundConstraints, userRequirements);
        System.out.printf("Conflict Set %d: %s\n", conflictNumber++, conflictSet);
        if (withRecommendation) {
            advisor = new PDiagAdvisor(userRequirements);
            pathQueue = new PriorityQueue<>(advisor);

            pathQueue.addAll(conflictSet.stream().map(Arrays::asList).collect(Collectors.toList()));
        } else {
            pathQueue = conflictSet.stream().map(Arrays::asList).collect(Collectors.toCollection(LinkedList::new));
        }
        while (!pathQueue.isEmpty()) {

            if(diagnosticNumber > maxDiagnosis) { break; }

            List<Constraint> path = pathQueue.poll();

            System.out.printf("Selected path: not{ %s }\n", path);

            List<Constraint> testConstraintsMinusPath = userRequirements.stream().filter(c -> !path.contains(c)).collect(Collectors.toList());
            conflictSet = QuickXplain.getInstance(model, auxiliaryConstraints).qx(backgroundConstraints, testConstraintsMinusPath);

            if (conflictSet.isEmpty() && diagnoses.stream().map(Diagnostic::getPath).noneMatch(path::containsAll)) {

                System.out.printf("Diagnostic %d: %s\n", diagnosticNumber++, path);

                if (advisor != null) {
                    diagnoses.add(new Diagnostic(path, advisor.getMaxSimilarityValue(path)));
                } else {
                    diagnoses.add(new Diagnostic(path));
                }
            } else {
                if (conflictSet.isEmpty()) {
                    System.out.println("Path contained -> closing node");
                } else {
                    System.out.printf("Conflict Set %d: %s\n", conflictNumber++, conflictSet);
                }
                conflictSet.forEach(c -> {
                    List<Constraint> newPath = new ArrayList<>(path);
                    newPath.add(c);

                    System.out.println("Adding to path: " + c);

                    pathQueue.offer(newPath);
                });
            }
        }
        return diagnoses;
    }

}

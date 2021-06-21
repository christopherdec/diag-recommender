package com.example.computerconfiguration.domain;

import org.chocosolver.solver.constraints.Constraint;

import java.util.HashMap;
import java.util.List;

/**
 * While the CompleteConfiguration class is used for representing complete, static and
 * previously made configurations, the SolverModel is used to represent an on-going
 * configuration process, which variables consists on Choco-Solver classes.
 */
public abstract class SolverModel {

    public abstract HashMap<String, Integer> getVariables(List<Constraint> userRequirements);
}

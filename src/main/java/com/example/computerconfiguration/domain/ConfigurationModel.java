package com.example.computerconfiguration.domain;

import org.chocosolver.solver.constraints.Constraint;

import java.util.HashMap;
import java.util.List;

public interface ConfigurationModel {

    HashMap<String, Integer> getVariables(List<Constraint> userRequirements) throws NoSuchFieldException, IllegalAccessException;
}

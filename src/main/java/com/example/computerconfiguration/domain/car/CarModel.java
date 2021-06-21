package com.example.computerconfiguration.domain.car;

import com.example.computerconfiguration.domain.SolverModel;
import org.chocosolver.solver.constraints.Constraint;

import java.util.HashMap;
import java.util.List;

public class CarModel extends SolverModel {

    public CarModel() {
    }

    @Override
    public HashMap<String, Integer> getVariables(List<Constraint> userRequirements) {
        HashMap<String, Integer> variables = new HashMap<>();

        for (Constraint userReq : userRequirements) {
            String[] split = userReq.toString().split(" = ");
            String name = split[0].split("\\[")[1];
            Integer value = Integer.parseInt(split[1].split("\\]")[0]);

            variables.put(name, value);
        }
        return variables;
    }
}

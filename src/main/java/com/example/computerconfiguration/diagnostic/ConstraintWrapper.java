package com.example.computerconfiguration.diagnostic;

import org.chocosolver.solver.constraints.Constraint;

public class ConstraintWrapper {

    Constraint constraint;
    String description;

    public ConstraintWrapper(Constraint constraint, String description){
        this.constraint = constraint;
        this.description = description;
    }

    public Constraint getConstraint() {
        return constraint;
    }

    public void setConstraint(Constraint constraint) {
        this.constraint = constraint;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

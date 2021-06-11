package com.example.computerconfiguration.diagnostic;

import com.example.computerconfiguration.domain.computer.Computer;
import org.chocosolver.solver.constraints.Constraint;

import java.util.ArrayList;
import java.util.List;

public class Diagnostic {

    private final List<String> pathString = new ArrayList<>();
    private double relevance = 0;
    private final List<Constraint> path;

    public Diagnostic(List<Constraint> path, double relevance) {
        this.relevance = relevance;
        this.path = path;
    }

    public Diagnostic(List<Constraint> path) {
        this.path = path;
    }

    public List<String> getPathString() {
        return pathString;
    }

    public void addPathString(String userReq) {
        String[] split = userReq.split(" = ");
        if (split.length == 1) {
            pathString.add(userReq);
        } else {
            String valueStr = Computer.stringifyAttributeValue(split[0], Integer.valueOf(split[1]));
            pathString.add(split[0] + " = " + valueStr);
        }
    }

    // method used by Thymeleaf
    public String getRelevance() {
        return String.format("%.0f",relevance*100)+"%";
    }

    public List<Constraint> getPath() {
        return path;
    }

    @Override
    public String toString() {
        return pathString.toString();
    }

    // normalizes relevances of all diagnostics from a list
    public static List<Diagnostic> updateRelevances(List<Diagnostic> diagnostics) {
        double num_diagnostics = diagnostics.size();
        double relevance_sum = 0;
        for(Diagnostic d : diagnostics) {
            relevance_sum += d.relevance;
        }
        if (relevance_sum == 0) {
            diagnostics.forEach(d -> d.relevance = 1/num_diagnostics);
        } else {
            double finalRelevance_sum = relevance_sum;
            diagnostics.forEach(d -> d.relevance = d.relevance/finalRelevance_sum);
        }
        return diagnostics;
    }
}

package com.example.computerconfiguration.diagnostic;

import org.apache.commons.collections4.ListUtils;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class QXHelper {

    List<ConstraintWrapper> userRequirementContraints = new ArrayList<>();
    List<ConstraintWrapper> knowledgeBaseConstraints = new ArrayList<>();
    List<Constraint> auxiliaryConstraints;
    Model model;

    public QXHelper(Model model){
        this.model = model;
    }

    public void addKnowledgeBaseCstr(Constraint constraint){
        knowledgeBaseConstraints.add(new ConstraintWrapper(constraint, ""));
    }

    public void addUserRequirementCstr(Constraint constraint){
        userRequirementContraints.add(new ConstraintWrapper(constraint, ""));
    }

    public void addKnowledgeBaseCstr(Constraint constraint, String description){
        knowledgeBaseConstraints.add(new ConstraintWrapper(constraint, description));
    }

    public void addKnowledgeBaseCstr(List<ConstraintWrapper> constraints) {
        knowledgeBaseConstraints.addAll(constraints);
    }

    public void addUserRequirementCstr(Constraint constraint, String description){
        userRequirementContraints.add(new ConstraintWrapper(constraint, description));
    }

    public void addUserRequirementCstr(List<ConstraintWrapper> constraints) {
        userRequirementContraints.addAll(constraints);
    }

    public void addUserRequirementCstr(ConstraintWrapper constraintWrapper) {
        userRequirementContraints.add(constraintWrapper);
    }

    public void postConstraints() {
        getKnowledgeBaseCstrs().forEach(cstr -> {
            if (cstr.getStatus() != Constraint.Status.POSTED) {
                cstr.post();
            }
        });
        getUserRequerimentCstrs().forEach(cstr -> {
            if (cstr.getStatus() != Constraint.Status.POSTED) {
                cstr.post();
            }
        });
        // lists auxiliary constraints created by choco (that are not UR or KB constraints), to make sure they're not
        // disabled during QuickXPlain execution
        auxiliaryConstraints = new ArrayList<>(Arrays.asList(model.getCstrs()));
        auxiliaryConstraints.removeIf(cstr -> getUserRequerimentCstrs().contains(cstr) || getKnowledgeBaseCstrs().contains(cstr));
    }

    public List<String> findConflictingCstrs(){
        postConstraints();

        List<Constraint> conflictingConstraints = QuickXplain.getInstance(model, auxiliaryConstraints).qx(
                getKnowledgeBaseCstrs(),
                getUserRequerimentCstrs()
        );
        List<ConstraintWrapper> conflictingConstraintsWrappers = userRequirementContraints.stream().filter(c -> conflictingConstraints.contains(c.getConstraint())).collect(Collectors.toList());
        List<String> result = conflictingConstraintsWrappers.stream().map(ConstraintWrapper::getDescription).collect(Collectors.toList());
        System.out.println(result);
        return result;
    }

    public List<Diagnostic> findDiagnoses(boolean withRecommendation) {
        postConstraints();
        List<Diagnostic> diagnoses = PDiag.getInstance(model, this).pDiag(getKnowledgeBaseCstrs(), getUserRequerimentCstrs(),
                auxiliaryConstraints, withRecommendation);
        for(Diagnostic diagnostic : diagnoses) {
            for(ConstraintWrapper cw : userRequirementContraints) {
                if(diagnostic.getPath().contains(cw.getConstraint())) {
                    diagnostic.addPathString(cw.getDescription());
                }
            }
        }
        System.out.printf("Diagnostics: %s", diagnoses);
        return diagnoses;
    }

    public List<String> findConflictingCstrsWithEspoirImplementation(){
        postConstraints();

        List<Constraint> conflictingConstraints = QuickXplainByEspoir.quickXPlain(
                getKnowledgeBaseCstrs(),
                getUserRequerimentCstrs(),
                model
        );
        List<ConstraintWrapper> conflictingConstraintsWrappers = userRequirementContraints.stream().filter(c -> conflictingConstraints.contains(c.getConstraint())).collect(Collectors.toList());
        return conflictingConstraintsWrappers.stream().map(ConstraintWrapper::getDescription).collect(Collectors.toList());
    }

    public String getDescriptionByConstraintListofLists(List<List<Constraint>> qConstraint){
        return qConstraint.stream().map(this::getDescriptionByConstraintList).collect(Collectors.toList()).toString();
    }

    public String getDescriptionByConstraintList(List<Constraint> qConstraint){
        return qConstraint.stream().map(this::getDescriptionByConstraint).collect(Collectors.toList()).toString();
    }

    public String getDescriptionByConstraint(Constraint qConstraint){
        return ListUtils.union(knowledgeBaseConstraints, userRequirementContraints).stream().filter(c -> qConstraint.equals(c.getConstraint())).findFirst().get().getDescription();
    }

    public List<Constraint> getUserRequerimentCstrs() {
        return getUserRequirementContraints().stream().map(ConstraintWrapper::getConstraint).collect(Collectors.toList());
    }

    public List<ConstraintWrapper> getUserRequirementContraints() {
        return userRequirementContraints;
    }

    public void setUserRequirementContraints(List<ConstraintWrapper> userRequirementContraints) {
        this.userRequirementContraints = userRequirementContraints;
    }

    public List<Constraint> getKnowledgeBaseCstrs() {
        return getKnowledgeBaseConstraints().stream().map(ConstraintWrapper::getConstraint).collect(Collectors.toList());
    }

    public List<ConstraintWrapper> getKnowledgeBaseConstraints() {
        return knowledgeBaseConstraints;
    }

    public void setKnowledgeBaseConstraints(List<ConstraintWrapper> knowledgeBaseConstraints) {
        this.knowledgeBaseConstraints = knowledgeBaseConstraints;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }
}

package com.example.computerconfiguration.diagnostic;

import org.apache.commons.collections4.ListUtils;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;

import java.util.ArrayList;
import java.util.List;

public class QuickXplain {

    private Model model;

    private List<Constraint> auxiliaryConstraints;

    public QuickXplain(Model model, List<Constraint> auxiliaryConstraints){
        this.model = model;
        this.auxiliaryConstraints = auxiliaryConstraints;
    }

    public static QuickXplain getInstance(Model model, List<Constraint> auxiliaryConstraints) {
        return new QuickXplain(model, auxiliaryConstraints);
    }

    void refresh() {
        model.getSolver().reset();

        for(Constraint constraint : model.getCstrs()) {
            if (!auxiliaryConstraints.contains(constraint)) {
                constraint.setEnabled(false);
            }
        }
    }

    public List<Constraint> qx(List<Constraint> b, List<Constraint> c) {
        refresh();
        b.forEach(cstr -> cstr.setEnabled(true));
        c.forEach(cstr -> cstr.setEnabled(true));

        return c.isEmpty() || model.getSolver().solve() ? new ArrayList<>() : qx(new ArrayList<>(), c, b);
    }

    public List<Constraint> qx(List<Constraint> d, List<Constraint> c, List<Constraint> b) {

        if (!d.isEmpty()) {
            refresh();
            b.forEach(cstr -> cstr.setEnabled(true));
            if (!model.getSolver().solve()) {
                return new ArrayList<>();
            }
        }
        if (c.size() == 1) {
            return c;
        }
        List<Constraint> c1 = c.subList(0, (c.size() + 1) / 2);
        List<Constraint> c2 = c.subList((c.size() + 1) / 2, c.size());
        List<Constraint> cs1 = qx(c2, c1, ListUtils.union(b, c2));
        List<Constraint> cs2 = qx(cs1, c2, ListUtils.union(b, cs1));
        return ListUtils.union(cs1, cs2);
    }

}

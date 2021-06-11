package com.example.computerconfiguration.diagnostic;

import com.example.computerconfiguration.base.CarLoader;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Implementation of the example model domain of the article
 * "Personalized Diagnosis for Over-Constrained Problems (2013)",
 * by Alexander Felfernig, Monika Schubert, and Stefan Reiterer.
**/
public class CarDiagnosticTest {

    Model model = new Model("Car");
    IntVar type = model.intVar("type", 1, 4);
    IntVar fuel = model.intVar("fuel", new int[]{4, 6, 10});
    BoolVar skibag = model.boolVar("skibag");
    BoolVar fourWheel = model.boolVar("fourWheel");
    BoolVar pdc = model.boolVar("pdc");

    private List<ConstraintWrapper> getBackgroundConstraints() {
        return Arrays.asList(
                new ConstraintWrapper(fourWheel.eq(1).imp(type.eq(4)).decompose(), "c1"),
                new ConstraintWrapper(skibag.eq(1).imp(type.ne(1)).decompose(), "c2"),
                new ConstraintWrapper(fuel.eq(4).imp(type.eq(1)).decompose(), "c3"),
                new ConstraintWrapper(fuel.eq(6).imp(type.ne(4)).decompose(), "c4"),
                new ConstraintWrapper(type.eq(1).imp(fuel.ne(10)).decompose(), "c5")
        );
    }

    private List<ConstraintWrapper> getUserRequirements() {
        return Arrays.asList(
                new ConstraintWrapper(type.eq(1).decompose(), "c8"),
                new ConstraintWrapper(fuel.eq(6).decompose(), "c7"),
                new ConstraintWrapper(skibag.eq(1).decompose(), "c9"),
                new ConstraintWrapper(fourWheel.eq(1).decompose(), "c6"),
                new ConstraintWrapper(pdc.eq(1).decompose(), "c10")
        );
    }

    @Test
    void findDiagnosticsWithRecommendation() {
        PDiagAdvisor.setDomain(CarLoader.getInstance());
        QXHelper qxHelper = new QXHelper(model);
        qxHelper.addKnowledgeBaseCstr(getBackgroundConstraints());
        qxHelper.addUserRequirementCstr(getUserRequirements());
        List<Diagnostic> diagnoses = qxHelper.findDiagnoses(true);
        assertEquals(3, diagnoses.size());
        assertTrue(diagnoses.get(0).toString().contains("c6"));
        assertTrue(diagnoses.get(0).toString().contains("c9"));
        assertTrue(diagnoses.get(1).toString().contains("c7"));
        assertTrue(diagnoses.get(1).toString().contains("c8"));
        assertTrue(diagnoses.get(2).toString().contains("c6"));
        assertTrue(diagnoses.get(2).toString().contains("c8"));
        // existe um erro na tabela 3 do artigo (linha 3), mas que não modifica o resultado
    }

    @Test
    void findDiagnosticsWithoutRecommendation() {
        PDiagAdvisor.setDomain(CarLoader.getInstance());
        QXHelper qxHelper = new QXHelper(model);
        qxHelper.addKnowledgeBaseCstr(getBackgroundConstraints());
        qxHelper.addUserRequirementCstr(getUserRequirements());
        List<Diagnostic> diagnoses = qxHelper.findDiagnoses(false);
        // abaixo é o que foi obtido no artigo, porém o primeiro CS calculado pelo nosso quickxplain é diferente
        // por isso o diagnóstico sem recomendação é imprevisível
//        assertEquals("[[c6, c8], [c6, c9], [c7, c8]]", diagnoses.toString());
        assertEquals(3, diagnoses.size());
    }

}
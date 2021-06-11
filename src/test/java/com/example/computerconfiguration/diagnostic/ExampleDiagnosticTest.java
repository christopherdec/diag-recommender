package com.example.computerconfiguration.diagnostic;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.BoolVar;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class ExampleDiagnosticTest {

    private static final int FALSE = 0;
    /**
     * Working example based on Knowledge-Based Configuration: From Research to Business Cases, Hotz et. al (2014).
     * Chapter 7: Conflict Detection and Diagnosis in Configuration.
     */
    Model model = new Model("PC");
    BoolVar mb = model.boolVar("mb");
    BoolVar silverMB = model.boolVar("silverMb");
    BoolVar diamondMB = model.boolVar("diamondMb");
    BoolVar cpu = model.boolVar("cpu");
    BoolVar dCPU = model.boolVar("dCPU");
    BoolVar sCPU = model.boolVar("sCPU");

    @Test
    void solutionTest() {
        mb.iff(silverMB.or(diamondMB)).post();
        silverMB.xor(diamondMB).post();
        cpu.iff(dCPU.or(sCPU)).post();
        dCPU.xor(sCPU).post();

        sCPU.imp(diamondMB).post();
        sCPU.imp(silverMB).post();
        dCPU.add(silverMB).le(1).not().post();
        sCPU.add(diamondMB).le(1).not().post();
        dCPU.eq(FALSE).post();

        assertFalse(model.getSolver().solve());
    }

    @Test
    void qxTest() {
        QXHelper qxHelper = new QXHelper(model);

        qxHelper.addKnowledgeBaseCstr(mb.iff(silverMB.or(diamondMB)).decompose(), "ca");
        qxHelper.addKnowledgeBaseCstr(silverMB.xor(diamondMB).decompose(), "cb");
        qxHelper.addKnowledgeBaseCstr(cpu.iff(dCPU.or(sCPU)).decompose(), "cc");
        qxHelper.addKnowledgeBaseCstr(dCPU.xor(sCPU).decompose(), "cd");

        qxHelper.addUserRequirementCstr(sCPU.imp(diamondMB).decompose(), "c1");
        qxHelper.addUserRequirementCstr(sCPU.imp(silverMB).decompose(), "c2");
        qxHelper.addUserRequirementCstr(dCPU.add(silverMB).le(1).decompose(), "c3");
//        qxHelper.addTestCstr(sCPU.add(diamondMB).le(1).decompose(), "c4");
        qxHelper.addUserRequirementCstr(dCPU.eq(FALSE).decompose(), "c5");

//        assertEquals("[c1, c4, c5]", qxHelper.findConflictingCstrs().toString());
        assertEquals("[c1, c2, c5]", qxHelper.findConflictingCstrs().toString());
    }

    @Test
    void noConflictQxTest() {
        QXHelper qxHelper = new QXHelper(model);

        qxHelper.addKnowledgeBaseCstr(mb.iff(silverMB.or(diamondMB)).decompose(), "ca");
        qxHelper.addKnowledgeBaseCstr(silverMB.xor(diamondMB).decompose(), "cb");
        qxHelper.addKnowledgeBaseCstr(cpu.iff(dCPU.or(sCPU)).decompose(), "cc");
        qxHelper.addKnowledgeBaseCstr(dCPU.xor(sCPU).decompose(), "cd");

//        qxHelper.addTestCstr(sCPU.imp(diamondMB).decompose(), "c1");
        qxHelper.addUserRequirementCstr(sCPU.imp(silverMB).decompose(), "c2");
        qxHelper.addUserRequirementCstr(dCPU.add(silverMB).le(1).decompose(), "c3");
        qxHelper.addUserRequirementCstr(sCPU.add(diamondMB).le(1).decompose(), "c4");
        qxHelper.addUserRequirementCstr(dCPU.eq(FALSE).decompose(), "c5");

        assertEquals("[]", qxHelper.findConflictingCstrs().toString());
    }

    @Test
    void noBackgroundQxTest() {
        QXHelper qxHelper = new QXHelper(model);

        qxHelper.addUserRequirementCstr(mb.iff(silverMB.or(diamondMB)).decompose(), "ca");
        qxHelper.addUserRequirementCstr(silverMB.xor(diamondMB).decompose(), "cb");
        qxHelper.addUserRequirementCstr(cpu.iff(dCPU.or(sCPU)).decompose(), "cc");
        qxHelper.addUserRequirementCstr(dCPU.xor(sCPU).decompose(), "cd");

        qxHelper.addUserRequirementCstr(sCPU.imp(diamondMB).decompose(), "c1");
        qxHelper.addUserRequirementCstr(sCPU.imp(silverMB).decompose(), "c2");
        qxHelper.addUserRequirementCstr(dCPU.add(silverMB).le(1).decompose(), "c3");
        qxHelper.addUserRequirementCstr(sCPU.add(diamondMB).le(1).decompose(), "c4");
        qxHelper.addUserRequirementCstr(dCPU.eq(FALSE).decompose(), "c5");

        assertEquals("[cd, c1, c4, c5]", qxHelper.findConflictingCstrs().toString());
    }

    @Test
    void hsTreeTest() {
        QXHelper qxHelper = new QXHelper(model);

        qxHelper.addKnowledgeBaseCstr(mb.iff(silverMB.or(diamondMB)).decompose(), "ca");
        qxHelper.addKnowledgeBaseCstr(silverMB.xor(diamondMB).decompose(), "cb");
        qxHelper.addKnowledgeBaseCstr(cpu.iff(dCPU.or(sCPU)).decompose(), "cc");
        qxHelper.addKnowledgeBaseCstr(dCPU.xor(sCPU).decompose(), "cd");

        qxHelper.addUserRequirementCstr(sCPU.imp(diamondMB).decompose(), "c1");
        qxHelper.addUserRequirementCstr(sCPU.imp(silverMB).decompose(), "c2");
        qxHelper.addUserRequirementCstr(dCPU.add(silverMB).le(1).decompose(), "c3");
        qxHelper.addUserRequirementCstr(sCPU.add(diamondMB).le(1).decompose(), "c4");
        qxHelper.addUserRequirementCstr(dCPU.eq(FALSE).decompose(), "c5");

        assertEquals("[[c1], [c5], [c2, c4]]", qxHelper.findDiagnoses(false).toString());
    }

    @Test
    void qxByEspoirTest() {
        QXHelper qxHelper = new QXHelper(model);

        qxHelper.addKnowledgeBaseCstr(mb.iff(silverMB.or(diamondMB)).decompose(), "ca");
        qxHelper.addKnowledgeBaseCstr(silverMB.xor(diamondMB).decompose(), "cb");
        qxHelper.addKnowledgeBaseCstr(cpu.iff(dCPU.or(sCPU)).decompose(), "cc");
        qxHelper.addKnowledgeBaseCstr(dCPU.xor(sCPU).decompose(), "cd");

        qxHelper.addUserRequirementCstr(sCPU.imp(diamondMB).decompose(), "c1");
        qxHelper.addUserRequirementCstr(sCPU.imp(silverMB).decompose(), "c2");
        qxHelper.addUserRequirementCstr(dCPU.add(silverMB).le(1).decompose(), "c3");
        qxHelper.addUserRequirementCstr(sCPU.add(diamondMB).le(1).decompose(), "c4");
        qxHelper.addUserRequirementCstr(dCPU.eq(FALSE).decompose(), "c5");

        assertEquals("[c1, c2, c5]", qxHelper.findConflictingCstrsWithEspoirImplementation().toString());
    }

}

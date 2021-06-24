package com.example.computerconfiguration.diagnostic;

import com.example.computerconfiguration.base.ComputerLoader;
import com.example.computerconfiguration.domain.computer.*;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ComputerDiagnosticTest {

    private static final int TRUE = 1;
    private static final int FALSE = 0;

    Model model = new Model("Computer");
    IntVar uso = model.intVar("uso", 1, Uso.values().length);
    IntVar gabinete = model.intVar("gabinete", 1, Gabinete.values().length);
    IntVar processador = model.intVar("processador", 1, Processador.values().length);
    BoolVar driveOptico = model.boolVar("driveOptico");
    BoolVar placaVideo = model.boolVar("placaVideo");
    IntVar memoria = model.intVar("memoria", 1, Memoria.values().length);
    BoolVar hdd = model.boolVar("hdd");
    BoolVar ssd = model.boolVar("ssd");

    Solver solver = model.getSolver();

    private List<ConstraintWrapper> getBackgroundConstraints() {
        return Arrays.asList(
                new ConstraintWrapper(hdd.or(ssd).decompose(), "c1"),
                new ConstraintWrapper(uso.eq(Uso.BÁSICO.getValue()).imp(gabinete.ne(Gabinete.GAMER.getValue())
                        .and(processador.eq(Processador.DUAL_CORE.getValue()))
                        .and(memoria.eq(Memoria.QUATRO_GB.getValue()))).decompose(), "c2"),
                new ConstraintWrapper(uso.eq(Uso.DESENVOLVIMENTO.getValue()).imp(memoria.eq(Memoria.DEZESSEIS_GB.getValue())
                        .and(ssd)).decompose(), "c3"),
                new ConstraintWrapper(uso.eq(Uso.GAMES.getValue()).imp(placaVideo.and(processador.eq(Processador.QUAD_CORE.getValue()))
                        .and(memoria.ne(Memoria.QUATRO_GB.getValue()))).decompose(), "c4"),
                new ConstraintWrapper(gabinete.eq(Gabinete.MINI.getValue()).imp(placaVideo.not().and(hdd.xor(ssd))).decompose(), "c5"),
                new ConstraintWrapper(gabinete.eq(Gabinete.GAMER.getValue()).imp(driveOptico.not()).decompose(), "c6")
        );
    }

    private void printBounds() {
        System.out.println("Uso: [" + uso.getLB() + ", " + uso.getUB() + "]");
        System.out.println("Gabinete: [" + gabinete.getLB() + ", " + gabinete.getUB() + "]");
        System.out.println("Processador: [" + processador.getLB() + ", " + processador.getUB() + "]");
        System.out.println("Drive Óptico: [" + driveOptico.getLB() + ", " + driveOptico.getUB() + "]");
        System.out.println("Placa Vídeo: [" + placaVideo.getLB() + ", " + placaVideo.getUB() + "]");
        System.out.println("Memória: [" + memoria.getLB() + ", " + memoria.getUB() + "]");
        System.out.println("HDD: [" + hdd.getLB() + ", " + hdd.getUB() + "]");
        System.out.println("SSD: [" + ssd.getLB() + ", " + ssd.getUB() + "]");
        System.out.println();
    }

    @DisplayName("initial configuration should have all options available")
    @Test
    void newComputer() {
        getBackgroundConstraints().forEach(cw -> cw.getConstraint().post());

        assertDoesNotThrow(solver::propagate);

        System.out.printf("Number of possible valid configurations: %d\n", solver.findAllSolutions().size());

        printBounds();
    }

    @Test
    @DisplayName("c5 propagation test: set 'gabinete = mini', then 'ssd = true'")
    void c5propagationTest() {

        getBackgroundConstraints().forEach(wrapper -> wrapper.getConstraint().post());

        gabinete.eq(Gabinete.MINI.getValue()).post();

        assertDoesNotThrow(solver::propagate);

        assertEquals(Gabinete.MINI.getValue(), gabinete.getValue());
        // c5: não cabe placa de vídeo no gabinete mini
        assertEquals(FALSE, placaVideo.getValue());
        // c4: uso para games requer placa de vídeo
        assertFalse(gabinete.contains(Uso.GAMES.getValue()));

        ssd.eq(TRUE).post();

        assertDoesNotThrow(solver::propagate);

        assertEquals(TRUE, ssd.getValue());
        // c5: gabinete mini não cabe ssd e hdd ao mesmo tempo
        assertEquals(FALSE, hdd.getValue());
    }

    @Test
    public void simpleContradictionTest() {
        uso.eq(1).imp(gabinete.ne(3)).post();
        uso.eq(1).post();
        gabinete.eq(3).post();

        assertThrows(ContradictionException.class, () -> solver.propagate());
    }

    @Test
    public void contradictionTest() {
        getBackgroundConstraints().forEach(wrapper -> wrapper.getConstraint().post());
        uso.eq(Uso.BÁSICO.getValue()).post();
        gabinete.eq(Gabinete.GAMER.getValue()).post();
        placaVideo.eq(TRUE).post();

        assertThrows(ContradictionException.class, () -> solver.propagate());

        printBounds();
    }

    @Test
    public void qxHelperTest() {
        QXHelper qxHelper = new QXHelper(model);

        qxHelper.addKnowledgeBaseCstr(uso.eq(Uso.BÁSICO.getValue())
                .imp(gabinete.ne(Gabinete.GAMER.getValue())).decompose(), "c1");

        qxHelper.addUserRequirementCstr(uso.eq(Uso.BÁSICO.getValue()).decompose(), "r1");

        qxHelper.addUserRequirementCstr(gabinete.eq(Gabinete.GAMER.getValue()).decompose(), "r2");

        List<String> conflictingCstrs = qxHelper.findConflictingCstrs();

        assertEquals("[r1, r2]", conflictingCstrs.toString());

    }

    @Test
    public void conventionalDiagnosis() {
        QXHelper qxHelper = new QXHelper(model);
        qxHelper.addKnowledgeBaseCstr(getBackgroundConstraints());

        qxHelper.addUserRequirementCstr(uso.eq(Uso.BÁSICO.getValue()).decompose(), "r1");
        qxHelper.addUserRequirementCstr(gabinete.eq(Gabinete.GAMER.getValue()).decompose(), "r2");
        qxHelper.addUserRequirementCstr(placaVideo.eq(TRUE).decompose(), "r3");
        qxHelper.addUserRequirementCstr(driveOptico.eq(TRUE).decompose(), "r4");

        List<Diagnostic> diagnoses = qxHelper.findDiagnoses();

        assertFalse(diagnoses.isEmpty());
    }

    @Test
    public void similarityBasedDiagnosis() {

        QXHelper qxHelper = new QXHelper(model);
        qxHelper.addKnowledgeBaseCstr(getBackgroundConstraints());

        qxHelper.addUserRequirementCstr(uso.eq(Uso.BÁSICO.getValue()).decompose(), "r1");
        qxHelper.addUserRequirementCstr(gabinete.eq(Gabinete.GAMER.getValue()).decompose(), "r2");
        qxHelper.addUserRequirementCstr(placaVideo.eq(TRUE).decompose(), "r3");
        qxHelper.addUserRequirementCstr(driveOptico.eq(TRUE).decompose(), "r4");

        PDiag.setMaxDiagnosis(10);

        List<Diagnostic> diagnoses = qxHelper.findDiagnoses(ComparisonApproach.SIMILARITY);

        assertFalse(diagnoses.isEmpty());
    }

    @Test
    void similarityTableTest() {
        Constraint c1 = uso.eq(Uso.GAMES.getValue()).decompose();
        Constraint c2 = processador.eq(Processador.DUAL_CORE.getValue()).decompose();
        Constraint c3 = gabinete.eq(Gabinete.MINI.getValue()).decompose();
        Constraint c4 = driveOptico.eq(TRUE).decompose();

        List<Constraint> userRequirements = Arrays.asList(c1, c2, c3, c4);
        ConfigurationBase configurationBase = new ConfigurationBase(userRequirements, ComputerLoader.getInstance());

        assertEquals(0.375, configurationBase.maxSimilarityQuery(new ArrayList<>()));
        assertEquals(0.375, configurationBase.maxSimilarityQuery(Collections.singletonList(c1)));
        assertEquals(0.25, configurationBase.maxSimilarityQuery(Collections.singletonList(c2)));
        assertEquals(0.25, configurationBase.maxSimilarityQuery(Collections.singletonList(c3)));
        assertEquals(0.25, configurationBase.maxSimilarityQuery(Collections.singletonList(c4)));
        assertEquals(0.125, configurationBase.maxSimilarityQuery(Arrays.asList(c1, c2)));
        assertEquals(0.125, configurationBase.maxSimilarityQuery(Arrays.asList(c1, c2, c3)));
        assertEquals(0, configurationBase.maxSimilarityQuery(Arrays.asList(c1, c2, c3, c4)));
    }

    @Test
    public void interactiveConfigurationSimilarityDiagnosis() throws Throwable {
        // usuário faz a primeira seleção
        ComputerModel.addUserRequirement("uso", Uso.DESENVOLVIMENTO.getValue());

        // propagaria e veria que nenhuma contradição foi gerada
        ComputerModel computerModel = new ComputerModel();

        assertDoesNotThrow(() -> new ComputerModel().getSolver().propagate());

        // atualiza todos os botões, de acordo com o estado das variáveis. conferindo:
        assertTrue(ComputerModel.isUserRequirement("uso", Uso.DESENVOLVIMENTO.getValue()));
        assertTrue(ComputerModel.isPropagationResult("memoria", Memoria.DEZESSEIS_GB.getValue()));
        assertTrue(ComputerModel.isPropagationResult("ssd", TRUE));
        assertEquals(3, computerModel.gabinete.getDomainSize());
        assertFalse(computerModel.processador.isInstantiated());
        assertFalse(computerModel.driveOptico.isInstantiated());
        assertFalse(computerModel.placaVideo.isInstantiated());
        assertFalse(computerModel.hdd.isInstantiated());

        // usuario tenta redefinir 'uso' para 'básico', propagaria e nenhuma contradição é observada
        assertTrue(ComputerModel.addUserRequirement("uso", Uso.BÁSICO.getValue()));

        // usuario tenta escolher gabinete gamer, propagaria e entraria num estado de inconsistência
        assertFalse(ComputerModel.addUserRequirement("gabinete", Gabinete.GAMER.getValue()));

        // chamaria o cálculo de diagnósticos
        computerModel = new ComputerModel();
        QXHelper qxHelper = new QXHelper(computerModel.model);
        qxHelper.addKnowledgeBaseCstr(computerModel.knowledgeBaseConstraints);
        qxHelper.addUserRequirementCstr(computerModel.userRequirementConstraints);

        List<Diagnostic> diagnoses = qxHelper.findDiagnoses(ComparisonApproach.SIMILARITY);

        assertEquals(2, diagnoses.size());
        assertTrue(diagnoses.get(0).getPathString().contains("uso = básico"));
        assertTrue(diagnoses.get(1).getPathString().contains("gabinete = gamer"));
    }

    @Test // esta situação é usada como exemplo na minha monografia
    public void complexSimilarityBasedDiagnosis() {

        QXHelper qxHelper = new QXHelper(model);
        qxHelper.addKnowledgeBaseCstr(getBackgroundConstraints());

        Constraint r1 = hdd.eq(TRUE).decompose();
        Constraint r2 = driveOptico.eq(TRUE).decompose();
        Constraint r3 = memoria.eq(Memoria.QUATRO_GB.getValue()).decompose();
        Constraint r4 = uso.eq(Uso.GAMES.getValue()).decompose();
        Constraint r5 = gabinete.eq(Gabinete.MINI.getValue()).decompose();
        Constraint r6 = ssd.eq(TRUE).decompose();

        qxHelper.addUserRequirementCstr(r1, "r1");
        qxHelper.addUserRequirementCstr(r2, "r2");
        qxHelper.addUserRequirementCstr(r3, "r3");
        qxHelper.addUserRequirementCstr(r4, "r4");
        qxHelper.addUserRequirementCstr(r5, "r5");
        qxHelper.addUserRequirementCstr(r6, "r6");

        List<Diagnostic> diagnoses = qxHelper.findDiagnoses(ComparisonApproach.SIMILARITY);
        assertFalse(diagnoses.isEmpty());
    }

}

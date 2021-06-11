package com.example.computerconfiguration.domain.computer;

import com.example.computerconfiguration.domain.ConfigurationModel;
import com.example.computerconfiguration.exception.IncompleteConfigurationException;
import com.example.computerconfiguration.diagnostic.ConstraintWrapper;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.Variable;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ComputerModel implements ConfigurationModel {

    public static final List<String> attributeNames = Arrays.asList("uso", "gabinete", "processador", "driveOptico",
            "placaVideo", "memoria", "hdd", "ssd");

    public static Map<String, Integer> userRequirements = new HashMap<>();

    private static String lastSelection;
    private static Integer valueBackup;

    public Model model = new Model("Computer");
    public IntVar uso = model.intVar("uso", 1, Uso.values().length);
    public IntVar gabinete = model.intVar("gabinete", 1, Gabinete.values().length);
    public IntVar processador = model.intVar("processador", 1, Processador.values().length);
    public BoolVar driveOptico = model.boolVar("driveOptico");
    public BoolVar placaVideo = model.boolVar("placaVideo");
    public IntVar memoria = model.intVar("memoria", 1, Memoria.values().length);
    public BoolVar hdd = model.boolVar("hdd");
    public BoolVar ssd = model.boolVar("ssd");

    private final Solver solver = model.getSolver();

    private final Constraint c1 = hdd.or(ssd).decompose();
    private final Constraint c2 = uso.eq(1).imp(gabinete.ne(3).and(processador.eq(1))
            .and(memoria.eq(1))).decompose();
    private final Constraint c3 = uso.eq(2).imp(memoria.eq(3).and(ssd)).decompose();
    private final Constraint c4 = uso.eq(3).imp(placaVideo.and(processador.eq(2))
            .and(memoria.ne(1))).decompose();
    private final Constraint c5 = gabinete.eq(1).imp(placaVideo.not().and(hdd.xor(ssd))).decompose();
    private final Constraint c6 = gabinete.eq(3).imp(driveOptico.not()).decompose();

    public List<ConstraintWrapper> knowledgeBaseConstraints = Arrays.asList(
            new ConstraintWrapper(c1, "c1"),
            new ConstraintWrapper(c2, "c2"),
            new ConstraintWrapper(c3, "c3"),
            new ConstraintWrapper(c4, "c4"),
            new ConstraintWrapper(c5, "c5"),
            new ConstraintWrapper(c6, "c6")
    );

    public List<ConstraintWrapper> userRequirementConstraints = new ArrayList<>();

    public ComputerModel() {

        knowledgeBaseConstraints.forEach(cw -> cw.getConstraint().post());

        for(Map.Entry<String, Integer> userReq : userRequirements.entrySet()) {

            try {
                IntVar attribute = (IntVar) ComputerModel.class.getDeclaredField(userReq.getKey()).get(this);

                Constraint cstr = attribute.eq(userReq.getValue()).decompose();

                userRequirementConstraints.add(new ConstraintWrapper(cstr, userReq.getKey() + " = " + userReq.getValue()));

                cstr.post();

            }  catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean applySelection(String name, Integer value) {
        // se o atributo já foi escolhido com este valor, é para remover o atributo
        if(userRequirements.containsKey(name) && userRequirements.get(name).equals(value)) {
            removeUserRequirement(name);
            return true;
        }
        lastSelection = name;
        valueBackup = userRequirements.getOrDefault(name, null);
        return addUserRequirement(name, value);
    }

    public static boolean addUserRequirement(String name, Integer value) {
        userRequirements.put(name, value);
        return ComputerModel.propagate();
    }

    public static void removeUserRequirement(String name) {
        userRequirements.remove(name);
    }

    public static void removeUserRequirement(List<String> requirements) {
        for(String requirement : requirements) {
            String[] split = requirement.split(" = ");
            userRequirements.remove(split[0]);
        }
    }

    public static void removeLastUserRequirement() {
        if(valueBackup == null) {
            userRequirements.remove(lastSelection);
        } else {
            userRequirements.put(lastSelection, valueBackup);
        }
    }

    public static void removeAllUserRequirements() {
        userRequirements = new HashMap<>();
    }

    public static boolean propagate() {
        try {
            new ComputerModel().getSolver().propagate();
            return true;
        } catch (ContradictionException e) {
            return false;
        }
    }

    public Solver getSolver() {
        return solver;
    }

    // usada para definir a cor do botão no html
    public static boolean isUserRequirement(String name, Integer value) {
        if(userRequirements.containsKey(name)) {
            return userRequirements.get(name).equals(value);
        }
        return false;
    }

    // usada para definir a cor do botão no html
    public static boolean isPropagationResult(String name, Integer value) throws NoSuchFieldException, IllegalAccessException {
        ComputerModel computerModel = new ComputerModel();
        if(!isUserRequirement(name, value)) {
            try {
                computerModel.getSolver().propagate();

                IntVar attribute = (IntVar) ComputerModel.class.getDeclaredField(name).get(computerModel);

                return attribute.isInstantiatedTo(value);

            } catch (ContradictionException ignore) { }
        }
        return false;
    }

    // usada para definir a cor do botão no html
    public static boolean isInconsistent(String name, Integer value) {

        if(isUserRequirement(name, value)) {
            return false;
        }
        boolean propagationSuccess;

        if(userRequirements.containsKey(name)) {
            Integer valueBackup = userRequirements.get(name);
            propagationSuccess = addUserRequirement(name, value);
            addUserRequirement(name, valueBackup);
        } else {
            propagationSuccess = addUserRequirement(name, value);
            removeUserRequirement(name);
        }
        // is inconsistent if propagation failed
        return !propagationSuccess;
    }

    // usado para definir o status do botão de confirmar configuração no html
    public static boolean isComplete() {
        ComputerModel computerModel = new ComputerModel();
        try {
            computerModel.getSolver().propagate();
        } catch (ContradictionException ignore) { }
        for (Variable variable : computerModel.model.getVars()) {
            if (attributeNames.contains(variable.getName()) && !variable.isInstantiated()) {
                return false;
            }
        }
        return true;
    }

    // usado na construção da tabela de similaridade com URs + variáveis definidas por propagação
    public HashMap<String, Integer> getVariables(List<Constraint> userRequirements) {

        ComputerModel computerModel = new ComputerModel();
        HashMap<String, Integer> variables = new HashMap<>();

        try {
            computerModel.getSolver().propagate();
        } catch (ContradictionException ignore) { }

        // adiciona variáveis que foram definidas por user requirements
        for (Constraint userReq : userRequirements) {
            String[] split = userReq.toString().split(" = ");
            String name = split[0].split("\\[")[1];
            Integer value = Integer.parseInt(split[1].split("\\]")[0]);
            variables.put(name, value);
        }
        // adiciona variáveis instanciadas
        for (Variable variable : computerModel.model.getVars()) {
            // filtra variáveis que não estão no modelo
            if (attributeNames.contains(variable.getName()) && variable.isInstantiated()) {
                IntVar var = (IntVar) variable;
                variables.put(var.getName(), var.getValue());
            }
        }
        return variables;
    }

    public static Computer getStaticModel() {
        if (!ComputerModel.isComplete()) {
            throw new IncompleteConfigurationException();
        }
        ComputerModel computerModel = new ComputerModel();
        try {
            computerModel.getSolver().propagate();
        } catch (ContradictionException ce) {
            ce.printStackTrace();
        }
        Computer computer = new Computer();
        computer.setDriveOptico(computerModel.driveOptico.getValue());
        computer.setGabinete(computerModel.gabinete.getValue());
        computer.setHdd(computerModel.hdd.getValue());
        computer.setMemoria(computerModel.memoria.getValue());
        computer.setProcessador(computerModel.processador.getValue());
        computer.setPlacaVideo(computerModel.placaVideo.getValue());
        computer.setSsd(computerModel.ssd.getValue());
        computer.setUso(computerModel.uso.getValue());
        return computer;
    }

}

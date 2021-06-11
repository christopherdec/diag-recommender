package com.example.computerconfiguration.service;

import com.example.computerconfiguration.base.ComputerBase;
import com.example.computerconfiguration.diagnostic.Diagnostic;
import com.example.computerconfiguration.diagnostic.QXHelper;
import com.example.computerconfiguration.domain.computer.Computer;
import com.example.computerconfiguration.domain.computer.ComputerModel;
import com.example.computerconfiguration.repository.ComputerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ConfigurationService {

    @Autowired
    private ComputerRepository computerRepository;

    @Autowired
    private ComputerBase computerBase;

    private List<Diagnostic> diagnoses = new ArrayList<>();

    public List<Diagnostic> applySelection(String name, Integer value) {

        if(!ComputerModel.applySelection(name, value)) {
            ComputerModel computerModel = new ComputerModel();
            QXHelper qxHelper = new QXHelper(computerModel.model);
            qxHelper.addKnowledgeBaseCstr(computerModel.knowledgeBaseConstraints);
            qxHelper.addUserRequirementCstr(computerModel.userRequirementConstraints);
            diagnoses = qxHelper.findDiagnoses(true);
            return Diagnostic.updateRelevances(diagnoses);
        }
        return new ArrayList<>();
    }

    public void applyRepair(Integer option) {

        if(option.equals(0)) {
            ComputerModel.removeLastUserRequirement();
        } else {
            ComputerModel.removeUserRequirement(diagnoses.get(option-1).getPathString());
        }
    }

    public void resetModel() {
        ComputerModel.removeAllUserRequirements();
    }

    public Computer saveModel() {
        Computer computer = ComputerModel.getStaticModel();
        System.out.println(computer);
        Optional<Computer> found = ComputerBase.contains(computer);
        if (found.isPresent()) {
            computer = found.get();
            computer.addReplicacao();
        }
        computerRepository.save(computer);
        computerBase.update();
        return computer;
    }

    public List<Computer> getConfigurations() {
        return computerRepository.findAll();
    }
}

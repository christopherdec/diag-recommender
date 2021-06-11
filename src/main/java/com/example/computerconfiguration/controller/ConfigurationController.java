package com.example.computerconfiguration.controller;

import com.example.computerconfiguration.diagnostic.Diagnostic;
import com.example.computerconfiguration.domain.computer.Computer;
import com.example.computerconfiguration.domain.computer.ComputerModel;
import com.example.computerconfiguration.service.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ConfigurationController {

    private boolean conflict = false;

    private List<Diagnostic> diagnostics = new ArrayList<>();

    @Autowired
    ConfigurationService configurationService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("computer", new ComputerModel());
        model.addAttribute("conflict", conflict);
        model.addAttribute("diagnostics", diagnostics);
        return "index";
    }

    @PostMapping("/update")
    public String update(@RequestParam String name, @RequestParam Integer value) throws
            NoSuchMethodException, NoSuchFieldException, IllegalAccessException, InvocationTargetException {
        System.out.println("Attribute '" + name + "' was selected with value '" + value + "'");
        diagnostics = configurationService.applySelection(name, value);
        conflict = !diagnostics.isEmpty();
        return "redirect:/";
    }

    @PostMapping("/repair")
    public String repair(@RequestParam Integer option) {
        configurationService.applyRepair(option);
        conflict = false;
        return "redirect:/";
    }

    @PostMapping("/success")
    public String success(Model model) {
        Computer computer = configurationService.saveModel();
        model.addAttribute("computer", computer);
        return "success";
    }

    @PostMapping("/reset")
    public String reset() {
        configurationService.resetModel();
        return "redirect:/";
    }

    @GetMapping("/base")
    public String base(Model model) {
        model.addAttribute("computers", configurationService.getConfigurations());
        return "base";
    }

}

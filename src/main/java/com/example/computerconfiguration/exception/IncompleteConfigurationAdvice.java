package com.example.computerconfiguration.exception;

import com.example.computerconfiguration.controller.ConfigurationController;
import com.example.computerconfiguration.domain.computer.ComputerModel;
import com.example.computerconfiguration.service.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;

@ControllerAdvice
public class IncompleteConfigurationAdvice {

    @Autowired
    ConfigurationService configurationService;

    @ResponseBody
    @ExceptionHandler(IncompleteConfigurationException.class)
    @ResponseStatus(HttpStatus.I_AM_A_TEAPOT)
    public ModelAndView incompleteConfigurationHandler(){
        ModelAndView model = new ModelAndView("index");
        configurationService.resetModel();
        model.addObject("computer", new ComputerModel());
        model.addObject("conflict", false);
        model.addObject("diagnostics", new ArrayList());
        return model;
    }
}

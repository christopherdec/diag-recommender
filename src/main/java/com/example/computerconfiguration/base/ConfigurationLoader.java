package com.example.computerconfiguration.base;

import com.example.computerconfiguration.domain.CompleteConfiguration;
import com.example.computerconfiguration.domain.SolverModel;

import java.util.List;

/**
 * Interface for complete configurations base loaders
 */
public interface ConfigurationLoader {

    List<? extends CompleteConfiguration> getConfigurations();

    SolverModel getDomainClass();
}

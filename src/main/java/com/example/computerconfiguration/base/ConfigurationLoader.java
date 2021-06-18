package com.example.computerconfiguration.base;

import com.example.computerconfiguration.domain.CompleteConfiguration;
import com.example.computerconfiguration.domain.ConfigurationModel;

import java.util.List;

/**
 * Interface for complete configurations base loaders
 */
public interface ConfigurationLoader {

    List<? extends CompleteConfiguration> getConfigurations();

    ConfigurationModel getDomainClass();
}

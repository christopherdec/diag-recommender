package com.example.computerconfiguration.base;

import com.example.computerconfiguration.domain.Configuration;
import com.example.computerconfiguration.domain.ConfigurationModel;

import java.util.List;

/**
 * Interface for complete configurations base loaders
 */
public interface ConfigurationLoader {

    List<? extends Configuration> getConfigurations();

    ConfigurationModel getDomainClass();
}

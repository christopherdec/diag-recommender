package com.example.computerconfiguration.exception;

public class IncompleteConfigurationException extends RuntimeException {

    public IncompleteConfigurationException() {
        super("Configuration is not complete, because some variables are not instantiated");
    }
}

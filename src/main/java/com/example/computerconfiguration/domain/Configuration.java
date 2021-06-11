package com.example.computerconfiguration.domain;

import java.util.Map;

/**
 * Abstract configuration class, used to represent complete configurations
 */
public abstract class Configuration {

    private double similarity;

    private static Map<String, AttributeInfo> attributeInfo;

    protected Configuration(Map<String, AttributeInfo> attInfo) {
        attributeInfo = attInfo;
    }

    public double getSimilarity() {
        return similarity;
    }

    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }

    public static Map<String, AttributeInfo> getAttributeInfo() {
        return attributeInfo;
    }

}

package com.example.computerconfiguration.domain;

public class AttributeInfo {

    public double weight;
    public String measure;
    public double min;
    public double max;

    // for attributes that use metrics which considers min and max values
    public AttributeInfo(double weight, String measure, double min, double max) {
        this.weight = weight;
        this.measure = measure;
        this.min = min;
        this.max = max;
    }

    public AttributeInfo(double weight, String measure) {
        this.weight = weight;
        this.measure = measure;
    }
}

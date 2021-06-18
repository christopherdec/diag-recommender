package com.example.computerconfiguration.domain;

/**
 * Generic class used to represent an entry of a complete configurations base.
 */
public abstract class CompleteConfiguration {

    private double similarity;

    // stores the number of times a specific configuration has been made
    private int replications;

    protected CompleteConfiguration() {
        replications = 1;
    }

    protected CompleteConfiguration(int replications) {
        this.replications = replications;
    }

    public double getSimilarity() {
        return similarity;
    }

    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }

    public int getReplications() {
        return replications;
    }

    public void addReplication() {
        replications++;
    }

    // used by Thymeleaf
    public String getReplicationsString() {
        return String.valueOf(replications);
    }

}

package com.example.computerconfiguration.domain;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

/**
 * Generic class used to represent an entry of a complete configurations base.
 * Intended to be used by the ConfigurationBase class.
 * "similarity" is an attribute for storing the total similarity value, which
 * is calculated when performing similarity-based diagnosis;
 * "replications" stores the number of times a specific configuration has been
 * made, which is considered when performing quantity-based diagnosis;
 */
@MappedSuperclass
public abstract class CompleteConfiguration {

    @Transient
    private double similarity;

    // stores the number of times a specific configuration has been made
    private int replications = 1;

    protected CompleteConfiguration() {
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

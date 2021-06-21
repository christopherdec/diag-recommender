package com.example.computerconfiguration.diagnostic;

import java.util.Map;

/**
 * Static attribute-level similarity metrics for use during the construction of the similarity table.
 * Based on:
 * Atas, M. et al - "Towards Similarity-Aware Constraint-Based Recommendation", and
 * Felfernig, A. et al - "Personalized Diagnosis for Over-Constrained Problems".
 * Glossary:
 * eib: Equal-is-Better
 * nib: Nearer-is-Better
 * mib: More-is-Better
 * lib: Less-is-Better
 */
public final class SimilarityMetrics {

    private SimilarityMetrics() {
    }

    public static double calculate(Map.Entry<String, Integer> userReqAttribute, int entryValue,
                                   AttributeInfo attributeInfo) {
        switch (attributeInfo.metric()) {
            case "EIB":
                return SimilarityMetrics.eib(userReqAttribute.getValue(), entryValue);
            case "LIB":
                return SimilarityMetrics.lib(entryValue,
                        attributeInfo.minValue(), attributeInfo.maxValue());
            case "MIB":
                return SimilarityMetrics.mib(entryValue,
                        attributeInfo.minValue(), attributeInfo.maxValue());
            case "NIB":
                return SimilarityMetrics.nib(userReqAttribute.getValue(), entryValue,
                        attributeInfo.minValue(), attributeInfo.maxValue());
            default:
                throw new RuntimeException("Unexpected attribute-level similarity metric: " +
                        attributeInfo.metric());
        }
    }

    public static double eib(double userRequeriment, double entryValue) {
        return (userRequeriment == entryValue) ? 1 : 0;
    }

    public static double nib(double userRequirement, double entryValue, double min, double max) {
        return 1 - Math.abs(userRequirement - entryValue)/(max - min);
    }

    public static double mib(double entryValue, double min, double max) {
        return (entryValue - min)/(max - min);
    }

    public static double lib(double entryValue, double min, double max) {
        return (max - entryValue)/(max - min);
    }

}

package com.example.computerconfiguration.diagnostic;

import com.example.computerconfiguration.domain.AttributeInfo;

import java.util.Map;

/**
 * Utility class which provides the similarity metrics for
 * attribute-level similarity calculation
 */
public final class SimilarityMetrics {

    private SimilarityMetrics() {
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

    public static double calculateSimilarity(Map.Entry<String, Integer> userReqAttribute, int entryValue,
                                             Map<String, AttributeInfo> attributeInfo) {
        switch (attributeInfo.get(userReqAttribute.getKey()).measure) {
            case "EIB":
                return SimilarityMetrics.eib(userReqAttribute.getValue(), entryValue);
            case "LIB":
                return SimilarityMetrics.lib(entryValue,
                        attributeInfo.get(userReqAttribute.getKey()).min, attributeInfo.get(userReqAttribute.getKey()).max);
            case "MIB":
                return SimilarityMetrics.mib(entryValue,
                        attributeInfo.get(userReqAttribute.getKey()).min, attributeInfo.get(userReqAttribute.getKey()).max);
            case "NIB":
                return SimilarityMetrics.nib(userReqAttribute.getValue(), entryValue,
                        attributeInfo.get(userReqAttribute.getKey()).min, attributeInfo.get(userReqAttribute.getKey()).max);
            default:
                throw new IllegalStateException("Unexpected attribute measure value: " +
                        attributeInfo.get(userReqAttribute.getKey()).measure);
        }
    }
}

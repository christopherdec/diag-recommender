package com.example.computerconfiguration.diagnostic;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A comparable property is an annotation that provides meta-
 * information about product attributes, and is considered
 * when performing diagnosis ordering techniques.
 * "metric" refers to the similarity metric;
 * "weight" is the attribute importance weight;
 * "minValue" is the minimum attribute value;
 * "maxValue" is the maximal attribute value;
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AttributeInfo {

    String metric();

    double weight();

    double minValue() default 0;

    double maxValue() default 1;
}

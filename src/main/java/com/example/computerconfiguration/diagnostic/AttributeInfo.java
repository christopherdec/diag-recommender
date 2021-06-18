package com.example.computerconfiguration.diagnostic;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AttributeInfo {

    String metric();

    double weight();

    double minValue() default 0;

    double maxValue() default 1;
}

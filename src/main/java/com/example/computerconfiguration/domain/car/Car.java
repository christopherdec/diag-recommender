package com.example.computerconfiguration.domain.car;

import com.example.computerconfiguration.diagnostic.AttributeInfo;
import com.example.computerconfiguration.domain.CompleteConfiguration;

import java.util.HashMap;

public class Car extends CompleteConfiguration {

    @AttributeInfo(metric = "EIB", weight = 0.5)
    public int type;

    @AttributeInfo(metric = "LIB", weight = 0.05, minValue = 4, maxValue = 10)
    public int fuel;

    @AttributeInfo(metric = "EIB", weight = 0.1)
    public int skibag;

    @AttributeInfo(metric = "EIB", weight = 0.3)
    public int fourWheel;

    @AttributeInfo(metric = "EIB", weight = 0.05)
    public int pdc;

    public Car(int type, int fuel, int skibag, int fourWheel, int pdc) {
        this.type = type;
        this.fuel = fuel;
        this.skibag = skibag;
        this.fourWheel = fourWheel;
        this.pdc = pdc;
    }

    public static String stringifyAttributeValue(String name, Integer value) {
        String valueStr;
        switch (name) {
            case "type":
                if (value.equals(1)) {
                    valueStr = "city";
                } else if (value.equals(2)) {
                    valueStr = "limo";
                } else if (value.equals(3)) {
                    valueStr = "combi";
                } else {
                    valueStr = "xdrive";
                }
                break;
            case "fuel":
                if (value.equals(4)) {
                    valueStr = "4l";
                } else if (value.equals(6)) {
                    valueStr = "6l";
                } else {
                    valueStr = "10l";
                }
                break;
            default:
                if (value.equals(1)) {
                    valueStr = "yes";
                } else {
                    valueStr = "no";
                }
                break;
        }
        return valueStr;
    }

}

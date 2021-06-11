package com.example.computerconfiguration.domain.car;

import com.example.computerconfiguration.domain.AttributeInfo;
import com.example.computerconfiguration.domain.Configuration;

import java.util.HashMap;
import java.util.Map;

public class Car extends Configuration {

    public int type;
    public int fuel;
    public int skibag;
    public int fourWheel;
    public int pdc;

    public static final HashMap<String, AttributeInfo> attributeInfo = new HashMap<>();

    static {
        attributeInfo.put("type", new AttributeInfo(0.5, "EIB"));
        attributeInfo.put("fuel", new AttributeInfo(0.05, "LIB", 4.0, 10.0));
        attributeInfo.put("skibag", new AttributeInfo(0.1, "EIB"));
        attributeInfo.put("fourWheel", new AttributeInfo(0.3, "EIB"));
        attributeInfo.put("pdc", new AttributeInfo(0.05, "EIB"));
    }

    public Car(int type, int fuel, int skibag, int fourWheel, int pdc) {
        super(attributeInfo);
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

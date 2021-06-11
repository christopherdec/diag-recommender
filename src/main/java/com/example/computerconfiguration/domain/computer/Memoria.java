package com.example.computerconfiguration.domain.computer;

public enum Memoria {

    QUATRO_GB(1),
    OITO_GB(2),
    DEZESSEIS_GB(3);

    private final int value;

    Memoria(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

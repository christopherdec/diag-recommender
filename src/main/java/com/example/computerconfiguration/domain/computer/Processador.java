package com.example.computerconfiguration.domain.computer;

public enum Processador {

    DUAL_CORE(1),
    QUAD_CORE(2);

    private final int value;

    Processador(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

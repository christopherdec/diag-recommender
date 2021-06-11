package com.example.computerconfiguration.domain.computer;

public enum Uso {

    BÁSICO(1),
    DESENVOLVIMENTO(2),
    GAMES(3);

    private final int value;

    Uso(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
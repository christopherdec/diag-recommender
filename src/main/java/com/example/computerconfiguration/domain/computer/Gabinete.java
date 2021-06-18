package com.example.computerconfiguration.domain.computer;

public enum Gabinete {

    MINI(1),
    DESKTOP(2),
    GAMER(3);

    private final int value;

    Gabinete(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}

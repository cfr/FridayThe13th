package com.AustinPilz.FridayThe13th.Components;

public enum TrapType {
    Counselor("Counselor"), Jason("Jason");

    private final String fieldDescription;

    TrapType(String value) {
        fieldDescription = value;
    }

    public String getFieldDescription() {
        return fieldDescription;
    }
}
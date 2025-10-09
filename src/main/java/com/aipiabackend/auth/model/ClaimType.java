package com.aipiabackend.auth.model;

public enum ClaimType {

    GRADE("grade");

    private final String value;

    ClaimType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}

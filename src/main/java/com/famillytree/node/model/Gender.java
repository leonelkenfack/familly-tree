package com.famillytree.node.model;

public enum Gender {
    MALE("HOMME"),
    FEMALE("FEMME");

    private final String label;

    Gender(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
} 
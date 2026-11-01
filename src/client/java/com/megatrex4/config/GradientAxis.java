package com.megatrex4.config;

public enum GradientAxis {
    HORIZONTAL("horizontal"),
    VERTICAL("vertical");

    private final String translationKey;

    GradientAxis(String name) {
        this.translationKey = "screen.randomblockplacement.gradient.axis." + name;
    }

    public String translationKey() {
        return translationKey;
    }
}

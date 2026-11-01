package com.megatrex4.config;

public enum GradientType {
    LINEAR("linear"),
    RADIAL("radial"),
    SQUARE("square");

    private final String translationKey;

    GradientType(String name) {
        this.translationKey = "screen.randomblockplacement.gradient.type." + name;
    }

    public String translationKey() {
        return translationKey;
    }

    public GradientType next() {
        GradientType[] types = values();
        return types[(ordinal() + 1) % types.length];
    }
}

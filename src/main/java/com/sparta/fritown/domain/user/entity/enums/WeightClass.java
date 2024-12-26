package com.sparta.fritown.domain.user.entity.enums;

public enum WeightClass {
    LIGHTFLY("라이트플라이"),
    FLY("플라이"),
    BANTAM("밴텀"),
    FEATHER("페더"),
    LIGHT("라이트"),
    LIGHTWELTER("라이트웰터"),
    WELTER("웰터"),
    LIGHTMIDDLE("라이트미들"),
    MIDDLE("미들"),
    LIGHTHEAVY("라이트헤비"),
    HEAVY("헤비"),
    SUPERHEAVY("슈퍼헤비");

    private final String message;

    WeightClass(String message) {
        this.message = message;
    }
}

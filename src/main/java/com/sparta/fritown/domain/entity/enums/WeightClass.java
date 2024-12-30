package com.sparta.fritown.domain.entity.enums;

import com.sparta.fritown.global.exception.ErrorCode;
import com.sparta.fritown.global.exception.custom.ServiceException;

public enum WeightClass {
    LIGHTFLY("라이트플라이", 0, 49),
    FLY("플라이", 49, 52),
    BANTAM("밴텀", 52, 56),
    FEATHER("페더", 56, 60),
    LIGHT("라이트", 60, 64),
    LIGHTWELTER("라이트웰터", 64, 69),
    WELTER("웰터", 69, 75),
    LIGHTMIDDLE("라이트미들", 75, 81),
    MIDDLE("미들", 81, 91),
    LIGHTHEAVY("라이트헤비", 91, 100),
    HEAVY("헤비", 100, 110),
    SUPERHEAVY("슈퍼헤비", 110, Integer.MAX_VALUE);

    private final String message;
    private final int minWeight;
    private final int maxWeight;

    WeightClass(String message, int minWeight, int maxWeight) {
        this.message = message;
        this.minWeight = minWeight;
        this.maxWeight = maxWeight;
    }

    public String getMessage() {
        return message;
    }

    public int getMinWeight() {
        return minWeight;
    }

    public int getMaxWeight() {
        return maxWeight;
    }

    public static WeightClass fromWeight(int weight) {
        for (WeightClass weightClass : values()) {
            if (weight >= weightClass.minWeight && weight < weightClass.maxWeight) {
                return weightClass;
            }
        }
        throw ServiceException.of(ErrorCode.WEIGHT_NOT_ACCEPTABLE);
    }
}

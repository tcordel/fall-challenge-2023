package fr.tcordel.model;

import java.util.List;

public enum FishType {
    JELLY, FISH, CRAB;

    public static FishType[] FISH_TYPE_VALUES = FishType.values();

    public static List<FishType> FISH_ORDERED = List.of(CRAB, FISH, JELLY);
}

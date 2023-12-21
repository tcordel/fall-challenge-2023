package fr.tcordel.model;

import java.util.List;

public enum FishType {
    JELLY(1 * Game.HEIGHT / 4, 2 * Game.HEIGHT / 4), FISH(2 * Game.HEIGHT / 4, 3 * Game.HEIGHT / 4), CRAB(3 * Game.HEIGHT / 4, Game.HEIGHT);


    private final int upperLimit;
    private final int deeperLimit;

    public static FishType[] FISH_TYPE_VALUES = FishType.values();

    public static List<FishType> FISH_ORDERED = List.of(CRAB, FISH, JELLY);

	FishType(int upperLimit, int deeperLimit) {
		this.upperLimit = upperLimit;
		this.deeperLimit = deeperLimit;
	}

	public int getUpperLimit() {
        return upperLimit;
    }

    public int getDeeperLimit() {
        return deeperLimit;
    }
}

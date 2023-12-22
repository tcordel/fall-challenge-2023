package fr.tcordel.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GameEstimator {

	public final Set<Scan> allScans;
	Map<Scan, Integer> firstToScan = new HashMap<>();
	Map<Integer, Integer> firstToScanAllFishOfColor = new HashMap<>();
	Map<FishType, Integer> firstToScanAllFishOfType = new HashMap<>();

	public GameEstimator() {
		allScans = Arrays.stream(FishType.values())
			.flatMap(fishType -> IntStream.range(0, Game.COLORS_PER_FISH).mapToObj(i -> new Scan(fishType, i)))
			.collect(Collectors.toSet());

	}

	public int computeScanScore(Set<Scan> scans, int playerIndex) {
		int total = 0;
		for (Scan scan : scans) {
			total += scan.type.ordinal() + 1;
			if (firstToScan.get(scan) == null || firstToScan.get(scan).equals(playerIndex)) {
				firstToScan.put(scan, playerIndex);
				total += scan.type.ordinal() + 1;
			}
		}

		for (FishType type : FishType.values()) {
			if (playerScannedAllFishOfType(scans, type)) {
				total += Game.COLORS_PER_FISH;
				if (Objects.isNull(firstToScanAllFishOfType.get(type))
					|| Objects.equals(firstToScanAllFishOfType.get(type), playerIndex)) {
					firstToScanAllFishOfType.put(type, playerIndex);
					total += Game.COLORS_PER_FISH;
				}
			}
		}

		for (int color = 0; color < Game.COLORS_PER_FISH; ++color) {
			if (playerScannedAllFishOfColor(scans, color)) {
				total += FishType.values().length;
				if (Objects.isNull(firstToScanAllFishOfColor.get(color))
					|| Objects.equals(firstToScanAllFishOfColor.get(color), playerIndex)) {
					firstToScanAllFishOfColor.put(color, playerIndex);
					total += FishType.values().length;
				}
			}
		}
		return total;
	}

	public int computeEndGameScore(int playerIndex) {
		int total = 0;
		for (Scan scan : allScans) {
			total += scan.type.ordinal() + 1;
			if (firstToScan.get(scan) == null || firstToScan.get(scan).equals(playerIndex)) {
				firstToScan.put(scan, playerIndex);
				total += scan.type.ordinal() + 1;
			}
		}

		for (FishType type : FishType.values()) {
			total += Game.COLORS_PER_FISH;
			if (Objects.isNull(firstToScanAllFishOfType.get(type))
				|| Objects.equals(firstToScanAllFishOfType.get(type), playerIndex)) {
				firstToScanAllFishOfType.put(type, playerIndex);
				total += Game.COLORS_PER_FISH;
			}
		}

		for (int color = 0; color < Game.COLORS_PER_FISH; ++color) {
			total += FishType.values().length;
			if (Objects.isNull(firstToScanAllFishOfColor.get(color))
				|| Objects.equals(firstToScanAllFishOfColor.get(color), playerIndex)) {
				firstToScanAllFishOfColor.put(color, playerIndex);
				total += FishType.values().length;
			}
		}
		return total;
	}

	private boolean playerScannedAllFishOfType(Set<Scan> scans, FishType type) {
		for (int color = 0; color < Game.COLORS_PER_FISH; ++color) {
			if (!scans.contains(new Scan(type, color))) {
				return false;
			}
		}
		return true;
	}

	private boolean playerScannedAllFishOfColor(Set<Scan> scans, int color) {

		for (int i = 0; i < FishType.FISH_TYPE_VALUES.length; i++) {
			if (!scans.contains(new Scan(FishType.FISH_TYPE_VALUES[i], color))) {
				return false;
			}
		}
		return true;
	}

	public void reset() {
		firstToScan.clear();
		firstToScanAllFishOfColor.clear();
		firstToScanAllFishOfType.clear();
	}
}
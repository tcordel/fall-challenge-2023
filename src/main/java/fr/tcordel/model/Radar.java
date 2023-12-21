package fr.tcordel.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Radar {

	public List<Integer> topLeft = new ArrayList<>();
	public List<Integer> topRight = new ArrayList<>();
	public List<Integer> bottomRight = new ArrayList<>();
	public List<Integer> bottomLeft = new ArrayList<>();

	public Radar() {
	}

	public Radar(List<Integer> topLeft, List<Integer> topRight, List<Integer> bottomRight, List<Integer> bottomLeft) {
		this.topLeft = topLeft;
		this.topRight = topRight;
		this.bottomRight = bottomRight;
		this.bottomLeft = bottomLeft;
	}

	public void reset() {
		topLeft.clear();
		topRight.clear();
		bottomRight.clear();
		bottomLeft.clear();
	}

	public void populate(int creatureId, RadarDirection radar) {
		switch (radar) {
			case TL -> topLeft.add(creatureId);
			case TR -> topRight.add(creatureId);
			case BR -> bottomRight.add(creatureId);
			case BL -> bottomLeft.add(creatureId);
		}
	}

	public Radar forType(Map<Integer, Fish> fishes, FishType type) {
		return new Radar(
			topLeft.stream().filter(i -> fishes.containsKey(i) && fishes.get(i).type == type).toList(),
			topRight.stream().filter(i -> fishes.containsKey(i) && fishes.get(i).type == type).toList(),
			bottomRight.stream().filter(i -> fishes.containsKey(i) && fishes.get(i).type == type).toList(),
			bottomLeft.stream().filter(i -> fishes.containsKey(i) && fishes.get(i).type == type).toList()
		);
	}
}

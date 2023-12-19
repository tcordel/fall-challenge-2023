package fr.tcordel.model;

import java.util.ArrayList;
import java.util.List;

public class Radar {

	public List<Integer> topLeft = new ArrayList<>();
	public List<Integer> topRight = new ArrayList<>();
	public List<Integer> bottomRight = new ArrayList<>();
	public List<Integer> bottomLeft = new ArrayList<>();

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
}

package fr.tcordel.model;

import java.util.List;
import java.util.Set;

public class DownAndUp {

	private final Game game;
	public int leftIndex = 0;

	public DownAndUp(Game game) {
		this.game = game;
	}

	enum Strat {
		DOWN, UP;
	}

	Strat left = Strat.DOWN;
	Strat right = Strat.DOWN;

	public void process(Radar[] radars, Set<Integer> scans) {
		for (int i = 0; i < game.gamePlayers.get(0).drones.size(); i++) {
			Drone drone = game.gamePlayers.get(0).drones.get(i);
			Radar radar = radars[i];
			boolean isLeft = leftIndex == i;
			Strat newStrat = checkStrat(drone, isLeft ? left : right);
			if (isLeft ) {
				left = newStrat;
			} else {
				right = newStrat;
			}
			if (newStrat == Strat.UP) {
				System.out.printf(
					"MOVE %d %d %d %s%n", (int)drone.getX(),
					(int) drone.getY() - 1000,
					Math.random() > 0.20d ? 1 : 0,
					isLeft ? left : right
				);
				continue;
			}
			boolean goToCenter = isLeft;
			List<Integer> integers = isLeft ? radar.bottomLeft : radar.bottomRight;
			integers.removeAll(scans);
			for (Integer integer : integers) {
				if (game.fishesMap.containsKey(integer)) {
					goToCenter = !goToCenter;
					System.err.println("go to border for " + drone.id + " because : " + integer);
					break;
				}
			}
			RadarDirection rd = goToCenter ? RadarDirection.BR : RadarDirection.BL;
			System.err.println("direction " + drone.id + "-> " + rd);
			Vector vector = drone.pos.add(rd.getDirection());
			double toXborder = Math.min(vector.getX(), Math.abs(vector.getX() - Game.WIDTH));
			if (toXborder < 2200) {
				vector = new Vector(drone.getX(), drone.getY() + 1000);
			}
			System.out.printf(
				"MOVE %d %d %d %s%n", (int)vector.getX(),
									(int) vector.getY(),
									Math.random() > 0.20d ? 1 : 0,
				isLeft ? left : right
								);
		}
	}

	private Strat checkStrat(Drone drone, Strat strat) {
		return switch (strat) {
			case UP -> drone.getY() < 500 ? Strat.DOWN : Strat.UP;
			case DOWN -> drone.getY() > 7400 ? Strat.UP : Strat.DOWN;
		};
	}
}
